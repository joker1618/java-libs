package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.entities.JkRepoProperty;
import xxx.joker.libs.repository.managers.RepoManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    private final RepoManager repoManager;
    private final Map<String, JkRepoProperty> properties;

    protected JkRepoFile(Path dbFolder, String dbName, String pkgToScan) {
        JkTimer timer = new JkTimer();
        LOG.info("Creating repository: dbName={}, dbFolder={}, pkgToScan={}", dbName, dbFolder, pkgToScan);
        List<Class<?>> eclasses = findPackageEntities(pkgToScan);
        this.repoManager = new RepoManager(dbFolder, dbName, eclasses);
        this.properties = JkStreams.toMapSingle(getDataSet(JkRepoProperty.class), JkRepoProperty::getKey);
        LOG.debug("Repository created in {}", timer.toStringElapsed());
    }

    @Override
    public <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return repoManager.getDataSet(entityClazz);
    }

    @Override
    public void commit() {
        JkTimer timer = new JkTimer();
        repoManager.commitDataSets();
        LOG.info("Repo committed in {}", JkDuration.toStringElapsed(timer.elapsed()));
    }

    @Override
    public Set<JkRepoProperty> getProperties() {
        return getDataSet(JkRepoProperty.class);
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
            JkRepoProperty pval = properties.get(propKey);
            if (pval == null) {
                pval = new JkRepoProperty(propKey, propValue);
                properties.put(propKey, pval);
                getDataSet(JkRepoProperty.class).add(pval);
            }
            pval.setValue(propValue);
        }
    }

    @Override
    public String delProperty(String propKey) {
        synchronized (properties) {
            JkRepoProperty prop = properties.remove(propKey);
            return prop == null ? null : prop.getValue();
        }
    }


    protected Map<Class<?>, Set<JkEntity>> getDataSets() {
        return repoManager.getDataSets();
    }

    private List<Class<?>> findPackageEntities(String pkgToScan) {
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, JkEntity.class));
        classes.add(JkRepoProperty.class);
        if(LOG.isDebugEnabled()) {
            classes.forEach(c -> LOG.debug("Found entity: {}", c.getName()));
        }
        return classes;
    }

}
