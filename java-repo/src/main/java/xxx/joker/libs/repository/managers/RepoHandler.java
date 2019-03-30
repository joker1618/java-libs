package xxx.joker.libs.repository.managers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.exceptions.RepoDesignError;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

class RepoHandler {

    private static final Logger logger = LoggerFactory.getLogger(RepoHandler.class);

    private final ReentrantReadWriteLock repoLock;
    private final AtomicLong repoSequence;

    private Map<Class<?>, Set<JkEntity>> proxySets;
    private Map<Class<?>, Set<JkEntity>> dataMap;
    private Map<Long, JkEntity> dataByID;
    private Map<Long, List<ForeignKey>> foreignKeys;
    private Map<Class<?>, TreeMap<Integer, DesignField>> designMap;

    protected RepoHandler(long repoSequenceValue) {
        this.repoLock = new ReentrantReadWriteLock(true);
        this.repoSequence = new AtomicLong(repoSequenceValue);
    }

    protected void initHandler(Map<Class<?>, List<JkEntity>> dataLists,
                               Map<Long, JkEntity> dataByID,
                               Map<Long, List<ForeignKey>> foreignKeys,
                               Map<Class<?>, TreeMap<Integer, DesignField>> designMap) {

        try {
            repoLock.writeLock().lock();

            if (dataMap != null) throw new RepoDesignError("Repo data handler already initialized");

            logger.debug("initialize repo data handler");

            this.dataByID = dataByID;
            this.foreignKeys = foreignKeys;
            this.dataByID.keySet().forEach(id -> this.foreignKeys.putIfAbsent(id, new ArrayList<>()));
            this.designMap = designMap;

            purgeMissingFk();

            this.dataMap = new HashMap<>();
            this.proxySets = new HashMap<>();
            initDataSets(dataLists);

            dataByID.values().forEach(this::initCollections);
            dataByID.values().forEach(this::spreadDependencies);

        } finally {
            repoLock.writeLock().unlock();
        }
    }

    private void purgeMissingFk() {
        List<Long> missingIDs = JkStreams.filter(foreignKeys.keySet(), fk -> !dataByID.containsKey(fk));
        // Delete missing from
        missingIDs.forEach(foreignKeys::remove);
        // Delete missing deps
        foreignKeys.values().forEach(fklist -> fklist.removeIf(fk -> !dataByID.containsKey(fk.getDepID())));
    }

    protected <T extends JkEntity> T getEntity(long entityID) {
        try {
            repoLock.readLock().lock();
            return (T) dataByID.get(entityID);
        } finally {
            repoLock.readLock().unlock();
        }
    }

    protected Map<Class<?>, Set<JkEntity>> getDataSets() {
        try {
            repoLock.readLock().lock();
            return proxySets;
        } finally {
            repoLock.readLock().unlock();
        }
    }
    protected <T extends JkEntity> Set<T> getDataSet(Class<T> clazz) {
        try {
            repoLock.readLock().lock();
            return (Set<T>) proxySets.get(clazz);
        } finally {
            repoLock.readLock().unlock();
        }
    }

    protected long getRepoSequenceValue() {
        try {
            repoLock.readLock().lock();
            return repoSequence.get();
        } finally {
            repoLock.readLock().unlock();
        }
    }

    protected Map<Long, Pair<JkEntity, List<ForeignKey>>> getDataAndForeignKeys() {
        try {
            repoLock.readLock().lock();
            Map<Long, Pair<JkEntity, List<ForeignKey>>> toRet = new TreeMap<>();
            for(long id : dataByID.keySet()) {
                Pair<JkEntity, List<ForeignKey>> pair = Pair.of(dataByID.get(id), foreignKeys.getOrDefault(id, Collections.emptyList()));
                toRet.put(id, pair);
            }
            return toRet;

        } finally {
            repoLock.readLock().unlock();
        }
    }

    private boolean addEntity(JkEntity e) {
        return addEntity(e, null, null, null);
    }
    private boolean addEntity(JkEntity e, Collection<? super JkEntity> insColl, Long parentID, Integer fieldIdx) {
        if(e.isRegistered()) {
            if(insColl == null)    return false;

            boolean added = insColl.add(e);
            if(!added)  return false;

            ForeignKey fk = new ForeignKey(parentID, fieldIdx, e.getEntityID());
            foreignKeys.get(parentID).add(fk);
            return true;
        }

        long newID = repoSequence.get();
        e.setEntityID(newID);
        e.setInsertTstamp(LocalDateTime.now());

        if(insColl != null) {
            boolean added = insColl.add(e);
            if(!added) {
                e.setEntityID(null);
                e.setInsertTstamp(null);
                return false;
            }
        }

        boolean added = dataMap.get(e.getClass()).add(e);
        if(!added) {
            if(insColl != null)   {
                insColl.remove(e);
            }
            e.setEntityID(null);
            e.setInsertTstamp(null);
            return false;
        }

        if(insColl != null) {
            ForeignKey fk = new ForeignKey(parentID, fieldIdx, e.getEntityID());
            foreignKeys.get(parentID).add(fk);
        }

        repoSequence.incrementAndGet();
        dataByID.put(newID, e);

        initCollections(e);

        logger.info("New entity: ID={}, class={}, PK={}", newID, e.getClass().getSimpleName(), e.getPrimaryKey());

        List<JkEntity> depChilds = retrieveDepChilds(e);
        depChilds.forEach(this::addEntity);

        // Create foreign keys from dependencies (now all dependencies has an ID)
        List<ForeignKey> eFKList = retrieveForeignKeys(e);
        foreignKeys.put(newID, eFKList);

        return true;
    }

    private boolean removeEntity(JkEntity e) {
        boolean res = false;

        Long eID = e.getEntityID();
        if(eID != null) {
            res = dataMap.get(e.getClass()).remove(e);
            foreignKeys.remove(eID);
            dataByID.remove(eID);

            // Remove from deps of other entities
            List<Long> depsID = JkStreams.filterMap(foreignKeys.entrySet(), entry -> !JkStreams.filter(entry.getValue(), fk -> fk.getDepID() == eID).isEmpty(), Map.Entry::getKey);
            for(long depID : depsID) {
                JkEntity edep = dataByID.get(depID);
                // Remove from dependencies
                List<ForeignKey> efklist = JkStreams.filter(foreignKeys.get(depID), fk -> fk.getDepID() == eID);
                for(ForeignKey efk : efklist) {
                    DesignField dfield = designMap.get(edep.getClass()).get(efk.getFromFieldIdx());
                    if(dfield.isCollection()) {
                        ((Collection<JkEntity>)dfield.getValue(edep)).removeIf(en -> en.getEntityID() == eID);
                    } else {
                        dfield.setValue(edep, null);
                    }
                }
                // Remove from foreign keys
                foreignKeys.get(depID).removeIf(fkey -> fkey.getDepID() == eID);
            }

            logger.info("Removed entity: ID={}, class={}, PK={}", eID, e.getClass().getSimpleName(), e.getPrimaryKey());
        }

        return res;
    }

    // Create data sets and proxies
    private void initDataSets(Map<Class<?>, List<JkEntity>> sourceLists) {
        for (Class<?> c : sourceLists.keySet()) {
            HandlerDataSet handler = new HandlerDataSet(sourceLists.get(c));
            this.dataMap.put(c, handler.getDataSet());
            this.proxySets.put(c, handler.createProxyDataSet());
        }
    }

    private void spreadDependencies(JkEntity e) {
        List<DesignField> depDFields = JkStreams.filter(designMap.get(e.getClass()).values(), DesignField::isFlatJkEntity);
        Map<Integer, List<ForeignKey>> depFKs = JkStreams.toMap(foreignKeys.get(e.getEntityID()), ForeignKey::getFromFieldIdx);
        for(DesignField dfield : depDFields) {
            List<ForeignKey> fkField = depFKs.get(dfield.getAnnotIdx());
            if(dfield.isCollection()) {
                List<JkEntity> eDeps = new ArrayList<>();
                if(fkField != null) {
                    eDeps = JkStreams.map(fkField, fk -> dataByID.get(fk.getDepID()));
                }
                setEntityFieldColl(e, dfield, eDeps);

            } else if(fkField != null) {
                JkEntity edep = dataByID.get(fkField.get(0).getDepID());
                dfield.setValue(e, edep);
            }
        }
    }

    private void setEntityFieldColl(JkEntity e, DesignField dfield, Collection<JkEntity> eDeps) {
        if(dfield.isList()) {
            HandlerList handler = new HandlerList(e.getEntityID(), dfield.getAnnotIdx(), eDeps);
            dfield.setValue(e, handler.createProxyList());
        } else {
            HandlerSet handler = new HandlerSet(e.getEntityID(), dfield.getAnnotIdx(), eDeps);
            dfield.setValue(e, handler.createProxySet());
        }
    }

    private void initCollections(JkEntity e) {
        List<DesignField> collFields = JkStreams.filter(designMap.get(e.getClass()).values(), DesignField::isCollection);
        for(DesignField dfield : collFields) {
            Object value = dfield.getValue(e);

            if(!dfield.isFlatJkEntity()) {
                if(value == null) {
                    if(dfield.isList()) {
                        dfield.setValue(e, new ArrayList<>());
                    } else {
                        Object o = dfield.isFlatFieldComparable() ? new TreeSet<>() : new HashSet<>();
                        dfield.setValue(e, o);
                    }
                }

            } else {
                List<JkEntity> data = new ArrayList<>();
                if(value != null) {
                    data.addAll((Collection<JkEntity>) value);
                }
                setEntityFieldColl(e, dfield, data);
            }
        }
    }

    private List<ForeignKey> retrieveForeignKeys(JkEntity e) {
        List<ForeignKey> fkList = new ArrayList<>();
        List<DesignField> depDFields = JkStreams.filter(designMap.get(e.getClass()).values(), DesignField::isFlatJkEntity);
        for(DesignField dfield : depDFields) {
            Object value = dfield.getValue(e);
            if(dfield.isCollection()) {
                Collection<JkEntity> coll = (Collection<JkEntity>) value;
                fkList.addAll(JkStreams.map(coll, v -> new ForeignKey(e.getEntityID(), dfield.getAnnotIdx(), v.getEntityID())));
            } else if(value != null) {
                JkEntity eVal = (JkEntity) value;
                fkList.add(new ForeignKey(e.getEntityID(), dfield.getAnnotIdx(), eVal.getEntityID()));
            }
        }
        return fkList;
    }
    private List<JkEntity> retrieveDepChilds(JkEntity e) {
        List<JkEntity> toRet = new ArrayList<>();
        List<DesignField> depDFields = JkStreams.filter(designMap.get(e.getClass()).values(), DesignField::isFlatJkEntity);
        for(DesignField dfield : depDFields) {
            Object value = dfield.getValue(e);
            if(dfield.isCollection()) {
                Collection<JkEntity> coll = (Collection<JkEntity>) value;
                toRet.addAll(coll);
            } else if(value != null) {
                JkEntity eVal = (JkEntity) value;
                toRet.add(eVal);
            }
        }
        return toRet;
    }

    private class HandlerDataSet implements InvocationHandler {
        private final Logger logger = LoggerFactory.getLogger(HandlerDataSet.class);

        private final List<String> writeMethodNames = Arrays.asList("add", "addAll", "delete", "removeIf", "removeAll", "clear");
        private final TreeSet<JkEntity> dataSet;

        private HandlerDataSet(Collection<JkEntity> data) {
            this.dataSet = new TreeSet<>(data);
        }

        public Set<JkEntity> createProxyDataSet() {
            ClassLoader loader = TreeSet.class.getClassLoader();
            Class[] interfaces = {Set.class};
            return (Set<JkEntity>) Proxy.newProxyInstance(loader, interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Lock actualLock = writeMethodNames.contains(methodName) ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();
                logger.trace("invoked {}", methodName);

                if ("add".equals(methodName)) {
                    JkEntity e = (JkEntity) args[0];
                    return addEntity(e);
                }

                if ("addAll".equals(methodName)) {
                    Collection<JkEntity> coll = (Collection<JkEntity>) args[0];
                    boolean res = false;
                    for (JkEntity e : coll) {
                        res |= addEntity(e);
                    }
                    return res;
                }

                if ("delete".equals(methodName)) {
                    JkEntity e = (JkEntity) args[0];
                    return removeEntity(e);
                }

                if ("removeIf".equals(methodName)) {
                    Predicate<JkEntity> pred = (Predicate<JkEntity>) args[0];
                    List<JkEntity> toremove = JkStreams.filter(dataSet, pred);
                    if (toremove.isEmpty()) return false;
                    boolean res = false;
                    for (JkEntity todel : toremove) {
                        res |= removeEntity(todel);
                    }
                    return res;
                }

                if ("removeAll".equals(methodName)) {
                    Collection<JkEntity> coll = (Collection<JkEntity>) args[0];
                    boolean res = false;
                    for (JkEntity todel : coll) {
                        res |= removeEntity(todel);
                    }
                    return res;
                }

                if ("clear".equals(methodName)) {
                    List<JkEntity> all = JkConvert.toArrayList(dataSet);
                    boolean res = false;
                    for (JkEntity todel : all) {
                        res |= removeEntity(todel);
                    }
                    return res;
                }

                return method.invoke(dataSet, args);

            } finally {
                actualLock.unlock();
            }
        }

        public TreeSet<JkEntity> getDataSet() {
            return dataSet;
        }
    }

    private class HandlerSet implements InvocationHandler {
        private final Logger logger = LoggerFactory.getLogger(HandlerSet.class);

        private final long parentID;
        private final int fieldIdx;
        private final TreeSet<Comparable> sourceSet;

        protected HandlerSet(long parentID, int fieldIdx, Collection<? extends Comparable> sourceData) {
            this.parentID = parentID;
            this.fieldIdx = fieldIdx;
            this.sourceSet = new TreeSet<>(sourceData);
        }

        protected Set<Comparable> createProxySet() {
            ClassLoader loader = TreeSet.class.getClassLoader();
            Class[] interfaces = {Set.class};
            return (Set<Comparable>) Proxy.newProxyInstance(loader, interfaces, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Lock actualLock = StringUtils.equalsAny(methodName, "add", "addAll") ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();
                logger.trace("invoked {}", methodName);

                if ("add".equals(methodName)) {
                    JkEntity e = (JkEntity) args[0];
                    return addEntity(e, sourceSet, parentID, fieldIdx);
                }

                if ("addAll".equals(methodName)) {
                    Collection<JkEntity> coll = (Collection<JkEntity>) args[0];
                    boolean res = false;
                    for (JkEntity e : coll) {
                        res |= addEntity(e, sourceSet, parentID, fieldIdx);
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
        private final Logger logger = LoggerFactory.getLogger(HandlerList.class);

        private final long parentID;
        private final int fieldIdx;
        private final List<JkEntity> sourceList;

        protected HandlerList(long parentID, int fieldIdx, Collection<JkEntity> sourceData) {
            this.parentID = parentID;
            this.fieldIdx = fieldIdx;
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
            Lock actualLock = StringUtils.equalsAny(methodName, "add", "addAll", "set") ? repoLock.writeLock() : repoLock.readLock();

            try {
                actualLock.lock();
                logger.trace("invoked {}", methodName);

                if ("add".equals(methodName)) {
                    JkEntity e = (JkEntity) args[0];
                    return addEntity(e, sourceList, parentID, fieldIdx);
                }

                if ("addAll".equals(methodName)) {
                    Collection<JkEntity> coll = (Collection<JkEntity>) args[0];
                    boolean res = false;
                    for (JkEntity e : coll) {
                        res |= addEntity(e, sourceList, parentID, fieldIdx);
                    }
                    return res;
                }

                if ("set".equals(methodName)) {
                    JkEntity e = (JkEntity) args[1];
                    return addEntity(e, sourceList, parentID, fieldIdx);
                }

                return method.invoke(sourceList, args);

            } finally {
                actualLock.unlock();
            }
        }

    }
}
