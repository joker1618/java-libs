package xxx.joker.libs.repository.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.RepoProperty;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class RepoManager {

    private static final Logger LOG = LoggerFactory.getLogger(RepoManager.class);

    private static final String EXT_REPO_FILE = "jkrepo";
    private static final String EXT_DEPS_FILE = "jkdeps";
    private static final String EXT_CLASS_DESCR_FILE = "jkdescr";
    private static final String EXT_SEQUENCE_FILE = "jkseq";

    private ReadWriteLock repoLock;
    private RepoDAO repoDao;
    private RepoHandler repoHandler;



    public RepoManager(Path dbFolder, String dbName, String pkgToScan) {
        this.repoLock = new ReentrantReadWriteLock(true);
//        JkTimer timer = new JkTimer();
//        this.designService = new DesignService(classes);
//        logger.debug("Entity classes parsed in {}", timer.toStringElapsed());
//        this.repoHandler = readRepoData(classes);
//        logger.debug("Repo data loaded in {}", timer.toStringElapsed());
        this.repoDao = new RepoDAO(dbFolder, dbName, scanPackage(pkgToScan));
        this.repoLock = new ReentrantReadWriteLock(true);
        this.repoHandler = new RepoHandler(repoDao.readRepoData(), repoLock);
    }

    public void rollback() {
        try {
            repoLock.writeLock().lock();
            List<RepoDTO> daoDTOs = repoDao.readRepoData();
            repoHandler = new RepoHandler(daoDTOs, repoLock);
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public void commit() {
        try {
            repoLock.writeLock().lock();
            List<RepoDTO> handlerDTOs = repoHandler.getRepoEntityDTOs();
            repoDao.saveRepoData(handlerDTOs);
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public Set<RepoEntity> getDataSet(Class<?> entityClazz) {
        return repoHandler.getDataSet(entityClazz);
    }


    private List<ClazzWrapper> scanPackage(String pkgToScan) {
        LOG.debug("Scanning package: {}", pkgToScan);
        List<Class<?>> classes = JkRuntime.findClasses(pkgToScan);
        classes.removeIf(c -> c.getSuperclass() != RepoEntity.class);
        classes.add(RepoProperty.class);
        if(LOG.isDebugEnabled()) {
            classes.forEach(c -> LOG.debug("Found entity: {}", c.getName()));
        }
        return JkStreams.map(classes, ClazzWrapper::wrap);
    }

}
