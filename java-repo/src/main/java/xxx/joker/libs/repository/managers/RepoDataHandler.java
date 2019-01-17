package xxx.joker.libs.repository.managers;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.JkEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

class RepoDataHandler {

    private final ReentrantReadWriteLock dbLock;

    private AtomicLong dbSequence;
    private TreeMap<Class<?>, Set<JkEntity>> dataSets;
    private Map<Class<?>, TreeMap<Integer, DesignField>> designFields;
    private Map<Long, List<ForeignKey>> foreignKeys;
    private Map<Long, JkEntity> dataByID;

    public RepoDataHandler() {
        this.dbLock = new ReentrantReadWriteLock();
    }

    public RepoDataHandler(TreeMap<Class<?>, Set<JkEntity>> dataSets,
                           Map<Class<?>, TreeMap<Integer, DesignField>> designFields,
                           Map<Long, List<ForeignKey>> foreignKeys) {

        this.dbLock = new ReentrantReadWriteLock();
        this.dbSequence = new AtomicLong(0L);
        this.designFields = designFields;
        this.dataSets = dataSets;
        this.foreignKeys = foreignKeys;
        this.dataByID = JkStreams.toMapSingle(getAllEntities(), JkEntity::getEntityID);
    }

    public void updateIndexes() {
        dataByID = JkStreams.toMapSingle(getAllEntities(), JkEntity::getEntityID);
    }

    public TreeMap<Class<?>, Set<JkEntity>> getDataSets() {
        try {
            dbLock.readLock().lock();
            return dataSets;
        } finally {
            dbLock.readLock().unlock();
        }
    }


    public <T extends JkEntity> Set<T> getDataSet(Class<?> entityClazz) {
        try {
            dbLock.readLock().lock();
            return (Set<T>) dataSets.get(entityClazz);
        } finally {
            dbLock.readLock().unlock();
        }
    }

    public <T extends JkEntity> boolean addEntity(T entity) {
        try {
            dbLock.writeLock().lock();
            return addEntityRecursive(entity);
        } finally {
            dbLock.writeLock().unlock();
        }
    }

    public <T extends JkEntity> boolean removeEntity(T entity) {
        try {
            dbLock.writeLock().lock();

            boolean removed = dataSets.get(entity.getClass()).remove(entity);
            if(removed) {
                Long entityID = entity.getEntityID();

                // Remove entity from other entities dependencies
                List<Long> refIDs = JkStreams.filterMap(foreignKeys.entrySet(), en -> en.getValue().contains(entityID), Map.Entry::getKey);
                for(Long refID : refIDs) {
                    JkEntity edep = dataByID.get(refID);
                    List<DesignField> dfields = JkStreams.filter(designFields.get(edep.getClass()).values(), DesignField::isFlatJkEntity);
                    for(DesignField df : dfields) {
                        if(df.isCollection()) {
                            ((Collection<JkEntity>)df.getValue(edep)).removeIf(et -> et.getEntityID() == entityID);
                        } else {
                            df.setValue(edep, null);
                        }
                    }
                }

                // Update indexes
                dataByID.remove(entityID);
                foreignKeys.remove(entityID);
                foreignKeys.values().forEach(set -> set.remove(entityID));
            }

            return removed;

        } finally {
            dbLock.writeLock().unlock();
        }
    }


    private <T extends JkEntity> boolean addEntityRecursive(T entity) {
        boolean added = false;
        Long entityID = entity.getEntityID();

        if (entityID == null) {
            entity.setEntityID(dbSequence.get());
            entity.setInsertTstamp(LocalDateTime.now());

            added = dataSets.get(entity.getClass()).add(entity);
            if (added) {
                dbSequence.getAndIncrement();
                dataByID.put(entityID, entity);
                foreignKeys.put(entityID, new ArrayList<>());
            }
        }


        TreeMap<DesignField, List<JkEntity>> echilds = findChildEntities(entity);

        // Add all dependencies, so that the ID will be set for all the new ones
        echilds.values().forEach(el -> el.forEach(this::addEntityRecursive));

        // Add foreign keys
        echilds.forEach((k,v) -> v.forEach(ev ->
                foreignKeys.get(entityID).add(new ForeignKey(entityID, k.getIdx(), ev.getEntityID()))
        ));

        return added;
    }
    private <T extends JkEntity> TreeMap<DesignField, List<JkEntity>> findChildEntities(T entity) {
        TreeMap<DesignField, List<JkEntity>> toRet = new TreeMap<>();

        List<DesignField> dfields = JkStreams.filter(designFields.get(entity.getClass()).values(), DesignField::isFlatJkEntity);
        for(DesignField df : dfields) {
            toRet.putIfAbsent(df, new ArrayList<>());
            Object value = df.getValue(entity);
            List<JkEntity> deps = new ArrayList<>();
            if(df.isCollection()) {
                deps.addAll((Collection<JkEntity>) value);
            } else {
                deps.add((JkEntity) value);
            }
            toRet.get(df).addAll(deps);
        }

        return toRet;
    }

    private TreeSet<JkEntity> getAllEntities() {
        List<JkEntity> collect = dataSets.values().stream().flatMap(Set::stream).collect(Collectors.toList());
        return JkConvert.toTreeSet(collect);
    }


    public void setDbSequence(long dbSequence) {
        this.dbSequence.set(dbSequence);
    }

    public void setDataSets(TreeMap<Class<?>, Set<JkEntity>> dataSets) {
        this.dataSets = dataSets;
    }

    public Map<Class<?>, TreeMap<Integer, DesignField>> getDesignFields() {
        return designFields;
    }

    public void setDesignFields(Map<Class<?>, TreeMap<Integer, DesignField>> designFields) {
        this.designFields = designFields;
    }

    public Map<Long, List<ForeignKey>> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(Map<Long, List<ForeignKey>> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public ReentrantReadWriteLock.WriteLock getWriteLock() {
        return dbLock.writeLock();
    }
    public long getSequenceValue() {
        return dbSequence.get();
    }
}
