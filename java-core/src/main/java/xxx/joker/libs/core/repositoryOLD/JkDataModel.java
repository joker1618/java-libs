package xxx.joker.libs.core.repositoryOLD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.repositoryOLD.entity.JkEntity;
import xxx.joker.libs.core.repositoryOLD.property.JkModelProperty;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@ToAnalyze
@Deprecated
public abstract class JkDataModel {

    private static final Logger logger = LoggerFactory.getLogger(JkDataModel.class);

    private final JkPersistenceManager persistenceManager;
    private final JkEntityManager entityManager;
    private final Map<Class<?>, Set<JkEntity>> dataMap;
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

    private Map<Class<?>, Set<JkEntity>> readModelData() {
        Map<Class<?>, JkPersistenceManager.EntityLines> elinesMap = persistenceManager.readData();
        return entityManager.parseData(elinesMap);
    }

    public void commit() {
        Map<Class<?>, JkPersistenceManager.EntityLines> map = entityManager.formatEntities(dataMap);
        persistenceManager.saveData(map, sequence.get());
        logger.info("Committed model data");
    }

    public Map<Class<?>, Set<JkEntity>> getDataMap() {
        return dataMap;
    }

    public <T extends JkEntity> Set<T> getData(Class<T> entityClazz) {
        Set<JkEntity> data = dataMap.get(entityClazz);
        if(data == null) {
            throw new JkRuntimeException("Class {} does not belong to package {}", entityClazz.getName(), pkgToScan);
        }
        return (Set<T>) data;
    }

    public <T extends JkEntity> List<T> getDataList(Class<T> entityClazz, Predicate<T>... filters) {
        Set<JkEntity> data = dataMap.get(entityClazz);
        if(data == null) {
            throw new JkRuntimeException("Class {} does not belong to package {}", entityClazz.getName(), pkgToScan);
        }
        Set<T> ts = new TreeSet<>((Set<T>) data);
        for(Predicate<T> filter : filters) {
            ts.removeIf(t -> !filter.test(t));
        }
        return new ArrayList<>(ts);
    }

    public <T extends JkEntity> T getDataObject(Class<T> entityClazz, Predicate<T> filter) {
        Set<JkEntity> data = dataMap.get(entityClazz);
        if(data == null) {
            throw new JkRuntimeException("Class {} does not belong to package {}", entityClazz.getName(), pkgToScan);
        }
        List<T> ts = new ArrayList<>((Set<T>) data);
        ts.removeIf(t -> !filter.test(t));
        return ts.size() == 1 ? ts.get(0) : null;
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

    public List<JkModelProperty> getAllProperties() {
        return getDataList(JkModelProperty.class);
    }

    public JkModelProperty getProperty(String propertyKey) {
        List<JkModelProperty> filter = JkStreams.filter(getData(JkModelProperty.class), p -> p.getKey().equalsIgnoreCase(propertyKey));
        return filter.isEmpty() ? null : filter.get(0);
    }

    public void setProperty(String key, String value) {
        setProperty(new JkModelProperty(key, value));
    }

    public void setProperty(JkModelProperty property) {
        JkModelProperty prop = getProperty(property.getKey());
        if(prop == null) {
            getData(JkModelProperty.class).add(property);
        } else {
            prop.setValue(property.getValue());
        }
    }

    private List<Class<?>> retrieveEntityClasses(String pkgToScan) {
        logger.info("Scanning package {}", pkgToScan);
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, JkEntity.class));
        logger.debug("{} JkEntity class found in package {}", classes.size(), pkgToScan);
        classes.add(JkModelProperty.class);
        return classes;
    }

}
