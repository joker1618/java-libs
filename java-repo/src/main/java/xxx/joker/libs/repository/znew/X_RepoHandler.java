package xxx.joker.libs.repository.znew;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.entities2.RepoProperty;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class X_RepoHandler {

    private static final List<String> WRITE_METHODS = Arrays.asList("add", "addAll", "delete", "removeIf", "removeAll", "clear", "set");

    private final ReadWriteLock repoLock;

    private Map<Class<?>, HandlerDataSet> handlers;
    private Map<Long, RepoEntity> dataByID;
    private FkManager fkManager;
    private final AtomicLong sequenceValue;

    X_RepoHandler(List<X_RepoEntityDTO> dtoList, ReadWriteLock repoLock) {
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

    private void initRepoHandler(List<X_RepoEntityDTO> dtoList) {
        for(X_RepoEntityDTO dto : dtoList) {
            handlers.put(dto.getEClazz(), new HandlerDataSet(dto.getEntities()));
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
    }

    private void initRepoFields(RepoEntity e) {
        X_RepoClazz rc = X_RepoClazz.wrap(e.getClass());

        // Apply field directives
        rc.getEntityFields().forEach(ef -> ef.applyDirectives(e));

        // Init collections
        for (ClazzField cf : rc.getEntityFields()) {
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

    private void setHandlerCollection(RepoEntity e, ClazzField efield, Collection<RepoEntity> eDeps) {
        if(efield.isList()) {
            HandlerList handler = new HandlerList(e.getEntityID(), efield.getFieldName(), eDeps);
            efield.setValue(e, handler.createProxyList());
        } else {
            HandlerSet handler = new HandlerSet(e.getEntityID(), efield.getFieldName(), eDeps);
            efield.setValue(e, handler.createProxySet());
        }
    }

    private void setSimpleCollection(RepoEntity e, ClazzField cf, Collection<?> eDeps) {
        if(cf.isList()) {
            cf.setValue(e, new ArrayList<>(eDeps));
        } else {
            Object o = cf.isComparableFlatField() ? new TreeSet<>(eDeps) : new HashSet<>(eDeps);
            cf.setValue(e, o);
        }
    }

    private void setDependencies(RepoEntity e) {
        List<X_RepoFK> fkList = fkManager.getForeignKeys(e);
        if(fkList != null && !fkList.isEmpty()) {
            X_RepoClazz rc = X_RepoClazz.wrap(e.getClass());
            Map<String, List<Long>> fkMap = JkStreams.toMap(fkList, X_RepoFK::getFieldName, X_RepoFK::getDepID);
            fkMap.forEach((k,v) -> {
                List<RepoEntity> deps = JkStreams.map(v, depID -> dataByID.get(depID));
                ClazzField cf = rc.getEntityField(k);
                if(cf.isCollection()) {
                    // now use simple collection, then call the method 'initRepoFields' that will create the handlers
                    setSimpleCollection(e, cf, deps);
                } else {
                    cf.setValue(e, deps.get(0));
                }
            });
        }
    }

    public List<X_RepoEntityDTO> getRepoEntityDTOs() {
        List<X_RepoEntityDTO> dtoList = new ArrayList<>();

        handlers.forEach((c,h) -> {
            X_RepoEntityDTO dto = new X_RepoEntityDTO(c);
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
            X_RepoClazz.setEntityID(e, sequenceValue.get());

            if (insColl != null) {
                boolean added = insColl.add(e);
                if (!added) {
                    X_RepoClazz.setEntityID(e, null);
                    return false;
                }
            }

            boolean added = handlers.get(e.getClass()).getEntities().add(e);
            if (!added) {
                if (insColl != null) {
                    insColl.remove(e);
                }
                X_RepoClazz.setEntityID(e, null);
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

        return true;
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
        List<X_RepoFK> parents = fkManager.remove(eID);
        
        // Remove where referenced
        for(X_RepoFK pfk : parents) {
            RepoEntity eParent = dataByID.get(pfk.getFromID());
            X_RepoClazz rc = X_RepoClazz.wrap(eParent.getClass());
            ClazzField cf = rc.getEntityField(pfk.getFieldName());
            if(cf.isCollection()) {
                ((Collection<RepoEntity>)cf.getValue(eParent)).removeIf(elem -> elem.getEntityID() == eID);
            } else {
                cf.setValue(eParent, null);
            }
        }

        // Remove from dataByID
        dataByID.remove(eID);
        
        return true;
    }

    private Map<String, List<RepoEntity>> retrieveDepChilds(RepoEntity e) {
        Map<String, List<RepoEntity>> toRet = new HashMap<>();
        X_RepoClazz rc = X_RepoClazz.wrap(e.getClass());
        for(ClazzField cf : rc.getEntityFields()) {
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
                    List<RepoEntity> all = JkConvert.toArrayList(entities);
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
                    RepoEntity e = (RepoEntity) args[1];
                    return addEntity(e, sourceList, parentID, fieldName);
                }

                return method.invoke(sourceList, args);

            } finally {
                actualLock.unlock();
            }
        }
    }

    private class FkManager {
        private Map<Long, List<X_RepoFK>> foreignKeys = new HashMap<>();
        private Map<Long, Set<Long>> references = new HashMap<>();

        public void add(Collection<X_RepoFK> fkeys) {
            for(X_RepoFK fk : fkeys) {
                add(fk.getFromID(), fk.getFieldName(), fk.getDepID());
            }
        }

        public void add(long fromID, String fieldName, long depID) {
            foreignKeys.putIfAbsent(fromID, new ArrayList<>());
            foreignKeys.get(fromID).add(new X_RepoFK(fromID, fieldName, depID));
            references.putIfAbsent(depID, new HashSet<>());
            references.get(depID).add(fromID);
        }

        public List<X_RepoFK> getForeignKeys(Collection<RepoEntity> entities) {
            return entities.stream().flatMap(e -> getForeignKeys(e).stream()).collect(Collectors.toList());
        }

        public List<X_RepoFK> getForeignKeys(RepoEntity e) {
            return foreignKeys.getOrDefault(e.getEntityID(), Collections.emptyList());
        }

        public List<X_RepoFK> remove(long entityID) {
            Set<X_RepoFK> toRet = new HashSet<>();
            
            Set<Long> parentIDs = references.remove(entityID);
            if(parentIDs != null) {
                for(Long pid : parentIDs) {
                    List<X_RepoFK> allParentFKs = foreignKeys.get(pid);
                    if(allParentFKs != null) {
                        List<X_RepoFK> eRefs = JkStreams.filter(allParentFKs, fk -> fk.getDepID() == entityID);
                        toRet.addAll(eRefs);
                        allParentFKs.removeAll(eRefs);
                        if(allParentFKs.isEmpty()) {
                            foreignKeys.remove(pid);
                        }
                    }
                }
            }

            foreignKeys.remove(entityID);

            return JkConvert.toArrayList(toRet);
        }
    }

}

