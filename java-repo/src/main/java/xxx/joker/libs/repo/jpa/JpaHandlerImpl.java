package xxx.joker.libs.repo.jpa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.runtime.wrapper.TypeWrapper;
import xxx.joker.libs.repo.config.RepoCtx;
import xxx.joker.libs.repo.design.RepoEntity;
import xxx.joker.libs.repo.design.entities.RepoProperty;
import xxx.joker.libs.repo.exceptions.RepoError;
import xxx.joker.libs.repo.jpa.indexes.IndexManager;
import xxx.joker.libs.repo.jpa.persistence.DaoDTO;
import xxx.joker.libs.repo.jpa.persistence.DaoHandler;
import xxx.joker.libs.repo.jpa.proxy.ProxyDataSet;
import xxx.joker.libs.repo.jpa.proxy.ProxyFactory;
import xxx.joker.libs.repo.wrapper.RepoWClazz;
import xxx.joker.libs.repo.wrapper.RepoWField;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;

import static xxx.joker.libs.core.lambda.JkStreams.filter;
import static xxx.joker.libs.core.lambda.JkStreams.filterMap;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.repo.config.RepoConfig.PROP_DB_SEQUENCE;
import static xxx.joker.libs.repo.exceptions.ErrorType.*;

class JpaHandlerImpl implements JpaHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JpaHandlerImpl.class);

    private final RepoCtx ctx;
    private final IndexManager indexManager;

    private DaoHandler daoHandler;
    private ProxyFactory proxyFactory;
    private TreeMap<Class<? extends RepoEntity>, ProxyDataSet> proxies;
    private Map<Long, RepoEntity> dataById;

    public JpaHandlerImpl(RepoCtx ctx) {
        this.ctx = ctx;
        this.dataById = new TreeMap<>();
        this.indexManager = new IndexManager(0L);

        this.proxyFactory = new ProxyFactory(ctx.getLock(), this::addEntityToRepo, this::removeEntity);
        this.proxies = new TreeMap<>(Comparator.comparing(Class::getName));
        this.proxies.putAll(JkStreams.toMapSingle(ctx.getWClazzMap().keySet(), Function.identity(), k -> proxyFactory.createProxyDataSet()));

        this.daoHandler = DaoHandler.createHandler(ctx);

        // Init repo data sets
        initDataSets(daoHandler.readData());
    }

    @Override
    public void initRepoContent(Collection<RepoEntity> repoData) {
        try {
            ctx.getWriteLock().lock();
            List<DaoDTO> dtoList = daoHandler.createDTOs(repoData);
            initDataSets(dtoList);
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    private List<RepoEntity> checkIdAndPk(List<RepoEntity> entities) {
        List<RepoEntity> finalList = new ArrayList<>();

        // Check duplicates
        Set<Long> idUsed = new HashSet<>();
        Map<Class<?>, Set<String>> pkMap = new HashMap<>();
        for (RepoEntity e : entities) {
            Long eid = e.getEntityId();
            pkMap.putIfAbsent(e.getClass(), new HashSet<>());
            if(eid != null && !idUsed.add(eid)) {
                LOG.error("ID duplicated: {}. Discarded entity: {}", eid, e);
            } else {
                String epk = e.getPrimaryKey();
                if(!pkMap.get(e.getClass()).add(epk)) {
                    LOG.error("Primary duplicated: {}. Discarded entity: {}", epk, e);
                } else {
                    finalList.add(e);
                }
            }
        }

        // Set missing IDs
        Map<Boolean, List<RepoEntity>> map = JkStreams.toMap(finalList, e -> e.getEntityId() != null);
        List<RepoEntity> withoutId = map.get(false);
        if(withoutId != null) {
            AtomicLong seqValue = new AtomicLong(0L);
            if(map.containsKey(true)) {
                seqValue.set(1 + map.get(true).stream().mapToLong(RepoEntity::getEntityId).max().orElse(-1L));
            }
            withoutId.forEach(e -> e.setEntityId(seqValue.getAndIncrement()));
            LOG.warn("Set ID for {}/{} entities", withoutId.size(), finalList.size());
        }

        return finalList;
    }
    private void createProxiesForStructFields(RepoWClazz cw, RepoEntity elem) {
        cw.getFields().forEach(fw -> {
            fw.initNullValue(elem);
            if(fw.isEntityFlat() && (fw.isList() || fw.isSet() || fw.isMap())) {
                if(fw.isList()) {
                    List<RepoEntity> proxyList = proxyFactory.createProxyList(fw.getValue(elem));
                    fw.setValue(elem, proxyList);
                } else if(fw.isSet()) {
                    Set<RepoEntity> proxySet = proxyFactory.createProxySet(fw.getValue(elem));
                    fw.setValue(elem, proxySet);
                } else if(fw.isMap()) {
                    Map<?, ?> proxyMap = proxyFactory.createProxyMap(fw.getValue(elem), fw);
                    fw.setValue(elem, proxyMap);
                }
            }
        });
    }

    @SafeVarargs
    @Override
    public final <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters) {
        try {
            ctx.getReadLock().lock();
            return JkStreams.findUnique(getDataSet(entityClazz), filters);
        } finally {
            ctx.getReadLock().unlock();
        }
    }
    @Override
    public Map<Class<? extends RepoEntity>, Set<RepoEntity>> getDataSets() {
        try {
            ctx.getReadLock().lock();
            return JkStreams.toMapSingle(proxies.entrySet(), Map.Entry::getKey, e -> e.getValue().getProxyDataSet());
        } finally {
            ctx.getReadLock().unlock();
        }
    }
    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        try {
            ctx.getReadLock().lock();
            if(!proxies.containsKey(entityClazz)) {
                throw new RepoError(RUNTIME_CLASS_NOT_MANAGED, "Unable to get data set of type {}: class not managed (loaded classes = {})", entityClazz, proxies.keySet());
            }
            return (Set<T>) proxies.get(entityClazz).getProxyDataSet();
        } finally {
            ctx.getReadLock().unlock();
        }
    }
    @Override
    public Map<Long, RepoEntity> getDataById() {
        try {
            ctx.getReadLock().lock();
            return dataById;
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    @Override
    public void clearAll(boolean resetIdSequence) {
        try {
            ctx.getWriteLock().lock();
            proxies.values().forEach(pds -> pds.getEntities().clear());
            dataById.clear();
            if(resetIdSequence) {
                indexManager.setSequenceValue(0L);
            }
            LOG.info("Cleared all data sets");
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public boolean commit() {
        try {
            ctx.getWriteLock().lock();
            daoHandler.persistData(dataById.values());
            LOG.info("Repo committed");
            return true;
        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    @Override
    public boolean rollback() {
        try {
            ctx.getWriteLock().lock();
            initDataSets(daoHandler.readData());
            LOG.info("Rollback done");
            return true;
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public String setProperty(String key, String value) {
        try {
            ctx.getWriteLock().lock();
            RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
            String oldValue;
            if (prop == null) {
                prop = new RepoProperty(key, value);
                getDataSet(RepoProperty.class).add(prop);
                oldValue = null;
            } else {
                oldValue = prop.getValue();
                prop.setValue(value);
            }
            return oldValue;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    @Override
    public String delProperty(String key) {
        try {
            ctx.getWriteLock().lock();
            RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
            if (prop == null)   return null;
            getDataSet(RepoProperty.class).remove(prop);
            return prop.getValue();

        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    @Override
    public String getProperty(String key) {
        RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
        return prop == null ? null : prop.getValue();
    }

    @Override
    public void updateDependencies(Collection<? extends RepoEntity> entities) {
        try {
            ctx.getWriteLock().lock();
            Set<Long> idSet = dataById.keySet();
            for (RepoEntity entity : entities) {
                RepoWClazz wClazz = ctx.getWClazz(entity.getClass());
                for (RepoWField wField : wClazz.getFields(RepoWField::isEntityFlat)) {
                    if(wField.isEntity()) {
                        RepoEntity re = wField.getValue(entity);
                        if(re != null && !idSet.contains(re.getEntityId())) {
                            wField.setValue(entity, null);
                        }

                    } else if(wField.isCollection()) {
                        Collection<RepoEntity> coll = wField.getValue(entity);
                        coll.removeIf(elem -> !idSet.contains(elem.getEntityId()));

                    } else if(wField.isMap()) {
                        Map<?,?> map = wField.getValue(entity);
                        if(wField.getParamType(0).instanceOf(RepoEntity.class)) {
                            List<?> keysToRemove = filter(map.keySet(), k -> !idSet.contains(((RepoEntity) k).getEntityId()));
                            keysToRemove.forEach(map::remove);
                        }
                        TypeWrapper twMapValue = wField.getParamType(1);
                        if(twMapValue.instanceOfFlat(RepoEntity.class)) {
                            if (twMapValue.instanceOf(RepoEntity.class)) {
                                List<?> keysToRemove = filterMap(map.entrySet(), e -> !idSet.contains(((RepoEntity) e.getValue()).getEntityId()), Map.Entry::getKey);
                                keysToRemove.forEach(map::remove);

                            } else if (twMapValue.isCollection()) {
                                for (Object valueColl : map.values()) {
                                    Collection<RepoEntity> coll = (Collection<RepoEntity>) valueColl;
                                    coll.removeIf(e -> !idSet.contains(e.getEntityId()));
                                }
                            }
                        }
                    }
                }
            }

        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    @Override
    public void removeFromDependencies(RepoEntity toRemove, Collection<? extends RepoEntity> entities) {
        try {
            ctx.getWriteLock().lock();
            for (RepoEntity entity : entities) {
                for (RepoWField wf : ctx.getWClazz(entity.getClass()).getFields(wf -> wf.instanceOfFlat(toRemove.getClass()))) {
                    if(wf.isEntity()) {
                        if(toRemove.equals(wf.getValue(entity))) {
                            wf.setValue(entity, null);
                        }

                    } else if(wf.isCollection()) {
                        Collection<RepoEntity> coll = wf.getValue(entity);
                        coll.remove(toRemove);

                    } else if(wf.isMap()) {
                        Map<?,?> map = wf.getValue(entity);
                        if(wf.getParamType(0).instanceOf(RepoEntity.class)) {
                            map.remove(toRemove);
                        }
                        TypeWrapper twMapValue = wf.getParamType(1);
                        if(twMapValue.instanceOfFlat(RepoEntity.class)) {
                            if (twMapValue.instanceOf(RepoEntity.class)) {
                                List<?> keysToRemove = filterMap(map.entrySet(), e -> toRemove.equals(e.getValue()), Map.Entry::getKey);
                                keysToRemove.forEach(map::remove);

                            } else if (twMapValue.isCollection()) {
                                for (Object valueColl : map.values()) {
                                    Collection<RepoEntity> coll = (Collection<RepoEntity>) valueColl;
                                    coll.remove(toRemove);
                                }
                            }
                        }
                    }
                }
            }

        } finally {
            ctx.getWriteLock().unlock();
        }

    }


    private void initDataSets(List<DaoDTO> dtoList) {
        try {
            JkTimer timer = JkTimer.start();

            ctx.getWriteLock().lock();

            List<RepoEntity> entities = JkStreams.map(dtoList, DaoDTO::getEntity);
            List<RepoEntity> finalList = checkIdAndPk(entities);

            if(!dataById.isEmpty())
                clearAll(false);

            if(!finalList.isEmpty()) {
                Map<Class<?>, List<RepoEntity>> emap = JkStreams.toMap(finalList, RepoEntity::getClass);
                List<Class<?>> wrongs = JkStreams.filter(emap.keySet(), c -> !proxies.containsKey(c));
                if(!wrongs.isEmpty())
                    throw new RepoError(RUNTIME_CLASS_NOT_MANAGED, "Classes not managed: {}", wrongs);

                // Init data sets and entities
                emap.forEach((c,l) -> {
                    proxies.get(c).getEntities().addAll(l);
                    RepoWClazz cw = ctx.getWClazz(c);
                    l.forEach(elem -> {
                        dataById.put(elem.getEntityId(), elem);
                        createProxiesForStructFields(cw, elem);
                    });
                });
            }

            // Update sequence ID value to the next value to use
            long seqValue = 1 + dataById.keySet().stream().mapToLong(Long::longValue).max().orElse(-1L);
            indexManager.setSequenceValue(seqValue);
            updateIdSequenceProperty();

            LOG.info("Initialized repo in {}", timer.strElapsed());

        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    private boolean addEntityToRepo(RepoEntity e) {
        try {
            ctx.getWriteLock().lock();
            if (e.getEntityId() != null)
                return false;

            // Init fields with def value
            RepoWClazz cw = ctx.getWClazz(e.getClass());
            createProxiesForStructFields(cw, e);

            // Add dependency entities before the input entity 'e'
            // This avoid problems when 'e' has a primary key that with other RepoEntity
            Map<Long, List<RepoWField>> refs = new TreeMap<>();
            cw.getFields(RepoWField::isEntityFlat).forEach(fw -> {
                if (fw.isEntity()) {
                    RepoEntity edep = fw.getValue(e);
                    RepoEntity toSet = addChildEntity(edep, fw, refs);
                    if(toSet != null)   fw.setValue(e, toSet);

                } else if (fw.isList()) {
                    List<RepoEntity> depList = fw.getValue(e);
                    addChildEntityList(depList, fw, refs);

                } else if (fw.isSet()) {
                    Set<RepoEntity> depSet = fw.getValue(e);
                    addChildEntitySet(depSet, fw, refs);

                } else if (fw.isMap()) {
                    boolean isKeyEntity = fw.getParamType(0).instanceOf(RepoEntity.class);
                    TypeWrapper twValue = fw.getParamType(1);
                    boolean isValueEntity = twValue.instanceOf(RepoEntity.class);
                    boolean isValueEntityColl = twValue.isCollection() && twValue.instanceOfFlat(RepoEntity.class);
                    Map depMap = fw.getValue(e);
                    List<? extends Map.Entry<?, ?>> entryList = toList(depMap.entrySet());
                    for (int idx = 0; idx < entryList.size(); idx++) {
                        Map.Entry<?, ?> entry = entryList.get(idx);
                        if(isKeyEntity) {
                            RepoEntity edep = (RepoEntity) entry.getKey();
                            RepoEntity toSet = addChildEntity(edep, fw, refs);
                            if (toSet != null) {
                                depMap.put(toSet, entry.getValue());
                            }
                        }
                        if(isValueEntity) {
                            RepoEntity edep = (RepoEntity) entry.getValue();
                            RepoEntity toSet = addChildEntity(edep, fw, refs);
                            if (toSet != null) {
                                depMap.put(entry.getKey(), toSet);
                            }
                        } else if(isValueEntityColl) {
                            Collection<RepoEntity> deps = (Collection<RepoEntity>) entry.getValue();
                            if(twValue.isList()) {
                                addChildEntityList(deps, fw, refs);
                            } else {
                                addChildEntitySet(deps, fw, refs);
                            }
                        }
                    }
                }
            });

            // Now all the dependencies belongs to repo: add source entity
            synchronized (indexManager) {
                e.setEntityId(indexManager.getSequenceValueAndLock());

                boolean nullTm = false;
                if (e.getCreationTm() == null) {
                    e.setCreationTm();
                    nullTm = true;
                }

                Set<RepoEntity> ds = proxies.get(e.getClass()).getEntities();
                boolean add = ds.add(e);

                if (add) {
                    Long eid = e.getEntityId();
                    dataById.put(eid, e);
                    indexManager.unlockSequence(true);
                    updateIdSequenceProperty();
                    LOG.info("Added new entity: {}", e);
                    return true;
                } else {
                    LOG.debug("Unable to add entity: PK already present [{}]", e.strShort());
                    e.setEntityId(null);
                    if (nullTm) e.setCreationTm(null);
                    indexManager.unlockSequence(false);
                    return false;
                }
            }

        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    private RepoEntity addChildEntity(RepoEntity edep, RepoWField fw, Map<Long, List<RepoWField>> refs) {
        RepoEntity toRet = null;
        if (edep != null) {
            boolean res = edep.getEntityId() == null && addEntityToRepo(edep);
            if (!res && edep.getEntityId() == null) {
                RepoEntity egot = get(edep.getClass(), edep::equals);
                if(egot == null)    throw new RepoError(RUNTIME_UNEXPECTED_ERROR, "Not found entity by PK: {}", edep.strShort());
                edep = egot;
                toRet = egot;
            }
            refs.putIfAbsent(edep.getEntityId(), new ArrayList<>());
            refs.get(edep.getEntityId()).add(fw);
        }
        return toRet;
    }
    private void addChildEntityList(Collection<RepoEntity> deps, RepoWField fw, Map<Long, List<RepoWField>> refs) {
        List<RepoEntity> depList = toList(deps);
        for (int idx = 0; idx < depList.size(); idx++) {
            RepoEntity edep = depList.get(idx);
            boolean res = edep.getEntityId() == null && addEntityToRepo(edep);
            if (!res && edep.getEntityId() == null) {
                RepoEntity egot = get(edep.getClass(), edep::equals);
                if(egot == null)    throw new RepoError(RUNTIME_UNEXPECTED_ERROR, "Not found entity by PK: {}", edep.strShort());
                depList.set(idx, egot);
                edep = egot;
            }
            refs.putIfAbsent(edep.getEntityId(), new ArrayList<>());
            refs.get(edep.getEntityId()).add(fw);
        }
    }
    private void addChildEntitySet(Collection<RepoEntity> depSet, RepoWField fw, Map<Long, List<RepoWField>> refs) {
        List<RepoEntity> elist = toList(depSet);
        for (int idx = 0; idx < elist.size(); idx++) {
            RepoEntity edep = elist.get(idx);
            boolean res = edep.getEntityId() == null && addEntityToRepo(edep);
            if (!res && edep.getEntityId() == null) {
                depSet.remove(edep);
                RepoEntity egot = get(edep.getClass(), edep::equals);
                if(egot == null)    throw new RepoError(RUNTIME_UNEXPECTED_ERROR, "Not found entity by PK: {}", edep.strShort());
                depSet.add(egot);
                edep = egot;
            }
            refs.putIfAbsent(edep.getEntityId(), new ArrayList<>());
            refs.get(edep.getEntityId()).add(fw);
        }
    }

    private boolean removeEntity(RepoEntity e) {
        try {
            ctx.getWriteLock().lock();

            Long eid = e.getEntityId();
            if(eid == null) {
                return false;
            }

            // Remove input entity 'e'
            boolean res = proxies.get(e.getClass()).getEntities().remove(e);
            if(!res)
                return false;

            dataById.remove(eid);
            LOG.info("Removed entity: {}", e);

            // Manage cascade delete
            List<RepoWField> cascadeFields = ctx.getWClazz(e.getClass()).getFields(RepoWField::isCascadeDelete);
            cascadeFields.forEach(fcasc -> {
                Set<RepoEntity> toDel = new HashSet<>();
                if(fcasc.isEntity()) {
                    RepoEntity child = fcasc.getValue(e);
                    Optional.ofNullable(child).ifPresent(toDel::add);
                } else {
                    Collection<RepoEntity> childList = fcasc.getValue(e);
                    toDel.addAll(childList);
                }
                toDel.forEach(this::removeEntity);
            });

            return res;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }


    private void updateIdSequenceProperty() {
        try {
            ctx.getWriteLock().lock();
            synchronized (indexManager) {
                setProperty(PROP_DB_SEQUENCE, String.valueOf(indexManager.getSequenceValue()));
            }
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

}
