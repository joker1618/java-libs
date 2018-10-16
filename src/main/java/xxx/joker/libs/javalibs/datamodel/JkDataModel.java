package xxx.joker.libs.javalibs.datamodel;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.javalibs.datamodel.entity.JkEntity;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static xxx.joker.libs.javalibs.datamodel.JkPersistenceManager.EntityLines;

public abstract class JkDataModel {

    private static final Logger logger = LoggerFactory.getLogger(JkDataModel.class);

    private final JkPersistenceManager persistenceManager;
    private final JkPersistenceManager pmNewFormat;
    private final JkEntityManager entityManager;
    private final JkEM2 emNewFormat;
    private final Map<Class<?>, TreeSet<JkEntity>> dataMap;
    private final String pkgToScan;

    protected JkDataModel(Path dbFolder, String dbName, String pkgToScan) {
        logger.info("Initializing data model:  [dbName={}] [dbFolder={}] [pkgToScan={}]", dbName, dbFolder, pkgToScan);
        this.pkgToScan = pkgToScan;
        this.entityManager = new JkEntityManager(pkgToScan);
        this.emNewFormat = new JkEM2(pkgToScan);
        this.persistenceManager = new JkPersistenceManager(dbFolder, dbName, entityManager.getEntityClasses());
        this.pmNewFormat = new JkPersistenceManager(dbFolder, dbName+"NEWFMT", entityManager.getEntityClasses());
        this.dataMap = readModelData();
    }

    private Map<Class<?>, TreeSet<JkEntity>> readModelData() {
        Map<Class<?>, EntityLines> elinesMap = persistenceManager.readData();
        return entityManager.parseData(elinesMap);
    }

    protected void commit() {
        Map<Class<?>, EntityLines> map = entityManager.formatData(dataMap);
        persistenceManager.saveData(map);
        logger.info("Committed model data");
    }

    public void commitNewFormat() {
        Map<Class<?>, EntityLines> map = emNewFormat.formatData(dataMap);
        pmNewFormat.saveData(map);
        logger.info("Committed model data NEW FORMAT");
    }

    protected <T extends JkEntity> TreeSet<T> getData(Class<T> entityClazz) {
        TreeSet<JkEntity> data = dataMap.get(entityClazz);
        if(data == null) {
            throw new JkRuntimeException("Class {} does not belong to package {}", entityClazz.getName(), pkgToScan);
        }
        return (TreeSet<T>) data;
    }

    public void cascadeDependencies() {
        dataMap.keySet().forEach(this::cascadeDependencies);
    }

    public void cascadeDependencies(Class<?> clazz) {
        dataMap.get(clazz).forEach(this::cascadeDependencies);
    }

    public void cascadeDependencies(JkEntity entity) {
        Map<Class<?>, Set<JkEntity>> dependencies = entityManager.getDependencies(entity);
        int counter = dependencies.values().stream().mapToInt(Set::size).sum();
        dependencies.forEach((k,v) -> dataMap.get(k).addAll(v));
        dependencies.forEach((k,v) -> v.forEach(this::cascadeDependencies));
        logger.trace("Spread {} broken dependencies for entity {}", counter, entity.getPrimaryKey());
    }

}
