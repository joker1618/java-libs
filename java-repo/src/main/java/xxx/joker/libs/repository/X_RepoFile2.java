package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.entities2.RepoProperty;
import xxx.joker.libs.repository.znew.X_RepoManager;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConsole.display;

public abstract class X_RepoFile2 implements X_Repo2 {

    private static final Logger LOG = LoggerFactory.getLogger(X_RepoFile2.class);

    private final X_RepoManager repoManager;

    protected X_RepoFile2(Path dbFolder, String dbName, String pkgToScan) {
//        JkTimer timer = new JkTimer();
//        LOG.info("Creating repository: dbName={}, dbFolder={}, pkgToScan={}", dbName, dbFolder, pkgToScan);
//        List<Class<?>> eclasses = findPackageEntities(pkgToScan);
        this.repoManager = new X_RepoManager(dbFolder, dbName, pkgToScan);
//        this.properties = JkStreams.toMapSingle(getEntities(JkRepoProperty.class), JkRepoProperty::getKey, JkRepoProperty::getValue);
//        LOG.debug("Repository created in {}", timer.toStringElapsed());


    }


    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return (Set<T>) repoManager.getDataSet(entityClazz);
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
        RepoProperty prop = JkStreams.findExactMatch(getDataSet(RepoProperty.class), rp -> rp.getKey().equalsIgnoreCase(propKey));
        return prop == null ? null : prop.getValue();
    }

    @Override
    public String getProperty(String propKey, String _default) {
        String val = getProperty(propKey);
        return val == null ? _default : val;
    }

    @Override
    public void setProperty(String propKey, String propValue) {
        delProperty(propKey);
        getDataSet(RepoProperty.class).add(new RepoProperty(propKey, propValue));
    }

    @Override
    public String delProperty(String propKey) {
        String val = getProperty(propKey);
        if(val != null) {
            getDataSet(RepoProperty.class).removeIf(p -> p.getKey().equalsIgnoreCase(propKey));
        }
        return val;
    }
}