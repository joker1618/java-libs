package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.core.media.JkMedia;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.config.RepoConfig;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.*;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.repository.entities.RepoMetaData.Attrib;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoManager {

    private static final Logger LOG = LoggerFactory.getLogger(RepoManager.class);

    private ReadWriteLock repoLock;
    private RepoDAO repoDao;
    private RepoHandler repoHandler;
    private Path resourcesFolder;


    public RepoManager(Path dbFolder, String dbName, String... pkgsToScan) {
        this(null, dbFolder, dbName, pkgsToScan);
    }

    public RepoManager(String encryptionPwd, Path dbFolder, String dbName, String... pkgsToScan) {
        JkTimer timer = new JkTimer();

        List<String> toScan = JkConvert.toList(pkgsToScan);
        toScan.add("xxx.joker.libs.repository.entities");

        if(StringUtils.isNotBlank(encryptionPwd)) {
            this.repoDao = new RepoDAOEncrypted(dbFolder, dbName, scanPackage(toScan), encryptionPwd);
        } else {
            this.repoDao = new RepoDAO(dbFolder, dbName, scanPackage(toScan));
        }
        this.repoLock = new ReentrantReadWriteLock(true);
        this.repoHandler = new RepoHandler(repoDao.readRepoData(), repoLock);
        this.resourcesFolder = RepoConfig.getResourcesFolder(dbFolder, dbName);

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

    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return (Set<T>) repoHandler.getDataSet(entityClazz);
    }

    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoHandler.getDataSets();
    }

    public void clearDataSets() {
        repoHandler.clearDataSets();
    }

    public RepoResource getResource(String resName, RepoTags tags) {
        return JkStreams.findUnique(getDataSet(RepoResource.class), rr -> rr.getName().equals(resName) && rr.getTags().equals(tags));
    }

    public RepoResource addResource(Path sourcePath, String resName, RepoTags tags) {
        RepoResource foundRes = getResource(resName, tags);
        String sourceMd5 = JkEncryption.getMD5(sourcePath);
        if(foundRes != null && !foundRes.getRepoURI().getMd5().equals(sourceMd5)) {
            throw new RepoError("Another resource with name='{}' and tags='{}' already exists");
        }

        if(foundRes != null) {
            return foundRes;
        }

        // Check if an RepoUri already exists, else create
        RepoUri uri = JkStreams.findUnique(getDataSet(RepoUri.class), u -> u.getMd5().equals(sourceMd5));
        if(uri == null) {
            uri = addRepoUri(sourcePath, sourceMd5);
            getDataSet(RepoUri.class).add(uri);
        }

        RepoResource repoRes = new RepoResource();
        repoRes.setName(resName);
        repoRes.setTags(tags);
        repoRes.setRepoURI(uri);
        getDataSet(RepoResource.class).add(repoRes);

        return repoRes;
    }
    private RepoUri addRepoUri(Path sourcePath, String sourceMd5) {
        RepoUriType uriType = RepoUriType.fromExtension(sourcePath);

        String outName = strf("{}.{}", sourceMd5, JkFiles.getExtension(sourcePath));
        Path outPath = resourcesFolder.resolve(uriType.name().toLowerCase()).resolve(outName);
        if(!Files.exists(outPath)) {
            if(sourcePath.startsWith(resourcesFolder)) {
                JkFiles.moveFile(sourcePath, outPath);
            } else {
                JkFiles.copyFile(sourcePath, outPath);
            }
            LOG.info("Added new file to resources: {}", sourcePath);
        }

        RepoMetaData md = new RepoMetaData();
        if(uriType == RepoUriType.IMAGE) {
            JkImage img = JkMedia.parseImage(outPath);
            md.addMetaData(Attrib.WIDTH, String.valueOf(img.getWidth()));
            md.addMetaData(Attrib.HEIGHT, String.valueOf(img.getHeight()));
        }

        RepoUri uri = new RepoUri();
        uri.setMd5(sourceMd5);
        uri.setType(uriType);
        uri.setPath(outPath);
        uri.setMetaData(md);

        return uri;
    }

    private List<ClazzWrapper> scanPackage(Collection<String> pkgsToScan) {
        Set<Class<?>> classes = new HashSet<>();

        pkgsToScan.forEach(pkg -> {
            LOG.debug("Scanning package: {}", pkg);
            classes.addAll(JkRuntime.findClasses(pkg));
        });

        classes.removeIf(c -> c.getSuperclass() != RepoEntity.class);
        if(LOG.isDebugEnabled()) {
            classes.forEach(c -> LOG.debug("Found entity: {}", c.getName()));
        }
        return JkStreams.map(classes, ClazzWrapper::get);
    }

}
