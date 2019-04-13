package xxx.joker.libs.repository.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.RepoEntity;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

class RepoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RepoHandler.class);
    private static final List<String> WRITE_METHODS = Arrays.asList("add", "addAll", "remove", "removeIf", "removeAll", "clear", "set");

    private final ReadWriteLock repoLock;

    private Map<Class<?>, HandlerDataSet> handlers;
    private Map<Long, RepoEntity> dataByID;
    private Map<Class<?>, List<Class<?>>> referenceMap;
    private final AtomicLong sequenceValue;

    RepoHandler(List<RepoDTO> dtoList, ReadWriteLock repoLock) {
        this.repoLock = repoLock;

        this.handlers = new HashMap<>();
        this.dataByID = new HashMap<>();
        this.referenceMap = new HashMap<>();
        this.sequenceValue = new AtomicLong(0L);

        initRepoHandler(dtoList);
    }

    public Set<RepoEntity> getDataSet(Class<?> entityClazz) {
        HandlerDataSet handler = handlers.get(entityClazz);
        return handler == null ? null : handler.getProxySet();
    }

    private void initRepoHandler(List<RepoDTO> dtoList) {
        Map<Long, List<RepoFK>> fkMap = new HashMap<>();
        for(RepoDTO dto : dtoList) {
            dto.getEntities().forEach(e -> dataByID.put(e.getEntityID(), e));
            fkMap.putAll(JkStreams.toMap(dto.getForeignKeys(), RepoFK::getFromID));
        }

        if(!dataByID.isEmpty()) {
            // 'setDependencies' does not create collection handlers, so after must be called 'initRepoFields'
            dataByID.values().forEach(e -> setDependencies(e, fkMap.get(e.getEntityID())));
            dataByID.values().forEach(this::initRepoFields);
            // Set sequence value
            sequenceValue.set(1L + getMaxUsedID());
        }

        for(RepoDTO dto : dtoList) {
            handlers.put(dto.getEClazz(), new HandlerDataSet(dto.getEntities()));
        }
    }

    private long getMaxUsedID() {
        return dataByID.keySet().stream().mapToLong(l -> l).max().orElse(-1);
    }

    private void initRepoFields(RepoEntity e) {
        ClazzWrapper rc = ClazzWrapper.get(e);

        // Apply field directives
        rc.getEntityFields().forEach(ef -> ef.applyDirectives(e));

        // Init collections
        for (FieldWrapper cf : rc.getEntityFields()) {
            if(cf.isCollection()) {
                Object value = cf.getValue(e);

                if(cf.isRepoEntityCollection()) {
                    List<RepoEntity> data = new ArrayList<>();
                    if(value != null) {
                        data.addAll((Collection<RepoEntity>) value);
                    }
                    setHandlerCollection(e, cf, data);

                } else {
                    if(value == null) {
                        setSimpleCollection(e, cf, Collections.emptyList());
                    }
                }
            }
        }
    }

    private void setHandlerCollection(RepoEntity e, FieldWrapper efield, Collection<RepoEntity> eDeps) {
        if(efield.isList()) {
            HandlerList handler = new HandlerList(eDeps);
            efield.setValue(e, handler.createProxyList());
        } else {
            HandlerSet handler = new HandlerSet(eDeps);
            efield.setValue(e, handler.createProxySet());
        }
    }

    private void setSimpleCollection(RepoEntity e, FieldWrapper cf, Collection<?> eDeps) {
        if(cf.isList()) {
            cf.setValue(e, new ArrayList<>(eDeps));
        } else {
            Object o = cf.isComparableFlatField() ? new TreeSet<>(eDeps) : new HashSet<>(eDeps);
            cf.setValue(e, o);
        }
    }

    private void setDependencies(RepoEntity e, List<RepoFK> fkList) {
        if(fkList != null && !fkList.isEmpty()) {
            ClazzWrapper rc = ClazzWrapper.get(e);
            Map<String, List<Long>> fkMap = JkStreams.toMap(fkList, RepoFK::getFieldName, RepoFK::getDepID);
            fkMap.forEach((k,v) -> {
                List<RepoEntity> deps = JkStreams.map(v, depID -> dataByID.get(depID));
                FieldWrapper cf = rc.getEntityField(k);
                if(cf.isCollection()) {
                    // now use simple collection, then call the method 'initRepoFields' that will create the handlers
                    setSimpleCollection(e, cf, deps);
                } else {
                    cf.setValue(e, deps.get(0));
                }
            });
        }
    }

    public List<RepoDTO> getRepoEntityDTOs() {
        List<RepoDTO> dtoList = new ArrayList<>();

        handlers.forEach((c,h) -> {
            RepoDTO dto = new RepoDTO(c);
            dtoList.add(dto);
            dto.setEntities(h.getEntities());
            for (RepoEntity e : h.getEntities()) {
                retrieveDepChilds(e).forEach((fname,edeps) -> edeps.forEach(ed ->
                        dto.getForeignKeys().add(new RepoFK(e.getEntityID(), fname, ed.getEntityID()))
                ));
            }
        });

        return dtoList;
    }

    private boolean addEntity(RepoEntity toAdd) {
        return addEntity(toAdd, null);
    }
    private boolean addEntity(RepoEntity toAdd, Collection<RepoEntity> insColl) {
        if(toAdd.getEntityID() != null) {
            if(insColl == null)  return false;
            return insColl.add(toAdd);
        }

        synchronized (sequenceValue) {
            ClazzWrapper.setEntityID(toAdd, sequenceValue.get());

            if (insColl != null) {
                boolean added = insColl.add(toAdd);
                if (!added) {
                    ClazzWrapper.setEntityID(toAdd, null);
                    return false;
                }
            }

            boolean added = handlers.get(toAdd.getClass()).getEntities().add(toAdd);
            if (!added) {
                if (insColl != null) {
                    insColl.remove(toAdd);
                }
                ClazzWrapper.setEntityID(toAdd, null);
                return false;
            }

            sequenceValue.getAndIncrement();
        }

        dataByID.put(toAdd.getEntityID(), toAdd);
        initRepoFields(toAdd);

        retrieveDepChilds(toAdd).values().stream().flatMap(List::stream).forEach(this::addEntity);

        LOG.debug("New entity added: {}", toAdd);

        return true;
    }

    private RepoEntity setEntityInList(RepoEntity e, List<RepoEntity> insList, int setPos) {
        if(e.getEntityID() == null) {
            if (!addEntity(e)) {
                return null;
            }
        }
        return insList.set(setPos, e);
    }

    private boolean removeEntity(RepoEntity toDel) {
        Long eID = toDel.getEntityID();
        if(eID == null) {
            return false;
        }

        // Remove from dataSet
        boolean res = handlers.get(toDel.getClass()).getEntities().remove(toDel);
        if(!res) {
            return false;
        }

        // Remove where referenced
        Map<Class<?>, List<FieldWrapper>> refMap = ClazzWrapper.getReferenceFields(toDel.getClass());
        refMap.forEach((c,l) -> {
            TreeSet<RepoEntity> eset = handlers.get(c).getEntities();
            l.forEach(fw -> {
                for (RepoEntity parent : eset) {
                    if(fw.isCollection()) {
                        Collection<RepoEntity> pcoll = (Collection<RepoEntity>) fw.getValue(parent);
                        pcoll.removeIf(el -> el.getEntityID() == eID);
                    } else {
                        fw.setValue(parent, null);
                    }
                }
            });
        });

        // Remove from dataByID
        dataByID.remove(eID);

        LOG.debug("Removed entity: {}", toDel);

        return true;
    }

    private Map<String, List<RepoEntity>> retrieveDepChilds(RepoEntity e) {
        Map<String, List<RepoEntity>> toRet = new HashMap<>();
        ClazzWrapper rc = ClazzWrapper.get(e);
        for(FieldWrapper cf : rc.getEntityFields()) {
            if(cf.isRepoEntityFlatField()) {
                Object value = cf.getValue(e);
                List<RepoEntity> childs = new ArrayList<>();
                if(cf.isCollection()) {
                    childs.addAll((Collection<RepoEntity>) value);
                } else if(value != null) {
                    childs.add((RepoEntity) value);
                }
                if(!childs.isEmpty()) {
                    toRet.put(cf.getFieldName(), childs);
                }
            }
        }
        return toRet;
    }

    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        Map<Class<T>, Set<T>> map = new HashMap<>();
        handlers.entrySet().forEach(e -> map.put((Class<T>) e.getKey(), (Set<T>) e.getValue().getProxySet()));
        return map;
    }

    public void clearDataSets() {
        // Remove entities without dependencies before
        List<Class<?>> toDelete = JkConvert.toList(handlers.keySet());
        List<Class<?>> delList = new ArrayList<>();
        Map<Class<?>, List<Class<?>>> refMap = JkStreams.toMapSingle(toDelete, c -> c, c -> JkConvert.toList(ClazzWrapper.getReferenceFields(c).keySet()));

        int prev = -1;
        while(!toDelete.isEmpty() && prev != delList.size()) {
            prev = delList.size();
            for (Class<?> toDel : toDelete) {
                List<Class<?>> refs = refMap.get(toDel);
                if (refs == null || refs.isEmpty()) {
                    delList.add(toDel);
                }
            }
            refMap.values().forEach(list -> list.removeIf(delList::contains));
            toDelete.removeAll(delList);
        }

        if(!toDelete.isEmpty()) {
            delList.addAll(toDelete);
        }

        JkStreams.distinct(delList).forEach(clazz -> getDataSet(clazz).clear());
    }

    private class HandlerDataSet implements InvocationHandler {
        private final Set<RepoEntity> proxySet;
        private final TreeSet<RepoEntity> entities;

        public HandlerDataSet(Collection<RepoEntity> data) {
            this.entities = new TreeSet<>(data);
            this.proxySet = createProxyDataSet();
        }

        private Set<RepoEntity> createProxyDataSet() {
            ClassLoader loader = TreeSet.class.getClassLoader();
            Class[] interfaces = {Set.class};
            return (Set<RepoEntity>) Proxy.newProxyInstance(loader, interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Lock actualLock = WRITE_METHODS.contains(methodName) ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();

                if ("add".equals(methodName)) {
                    RepoEntity e = (RepoEntity) args[0];
                    return addEntity(e);
                }

                if ("addAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity e : coll) {
                        res |= addEntity(e);
                    }
                    return res;
                }

                if ("remove".equals(methodName)) {
                    RepoEntity e = (RepoEntity) args[0];
                    return removeEntity(e);
                }

                if ("removeIf".equals(methodName)) {
                    Predicate<RepoEntity> pred = (Predicate<RepoEntity>) args[0];
                    List<RepoEntity> toremove = JkStreams.filter(entities, pred);
                    if (toremove.isEmpty()) return false;
                    boolean res = false;
                    for (RepoEntity todel : toremove) {
                        res |= removeEntity(todel);
                    }
                    return res;
                }

                if ("removeAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity todel : coll) {
                        res |= removeEntity(todel);
                    }
                    return res;
                }

                if ("clear".equals(methodName)) {
                    List<RepoEntity> all = JkConvert.toList(entities);
                    boolean res = false;
                    for (RepoEntity todel : all) {
                        res |= removeEntity(todel);
                    }
                    if(res) {
                        synchronized (sequenceValue) {
                            sequenceValue.set(1L + getMaxUsedID());
                        }
                    }
                    return res;
                }

                return method.invoke(entities, args);

            } finally {
                actualLock.unlock();
            }
        }

        public Set<RepoEntity> getProxySet() {
            return proxySet;
        }

        public <T extends RepoEntity> TreeSet<T> getEntities() {
            return (TreeSet<T>) entities;
        }
    }

    private class HandlerSet implements InvocationHandler {
        private final TreeSet<RepoEntity> sourceSet;

        protected HandlerSet(Collection<RepoEntity> sourceData) {
            this.sourceSet = new TreeSet<>(sourceData);
        }

        protected Set<RepoEntity> createProxySet() {
            ClassLoader loader = TreeSet.class.getClassLoader();
            Class[] interfaces = {Set.class};
            return (Set<RepoEntity>) Proxy.newProxyInstance(loader, interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Lock actualLock = WRITE_METHODS.contains(methodName) ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();

                if ("add".equals(methodName)) {
                    RepoEntity e = (RepoEntity) args[0];
                    return addEntity(e, sourceSet);
                }

                if ("addAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity e : coll) {
                        res |= addEntity(e, sourceSet);
                    }
                    return res;
                }

                return method.invoke(sourceSet, args);

            } finally {
                actualLock.unlock();
            }
        }
    }

    private class HandlerList implements InvocationHandler {
        private final List<RepoEntity> sourceList;

        protected HandlerList(Collection<RepoEntity> sourceData) {
            this.sourceList = new ArrayList<>(sourceData);
        }

        public List<Object> createProxyList() {
            ClassLoader loader = ArrayList.class.getClassLoader();
            Class[] interfaces = {List.class};
            return (List<Object>) Proxy.newProxyInstance(loader, interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

            String methodName = method.getName();
            Lock actualLock = WRITE_METHODS.contains(methodName) ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();

                if ("add".equals(methodName)) {
                    RepoEntity e = (RepoEntity) args[0];
                    return addEntity(e, sourceList);
                }

                if ("addAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity e : coll) {
                        res |= addEntity(e, sourceList);
                    }
                    return res;
                }

                if ("set".equals(methodName)) {
                    int pos = (int) args[0];
                    RepoEntity e = (RepoEntity) args[1];
                    return setEntityInList(e, sourceList, pos);
                }

                return method.invoke(sourceList, args);

            } finally {
                actualLock.unlock();
            }
        }
    }

}

