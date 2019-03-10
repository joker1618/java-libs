package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.entities.JkRepoProp;
import xxx.joker.libs.repository.managers.RepoManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JkDataRepoFile implements JkDataRepo {

    private static final Logger logger = LoggerFactory.getLogger(JkDataRepoFile.class);

    private final RepoManager repoManager;
    private final Map<String, JkRepoProp> properties;

    protected JkDataRepoFile(Path dbFolder, String dbName, String pkgToScan) {
        logger.info("Creating repository: dbName={}, dbFolder={}, pkgToScan={}", dbName, dbFolder, pkgToScan);
        List<Class<?>> eclasses = findPackageEntities(pkgToScan);
        this.repoManager = new RepoManager(dbFolder, dbName, eclasses);
        this.properties = JkStreams.toMapSingle(getDataSet(JkRepoProp.class), JkRepoProp::getKey);
    }

    @Override
    public <T extends JkEntity> T getEntity(long entityID) {
        return repoManager.getEntity(entityID);
    }

    @Override
    public <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return repoManager.getDataSet(entityClazz);
    }

    @Override
    public void commit() {
        repoManager.commitDataSets();
        logger.info("Repo committed");
    }

    @Override
    public String getProperty(String propKey) {
        return getProperty(propKey, null);
    }

    @Override
    public String getProperty(String propKey, String _default) {
        synchronized (properties) {
            String val = properties.get(propKey).getValue();
            return val == null ? _default : val;
        }
    }

    @Override
    public void setProperty(String propKey, String propValue) {
        synchronized (properties) {
            if (!properties.containsKey(propKey)) {
                JkRepoProp prop = new JkRepoProp(propKey, propValue);
                properties.put(propKey, prop);
                getDataSet(JkRepoProp.class).add(prop);
            }
            properties.get(propKey).setValue(propValue);
        }
    }

    @Override
    public String removeProperty(String propKey) {
        synchronized (properties) {
            JkRepoProp prop = properties.remove(propKey);
            return prop == null ? null : prop.getValue();
        }
    }


    protected Map<Class<?>, Set<JkEntity>> getDataSets() {
        return repoManager.getDataSets();
    }

    private List<Class<?>> findPackageEntities(String pkgToScan) {
        logger.debug("Scanning package {}", pkgToScan);
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, JkEntity.class));
        classes.add(JkRepoProp.class);
        if(logger.isDebugEnabled()) {
            classes.forEach(c -> logger.debug("Found entity: {}", c.getSimpleName()));
        }
        return classes;
    }

}
