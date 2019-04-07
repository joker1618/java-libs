package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.engine.RepoManager;
import xxx.joker.libs.repository.entities.RepoProperty;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    private final RepoManager repoManager;

    protected JkRepoFile(Path dbFolder, String dbName, String pkgToScan) {
        this(dbFolder, dbName, pkgToScan, null);
    }
    protected JkRepoFile(Path dbFolder, String dbName, String pkgToScan, String encryptionPwd) {
        this.repoManager = new RepoManager(dbFolder, dbName, pkgToScan, encryptionPwd);
    }

    @Override
    public Set<RepoProperty> getProperties() {
        return getDataSet(RepoProperty.class);
    }

    @Override
    public  <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoManager.getDataSets();
    }

    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return (Set<T>) repoManager.getDataSet(entityClazz);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> List<T> getDataList(Class<T> entityClazz, Predicate<T>... filters) {
        return JkStreams.filter(getDataSet(entityClazz), filters);
    }

    @Override
    @SafeVarargs
    public final <K,T extends RepoEntity> Map<K,T> getDataMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMapSingle(getDataSet(entityClazz), keyMapper, e -> e, filters);
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
        getDataSets().values().forEach(Set::clear);
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
        if(prop != null) {
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
        if(prop != null) {
            getDataSet(RepoProperty.class).remove(prop);
        }
        return prop == null ? null : prop.getValue();
    }

    private RepoProperty retrieveProperty(String propKey) {
        return JkStreams.findUnique(getDataSet(RepoProperty.class), rp -> rp.getKey().equalsIgnoreCase(propKey));
    }
}