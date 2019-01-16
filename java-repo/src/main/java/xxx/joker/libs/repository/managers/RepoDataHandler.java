package xxx.joker.libs.repository.managers;

import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.repository.design.JkEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

class RepoDataHandler {

    private final ReentrantReadWriteLock dbLock;
    private final AtomicLong dbSequence;

    private TreeMap<Class<?>, Set<JkEntity>> dataSets;

    private Map<Class<?>, List<DesignField>> entityFields;

    // Indexes
    private TreeMap<Long, JkEntity> dataByID;
    private TreeMap<Long, Set<Long>> indexesMap;


    public RepoDataHandler(long sequenceValue, TreeMap<Class<?>, Set<JkEntity>> dataSets, Map<Class<?>, List<DesignField>> entityFields) {
        this.dbLock = new ReentrantReadWriteLock();
        this.dbSequence = new AtomicLong(sequenceValue);
        this.entityFields = entityFields;
        this.dataSets = dataSets;

        this.dataByID = new TreeMap<>();
        this.indexesMap = new TreeMap<>();
        initIndexes();
    }

    public TreeMap<Class<?>, Set<JkEntity>> getDataSets() {
        return dataSets;
    }

    public <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz) {
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
                List<Long> refIDs = JkStreams.filterMap(indexesMap.entrySet(), en -> en.getValue().contains(entityID), Map.Entry::getKey);
                for(Long refID : refIDs) {
                    JkEntity edep = dataByID.get(refID);
                    List<DesignField> dfields = JkStreams.filter(entityFields.get(edep.getClass()), DesignField::isFlatJkEntity);
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
                indexesMap.remove(entityID);
                indexesMap.values().forEach(set -> set.remove(entityID));
            }

            return removed;

        } finally {
            dbLock.writeLock().unlock();
        }
    }


    private void initIndexes() {
        dataSets.values().stream().flatMap(Set::stream).forEach(e -> {
            Long eID = e.getEntityID();
            dataByID.put(eID, e);
            indexesMap.putIfAbsent(eID, new TreeSet<>());
            findChildEntities(e).values().stream().flatMap(List::stream).forEach(ec ->
                indexesMap.get(eID).add(ec.getEntityID())
            );
        });
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
                indexesMap.put(entityID, new TreeSet<>());
            }
        }

        List<JkEntity> depChilds = findChildEntities(entity).values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        indexesMap.get(entityID).addAll(JkStreams.map(depChilds, JkEntity::getEntityID));

        List<JkEntity> newDeps = JkStreams.filter(depChilds, e -> e.getEntityID() == null);
        newDeps.forEach(this::addEntityRecursive);

        return added;
    }
    private <T extends JkEntity> TreeMap<Class<?>, List<JkEntity>> findChildEntities(T entity) {
        TreeMap<Class<?>, List<JkEntity>> toRet = new TreeMap<>(Comparator.comparing(Class::getName));

        List<DesignField> dfields = entityFields.get(entity.getClass());
        for(DesignField df : dfields) {
            toRet.putIfAbsent(df.getFlatFieldType(), new ArrayList<>());
            Object value = df.getValue(entity);
            List<JkEntity> deps = new ArrayList<>();
            if(df.isCollection()) {
                deps.addAll((Collection<JkEntity>) value);
            } else {
                deps.add((JkEntity) value);
            }
            toRet.get(df.getFlatFieldType()).addAll(deps);
        }

        return toRet;
    }

    private TreeSet<JkEntity> getAllEntities() {
        List<JkEntity> collect = dataSets.values().stream().flatMap(Set::stream).collect(Collectors.toList());
        return JkConvert.toTreeSet(collect);
    }
}
