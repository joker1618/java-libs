package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.entities2.RepoProperty;

import java.nio.file.Path;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConsole.display;

public abstract class X_RepoFile2 implements X_Repo2 {

    private static final Logger LOG = LoggerFactory.getLogger(X_RepoFile2.class);

//    private final RepoManager repoManager;
//    private final Map<String, JkRepoProperty> properties;

    protected X_RepoFile2(Path dbFolder, String dbName, String pkgToScan) {
//        JkTimer timer = new JkTimer();
//        LOG.info("Creating repository: dbName={}, dbFolder={}, pkgToScan={}", dbName, dbFolder, pkgToScan);
//        List<Class<?>> eclasses = findPackageEntities(pkgToScan);
//        this.repoManager = new RepoManager(dbFolder, dbName, eclasses);
//        this.properties = JkStreams.toMapSingle(getEntities(JkRepoProperty.class), JkRepoProperty::getKey, JkRepoProperty::getValue);
//        LOG.debug("Repository created in {}", timer.toStringElapsed());


    }


    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        // todo impl
        return null;
    }

    @Override
    public void rollback() {
        // todo impl

    }

    @Override
    public void commit() {
        // todo impl

    }

    @Override
    public Set<RepoProperty> getProperties() {
        // todo impl
        return null;
    }

    @Override
    public String getProperty(String propKey) {
        // todo impl
        return null;
    }

    @Override
    public String getProperty(String propKey, String _default) {
        // todo impl
        return null;
    }

    @Override
    public void setProperty(String propKey, String propValue) {
        // todo impl

    }

    @Override
    public String delProperty(String propKey) {
        // todo impl
        return null;
    }
}