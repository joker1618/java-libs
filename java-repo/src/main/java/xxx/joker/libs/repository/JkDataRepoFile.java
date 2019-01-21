package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.entities.JkRepoProperty;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.managers.RepoManager;

import java.nio.file.Path;
import java.util.*;

public abstract class JkDataRepoFile implements JkDataRepo {

    private static final Logger logger = LoggerFactory.getLogger(JkDataRepoFile.class);

    private RepoManager repoManager;

    protected JkDataRepoFile(Path dbFolder, String dbName, String pkgToScan) {
        logger.info("Creating repository: dbName={}, dbFolder={}, pkgToScan={}", dbName, dbFolder, pkgToScan);
        List<Class<?>> eclasses = findPackageEntities(pkgToScan);
        this.repoManager = new RepoManager(dbFolder, dbName, eclasses);
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

    protected Map<Class<?>, Set<JkEntity>> getDataSets() {
        return repoManager.getDataSets();
    }

    private List<Class<?>> findPackageEntities(String pkgToScan) {
        logger.debug("Scanning package {}", pkgToScan);
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, JkEntity.class));
        classes.add(JkRepoProperty.class);
        if(logger.isDebugEnabled()) {
            classes.forEach(c -> logger.debug("Found entity: {}", c.getSimpleName()));
        }
        return classes;
    }

}
