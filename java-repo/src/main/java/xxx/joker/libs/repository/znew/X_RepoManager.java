package xxx.joker.libs.repository.znew;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.entities2.RepoProperty;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class X_RepoManager {

    private static final Logger LOG = LoggerFactory.getLogger(X_RepoManager.class);

    private static final String EXT_REPO_FILE = "jkrepo";
    private static final String EXT_DEPS_FILE = "jkdeps";
    private static final String EXT_CLASS_DESCR_FILE = "jkdescr";
    private static final String EXT_SEQUENCE_FILE = "jkseq";

    private ReadWriteLock repoLock;
    private X_RepoDAO repoDao;
    private X_RepoHandler repoHandler;



    public X_RepoManager(Path dbFolder, String dbName, String pkgToScan) {
        this.repoLock = new ReentrantReadWriteLock(true);
//        JkTimer timer = new JkTimer();
//        this.designService = new DesignService(classes);
//        logger.debug("Entity classes parsed in {}", timer.toStringElapsed());
//        this.repoHandler = readRepoData(classes);
//        logger.debug("Repo data loaded in {}", timer.toStringElapsed());
        this.repoDao = new X_RepoDAO(dbFolder, dbName, scanPackage(pkgToScan));
        this.repoLock = new ReentrantReadWriteLock(true);
        this.repoHandler = new X_RepoHandler(repoDao.readRepoData(), repoLock);
    }

    public void rollback() {
        try {
            repoLock.writeLock().lock();
            List<X_RepoEntityDTO> daoDTOs = repoDao.readRepoData();
            repoHandler = new X_RepoHandler(daoDTOs, repoLock);
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public void commit() {
        try {
            repoLock.writeLock().lock();
            List<X_RepoEntityDTO> handlerDTOs = repoHandler.getRepoEntityDTOs();
            repoDao.saveRepoData(handlerDTOs);
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public Set<RepoEntity> getDataSet(Class<?> entityClazz) {
        return repoHandler.getDataSet(entityClazz);
    }


    private List<X_RepoClazz> scanPackage(String pkgToScan) {
        LOG.debug("Scanning package: {}", pkgToScan);
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> c.getSuperclass() != RepoEntity.class);
        classes.add(RepoProperty.class);
        if(LOG.isDebugEnabled()) {
            classes.forEach(c -> LOG.debug("Found entity: {}", c.getName()));
        }
        return JkStreams.map(classes, X_RepoClazz::wrap);
    }

}
