package xxx.joker.libs.repository.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.JkEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

class RepoDataHandler {

    private static final Logger logger = LoggerFactory.getLogger(RepoDataHandler.class);


    private AtomicLong dbSequence;
    private Map<Class<?>, Set<JkEntity>> dataSets;
    private Map<Class<?>, List<DesignField>> designEntities;
    private Map<Long, JkEntity> dataByID;
    private Map<Long, List<ForeignKey>> foreignKeys;
    private boolean initialized;

    public RepoDataHandler() {
        this.initialized = false;
    }

    protected void initHandler(long sequenceValue,
                               Map<Class<?>, Set<JkEntity>> dataSets,
                               Map<Class<?>, TreeMap<Integer, DesignField>> allDesignMap) {

        if(initialized)     throw new JkRuntimeException("Repo data handler already initialized");

        logger.debug("initialize repo data handler");

        this.dbSequence = new AtomicLong(sequenceValue);
        this.dataSets = dataSets;

        this.designEntities = new HashMap<>();
        allDesignMap.forEach((c,tm) -> designEntities.put(c, JkConvert.toArrayList(tm.values())));
        this.designEntities.forEach((c,l) -> l.removeIf(df -> !df.isFlatJkEntity()));

        this.dataByID = new TreeMap<>();
        dataSets.values().stream().flatMap(Set::stream).forEach(e -> dataByID.put(e.getEntityID(), e));

        this.foreignKeys = new TreeMap<>();
        for(JkEntity e : dataByID.values()) {
            foreignKeys.putIfAbsent(e.getEntityID(), findForeignKeys(e));
        }

        initialized = true;
    }

    public Map<Class<?>, Set<JkEntity>> getDataSets() {
        return dataSets;
    }


    public <T extends JkEntity> Set<T> getDataSet(Class<?> entityClazz) {
        return dataSets.containsKey(entityClazz) ? (Set<T>) dataSets.get(entityClazz) : null;
    }

    public <T extends JkEntity> boolean setIdAndCheckIfExists(T entity) {
        if(entity.isRegistered())    return true;

        entity.setEntityID(dbSequence.get());
        entity.setInsertTstamp(LocalDateTime.now());

        if(dataSets.get(entity.getClass()).contains(entity)) {
            entity.setEntityID(null);
            return true;
        }

        dataByID.put(entity.getEntityID(), entity);
        foreignKeys.put(entity.getEntityID(), new ArrayList<>());
        dbSequence.incrementAndGet();
        return false;
    }

    public <T extends JkEntity> void addDependencies(T entity) {
        List<JkEntity> children = findChildEntities(entity);
        for(JkEntity child : children) {
            dataSets.get(entity.getClass()).add(child);
        }

        List<ForeignKey> fklist = foreignKeys.get(entity.getEntityID());
        fklist.clear();
        fklist.addAll(findForeignKeys(entity));
    }

    public <T extends JkEntity> boolean addEntity(T entity) {
        return dataSets.get(entity.getClass()).add(entity);
    }



    public <T extends JkEntity> void removeFromDependencies(T entity) {
        if(!entity.isRegistered())   return;

        Long entityID = entity.getEntityID();

        dataByID.remove(entityID);
        foreignKeys.remove(entityID);

        List<Long> refIDs = JkStreams.filterMap(foreignKeys.entrySet(), en -> en.getValue().contains(entityID), Map.Entry::getKey);
        for(Long refID : refIDs) {
            JkEntity edep = dataByID.get(refID);
            List<DesignField> dfields = designEntities.get(edep.getClass());
            for(DesignField df : dfields) {
                if(df.isCollection()) {
                    Collection<JkEntity> coll = (Collection<JkEntity>) df.getValue(edep);
                    coll.removeIf(et -> et.getEntityID() == entityID);
                } else {
                    JkEntity e = (JkEntity) df.getValue(edep);
                    if(e.getEntityID() == entityID) {
                        df.setValue(edep, null);
                    }
                }
            }
        }
    }


    private <T extends JkEntity> List<JkEntity> findChildEntities(T entity) {
        List<JkEntity> toRet = new ArrayList<>();

        List<DesignField> dfields = designEntities.get(entity.getClass());
        for(DesignField df : dfields) {
            Object value = df.getValue(entity);
            if(df.isCollection()) {
                toRet.addAll((Collection<JkEntity>) value);
            } else {
                toRet.add((JkEntity) value);
            }
        }

        return toRet;
    }

    public Map<Long, List<ForeignKey>> getForeignKeys() {
        return foreignKeys;
    }

    private <T extends JkEntity> List<ForeignKey> findForeignKeys(T entity) {
        List<ForeignKey> toRet = new ArrayList<>();
        Long entityID = entity.getEntityID();
        List<DesignField> dfields = designEntities.get(entity.getClass());

        for(DesignField df : dfields) {
            if(df.isCollection()) {
                Collection<JkEntity> coll = (Collection<JkEntity>) df.getValue(entity);
                List<ForeignKey> fkList = JkStreams.map(coll, c -> new ForeignKey(entityID, df.getIdx(), c.getEntityID()));
                toRet.addAll(fkList);
            } else {
                Long depID = ((JkEntity) df.getValue(entity)).getEntityID();
                ForeignKey fk = new ForeignKey(entityID, df.getIdx(), depID);
                toRet.add(fk);
            }
        }

        return toRet;
    }

    protected long getDbSequenceValue() {
        return dbSequence.get();
    }
    protected void incrementDbSequence() {
        dbSequence.incrementAndGet();
    }
}
