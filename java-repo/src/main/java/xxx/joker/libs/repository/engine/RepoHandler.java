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
import java.util.function.Predicate;
import java.util.stream.Collectors;

class RepoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RepoHandler.class);
    private static final List<String> WRITE_METHODS = Arrays.asList("add", "addAll", "delete", "removeIf", "removeAll", "clear", "set");

    private final ReadWriteLock repoLock;

    private Map<Class<?>, HandlerDataSet> handlers;
    private Map<Long, RepoEntity> dataByID;
    private FkManager fkManager;
    private final AtomicLong sequenceValue;

    RepoHandler(List<RepoDTO> dtoList, ReadWriteLock repoLock) {
        this.repoLock = repoLock;

        this.handlers = new HashMap<>();
        this.dataByID = new HashMap<>();
        this.fkManager = new FkManager();
        this.sequenceValue = new AtomicLong(0L);

        initRepoHandler(dtoList);
    }

    public Set<RepoEntity> getDataSet(Class<?> entityClazz) {
        HandlerDataSet handler = handlers.get(entityClazz);
        return handler == null ? null : handler.getProxySet();
    }

    private void initRepoHandler(List<RepoDTO> dtoList) {
        for(RepoDTO dto : dtoList) {
            dataByID.putAll(JkStreams.toMapSingle(dto.getEntities(), RepoEntity::getEntityID));
            fkManager.add(dto.getForeignKeys());
        }

        if(!dataByID.isEmpty()) {
            // 'setDependencies' does not create collection handlers, so after must be called 'initRepoFields'
            dataByID.values().forEach(this::setDependencies);
            dataByID.values().forEach(this::initRepoFields);
            // Set sequence value
            long maxID = dataByID.keySet().stream().mapToLong(l -> l).max().getAsLong();
            sequenceValue.set(1L + maxID);
        }

        for(RepoDTO dto : dtoList) {
            handlers.put(dto.getEClazz(), new HandlerDataSet(dto.getEntities()));
        }
    }

    private void initRepoFields(RepoEntity e) {
        ClazzWrapper rc = ClazzWrapper.wrap(e.getClass());

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
            HandlerList handler = new HandlerList(e.getEntityID(), efield.getFieldName(), eDeps);
            efield.setValue(e, handler.createProxyList());
        } else {
            HandlerSet handler = new HandlerSet(e.getEntityID(), efield.getFieldName(), eDeps);
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

    private void setDependencies(RepoEntity e) {
        List<RepoFK> fkList = fkManager.getForeignKeys(e);
        if(fkList != null && !fkList.isEmpty()) {
            ClazzWrapper rc = ClazzWrapper.wrap(e.getClass());
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
            dto.setForeignKeys(fkManager.getForeignKeys(h.getEntities()));
        });

        return dtoList;
    }

    private boolean addEntity(RepoEntity e) {
        return addEntity(e, null, null, null);
    }
    private boolean addEntity(RepoEntity e, Collection<RepoEntity> insColl, Long parentID, String fieldName) {
        if(e.getEntityID() != null) {
            if(insColl == null)  return false;
            if(!insColl.add(e))  return false;
            fkManager.add(parentID, fieldName, e.getEntityID());
            return true;
        }

        synchronized (sequenceValue) {
            ClazzWrapper.setEntityID(e, sequenceValue.get());

            if (insColl != null) {
                boolean added = insColl.add(e);
                if (!added) {
                    ClazzWrapper.setEntityID(e, null);
                    return false;
                }
            }

            boolean added = handlers.get(e.getClass()).getEntities().add(e);
            if (!added) {
                if (insColl != null) {
                    insColl.remove(e);
                }
                ClazzWrapper.setEntityID(e, null);
                return false;
            }

            if (insColl != null) {
                fkManager.add(parentID, fieldName, e.getEntityID());
            }

            sequenceValue.getAndIncrement();
        }

        dataByID.put(e.getEntityID(), e);

        initRepoFields(e);

        Map<String, List<RepoEntity>> depChilds = retrieveDepChilds(e);
        depChilds.forEach((fname,children) -> {
            children.forEach(child -> {
                addEntity(child);
                if(child.getEntityID() != null) {
                    fkManager.add(e.getEntityID(), fname, child.getEntityID());
                }
            });
        });

        LOG.debug("New entity added: {}", e);

        return true;
    }

    private RepoEntity setEntityInList(RepoEntity e, List<RepoEntity> insList, int setPos, Long parentID, String fieldName) {
        if(e.getEntityID() == null) {
            if (addEntity(e)) {
                return null;
            }
        }

        RepoEntity old = insList.set(setPos, e);
        RepoFK newFK = new RepoFK(parentID, fieldName, e.getEntityID());
        fkManager.replaceFK(parentID, fieldName, setPos, newFK);

        return old;
    }

    private boolean removeEntity(RepoEntity e) {
        Long eID = e.getEntityID();
        if(eID == null) {
            return false;
        }

        // Remove from dataSet
        boolean res = handlers.get(e.getClass()).getEntities().remove(e);
        if(!res) {
            return false;
        }

        // Remove foreign keys 
        List<RepoFK> parents = fkManager.remove(eID);
        
        // Remove where referenced
        for(RepoFK pfk : parents) {
            RepoEntity eParent = dataByID.get(pfk.getFromID());
            ClazzWrapper rc = ClazzWrapper.wrap(eParent.getClass());
            FieldWrapper cf = rc.getEntityField(pfk.getFieldName());
            if(cf.isCollection()) {
                ((Collection<RepoEntity>)cf.getValue(eParent)).removeIf(elem -> elem.getEntityID() == eID);
            } else {
                cf.setValue(eParent, null);
            }
        }

        // Remove from dataByID
        dataByID.remove(eID);

        LOG.debug("Removed entity: {}", e);
        
        return true;
    }

    private Map<String, List<RepoEntity>> retrieveDepChilds(RepoEntity e) {
        Map<String, List<RepoEntity>> toRet = new HashMap<>();
        ClazzWrapper rc = ClazzWrapper.wrap(e.getClass());
        for(FieldWrapper cf : rc.getEntityFields()) {
            if(cf.isRepoEntityFlatField()) {
                Object value = cf.getValue(e);
                List<RepoEntity> depColl = new ArrayList<>();
                if(cf.isCollection()) {
                    depColl.addAll((Collection<RepoEntity>) value);
                } else if(value != null) {
                    depColl.add((RepoEntity) value);
                }
                if(!depColl.isEmpty()) {
                    toRet.put(cf.getFieldName(), depColl);
                }
            }
        }
        return toRet;
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

                if ("delete".equals(methodName)) {
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
        private final long parentID;
        private final String fieldName;
        private final TreeSet<RepoEntity> sourceSet;

        protected HandlerSet(long parentID, String fieldName, Collection<RepoEntity> sourceData) {
            this.parentID = parentID;
            this.fieldName = fieldName;
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
                    return addEntity(e, sourceSet, parentID, fieldName);
                }

                if ("addAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity e : coll) {
                        res |= addEntity(e, sourceSet, parentID, fieldName);
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
        private final long parentID;
        private final String fieldName;
        private final List<RepoEntity> sourceList;

        protected HandlerList(long parentID, String fieldName, Collection<RepoEntity> sourceData) {
            this.parentID = parentID;
            this.fieldName = fieldName;
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
                    return addEntity(e, sourceList, parentID, fieldName);
                }

                if ("addAll".equals(methodName)) {
                    Collection<RepoEntity> coll = (Collection<RepoEntity>) args[0];
                    boolean res = false;
                    for (RepoEntity e : coll) {
                        res |= addEntity(e, sourceList, parentID, fieldName);
                    }
                    return res;
                }

                if ("set".equals(methodName)) {
                    int pos = (int) args[0];
                    RepoEntity e = (RepoEntity) args[1];
                    return setEntityInList(e, sourceList, pos, parentID, fieldName);
                }

                return method.invoke(sourceList, args);

            } finally {
                actualLock.unlock();
            }
        }
    }

    private class FkManager {
        private Map<Long, List<RepoFK>> foreignKeys = new HashMap<>();
        private Map<Long, Set<Long>> references = new HashMap<>();

        public void add(Collection<RepoFK> fkeys) {
            for(RepoFK fk : fkeys) {
                add(fk.getFromID(), fk.getFieldName(), fk.getDepID());
            }
        }

        public void add(long fromID, String fieldName, long depID) {
            foreignKeys.putIfAbsent(fromID, new ArrayList<>());
            foreignKeys.get(fromID).add(new RepoFK(fromID, fieldName, depID));
            references.putIfAbsent(depID, new HashSet<>());
            references.get(depID).add(fromID);
        }

        public List<RepoFK> getForeignKeys(Collection<RepoEntity> entities) {
            return entities.stream().flatMap(e -> getForeignKeys(e).stream()).collect(Collectors.toList());
        }

        public List<RepoFK> getForeignKeys(RepoEntity e) {
            return foreignKeys.getOrDefault(e.getEntityID(), Collections.emptyList());
        }

        public List<RepoFK> remove(long entityID) {
            Set<RepoFK> toRet = new HashSet<>();
            
            Set<Long> parentIDs = references.remove(entityID);
            if(parentIDs != null) {
                for(Long pid : parentIDs) {
                    List<RepoFK> allParentFKs = foreignKeys.get(pid);
                    if(allParentFKs != null) {
                        List<RepoFK> eRefs = JkStreams.filter(allParentFKs, fk -> fk.getDepID() == entityID);
                        toRet.addAll(eRefs);
                        allParentFKs.removeAll(eRefs);
                        if(allParentFKs.isEmpty()) {
                            foreignKeys.remove(pid);
                        }
                    }
                }
            }

            foreignKeys.remove(entityID);

            return JkConvert.toList(toRet);
        }

        public void replaceFK(long parentID, String fieldName, int fkIndex, RepoFK newFK) {
            List<RepoFK> fkList = foreignKeys.get(parentID);

            int pos = -1;
            for(int i = 0, counter = 0; pos < 0 && i < fkList.size(); i++) {
                if(fkList.get(i).getFieldName().equals(fieldName)) {
                    if(counter == fkIndex) {
                        pos = i;
                    } else {
                        counter++;
                    }
                }
            }

            RepoFK oldFK = fkList.set(pos, newFK);
            references.get(oldFK.getDepID()).remove(parentID);
            references.putIfAbsent(newFK.getDepID(), new HashSet<>());
            references.get(newFK.getDepID()).add(parentID);
        }
    }

}

