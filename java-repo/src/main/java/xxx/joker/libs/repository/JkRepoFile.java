package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.engine.RepoManager;
import xxx.joker.libs.repository.entities.RepoProperty;
import xxx.joker.libs.repository.entities.RepoResource;
import xxx.joker.libs.repository.entities.RepoTags;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    private final RepoManager repoManager;

    protected JkRepoFile(Path dbFolder, String dbName, String... pkgsToScan) {
        this(null, dbFolder, dbName, pkgsToScan);
    }

    protected JkRepoFile(Path dbFolder, String dbName, Collection<Class<?>> classes) {
        this(null, dbFolder, dbName, classes);
    }

    protected JkRepoFile(String encryptionPwd, Path dbFolder, String dbName, String... pkgsToScan) {
        this.repoManager = new RepoManager(encryptionPwd, dbFolder, dbName, scanPackages(pkgsToScan));
    }

    protected JkRepoFile(String encryptionPwd, Path dbFolder, String dbName, Collection<Class<?>> classes) {
        this.repoManager = new RepoManager(encryptionPwd, dbFolder, dbName, classes);
    }


    @Override
    public Set<RepoProperty> getProperties() {
        return getDataSet(RepoProperty.class);
    }

    @Override
    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoManager.getDataSets();
    }

    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return repoManager.getDataSet(entityClazz);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> List<T> getDataList(Class<T> entityClazz, Predicate<T>... filters) {
        return JkStreams.filter(getDataSet(entityClazz), filters);
    }

    @Override
    @SafeVarargs
    public final <K, T extends RepoEntity> Map<K, T> getDataMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMapSingle(getDataSet(entityClazz), keyMapper, e -> e, filters);
    }

    @Override
    public <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters) {
        List<T> dataList = getDataList(entityClazz, filters);
        return dataList.size() == 1 ? dataList.get(0) : null;
    }

    @Override
    public <T extends RepoEntity> T get(T entity) {
        Set<T> dataSet = (Set<T>) getDataSet(entity.getClass());
        return JkStreams.findUnique(dataSet, entity::equals);
    }

    @Override
    public <T extends RepoEntity> T getOrAdd(T entity) {
        T found = get(entity);
        if (found == null) {
            add(entity);
            found = entity;
        }
        return found;
    }

    @Override
    public <T extends RepoEntity> boolean add(T toAdd) {
        Set<T> dataSet = (Set<T>) getDataSet(toAdd.getClass());
        return dataSet.add(toAdd);
    }

    @Override
    public <T extends RepoEntity> boolean remove(T toRemove) {
        return getDataSet(toRemove.getClass()).remove(toRemove);
    }

    @Override
    public void clearDataSets() {
        repoManager.clearDataSets();
    }

    @Override
    public void rollback() {
        repoManager.rollback();
    }

    @Override
    public void commit() {
        repoManager.commit();
    }

    @Override
    public String getProperty(String propKey) {
        RepoProperty prop = retrieveProperty(propKey);
        return prop == null ? null : prop.getValue();
    }

    @Override
    public String getProperty(String propKey, String _default) {
        String val = getProperty(propKey);
        return val == null ? _default : val;
    }

    @Override
    public String setProperty(String propKey, String propValue) {
        RepoProperty prop = retrieveProperty(propKey);
        String oldValue = prop == null ? null : prop.getValue();
        if (prop != null) {
            prop.setValue(propValue);
        } else {
            prop = new RepoProperty(propKey, propValue);
            getDataSet(RepoProperty.class).add(prop);
        }
        return oldValue;
    }

    @Override
    public String delProperty(String propKey) {
        RepoProperty prop = retrieveProperty(propKey);
        if (prop != null) {
            getDataSet(RepoProperty.class).remove(prop);
        }
        return prop == null ? null : prop.getValue();
    }

    private RepoProperty retrieveProperty(String propKey) {
        return JkStreams.findUnique(getDataSet(RepoProperty.class), rp -> rp.getKey().equalsIgnoreCase(propKey));
    }

    @Override
    public RepoResource getResource(String resName, String... tags) {
        return repoManager.getResource(resName, RepoTags.of(tags));
    }

    @Override
    public RepoResource addResource(Path sourcePath, String resName, String... tags) {
        return repoManager.addResource(sourcePath, resName, RepoTags.of(tags));
    }

    private List<Class<?>> scanPackages(String[] pkgsArr) {
        Set<Class<?>> classes = new HashSet<>();

        List<String> pkgsToScan = JkConvert.toList(pkgsArr);
        pkgsToScan.forEach(pkg -> classes.addAll(JkRuntime.findClasses(pkg)));
        classes.removeIf(c -> c.getSuperclass() != RepoEntity.class);

        return JkConvert.toList(classes);
    }


}