package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.RepoProperty;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RepoManager {

    private static final Logger LOG = LoggerFactory.getLogger(RepoManager.class);

    private ReadWriteLock repoLock;
    private RepoDAO repoDao;
    private RepoHandler repoHandler;


    public RepoManager(Path dbFolder, String dbName, String pkgToScan) {
        this(dbFolder, dbName, pkgToScan, null);
    }

    public RepoManager(Path dbFolder, String dbName, String pkgToScan, String encryptionPwd) {
        JkTimer timer = new JkTimer();
        if(StringUtils.isNotBlank(encryptionPwd)) {
            this.repoDao = new RepoDAOEncrypted(dbFolder, dbName, scanPackage(pkgToScan), encryptionPwd);
        } else {
            this.repoDao = new RepoDAO(dbFolder, dbName, scanPackage(pkgToScan));
        }
        this.repoLock = new ReentrantReadWriteLock(true);
        this.repoHandler = new RepoHandler(repoDao.readRepoData(), repoLock);
        LOG.info("Initialized repo [{}, {}] in {}", dbFolder, dbName, timer.toStringElapsed());
    }

    public void rollback() {
        try {
            repoLock.writeLock().lock();
            List<RepoDTO> daoDTOs = repoDao.readRepoData();
            repoHandler = new RepoHandler(daoDTOs, repoLock);
            LOG.info("Rollback repo completed");
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public void commit() {
        try {
            repoLock.writeLock().lock();
            JkTimer timer = new JkTimer();
            List<RepoDTO> handlerDTOs = repoHandler.getRepoEntityDTOs();
            repoDao.saveRepoData(handlerDTOs);
            LOG.info("Committed repo in {}", timer.toStringElapsed());
        } finally {
            repoLock.writeLock().unlock();
        }
    }

    public Set<RepoEntity> getDataSet(Class<?> entityClazz) {
        return repoHandler.getDataSet(entityClazz);
    }

    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoHandler.getDataSets();
    }

    public void clearDataSets() {
        repoHandler.clearDataSets();
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
