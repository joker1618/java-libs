package xxx.joker.libs.core.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.repository.entity.JkEntity;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkStuff;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import static xxx.joker.libs.core.repository.JkPersistenceManager.EntityLines;

public abstract class JkDataModel {

    private static final Logger logger = LoggerFactory.getLogger(JkDataModel.class);

    private final JkPersistenceManager persistenceManager;
    private final JkEntityManager entityManager;
    private final Map<Class<?>, TreeSet<JkEntity>> dataMap;
    private final String pkgToScan;
    private final AtomicLong sequence;

    protected JkDataModel(Path dbFolder, String dbName, String pkgToScan) {
        logger.info("Initializing data model:  [dbName={}] [dbFolder={}] [pkgToScan={}]", dbName, dbFolder, pkgToScan);
        this.pkgToScan = pkgToScan;
        List<Class<?>> classes = retrieveEntityClasses(pkgToScan);
        this.persistenceManager = new JkPersistenceManager(dbFolder, dbName, classes);
        this.sequence = new AtomicLong(persistenceManager.readSequence());
        this.entityManager = new JkEntityManager(classes, sequence);
        this.dataMap = readModelData();
    }

    private Map<Class<?>, TreeSet<JkEntity>> readModelData() {
        Map<Class<?>, EntityLines> elinesMap = persistenceManager.readData();
        return entityManager.parseData(elinesMap);
    }

    public void commit() {
        Map<Class<?>, EntityLines> map = entityManager.formatEntities(dataMap);
        persistenceManager.saveData(map, sequence.get());
        logger.info("Committed model data");
    }

    public <T extends JkEntity> TreeSet<T> getData(Class<T> entityClazz) {
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

    private List<Class<?>> retrieveEntityClasses(String pkgToScan) {
        logger.info("Scanning package {}", pkgToScan);
        List<Class<?>> classes = JkStuff.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isOfType(c, JkEntity.class));
        if(classes.isEmpty()) {
            throw new JkRuntimeException("No JkEntity class found in package {}", pkgToScan);
        }
        logger.info("{} JkEntity class found in package {}", classes.size(), pkgToScan);
        return classes;
    }

}
