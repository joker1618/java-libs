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
import xxx.joker.libs.repository.JkRepoFile;
import xxx.joker.libs.repository.config.RepoCtx;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.*;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.repository.entities.RepoMetaData.Attrib;

public class RepoManager {

    private static final Logger LOG = LoggerFactory.getLogger(RepoManager.class);

    private RepoDAO repoDao;
    private RepoHandler repoHandler;

    private final RepoCtx ctx;


    public RepoManager(RepoCtx ctx) {
        this.ctx = ctx;
        initRepoManager();
    }
//    public RepoManager(Path dbFolder, String dbName, Collection<Class<?>> classes) {
//        this(null, dbFolder, dbName, classes);
//    }
//
//    public RepoManager(String encryptionPwd, Path dbFolder, String dbName, Collection<Class<?>> classes) {
//        initRepoManager(dbFolder, dbName, classes, encryptionPwd);
//    }

    private void initRepoManager() {
        JkTimer timer = new JkTimer();

        Map<Class<?>, ClazzWrapper> cwMap = ctx.getEClasses();
        getCommonEntityClasses().forEach(c -> cwMap.put(c, new ClazzWrapper(c, ctx)));

        if(LOG.isDebugEnabled()) {
            cwMap.keySet().forEach(c -> LOG.debug("Repo entity class: {}", c.getName()));
        }

        if(StringUtils.isNotBlank(ctx.getEncryptionPwd())) {
            this.repoDao = new RepoDAOEncrypted(ctx);
        } else {
            this.repoDao = new RepoDAO(ctx);
        }

        this.repoHandler = new RepoHandler(repoDao.readRepoData(), ctx);

        LOG.info("Initialized repo [{}, {}] in {}", ctx.getDbFolder(), ctx.getDbName(), timer.toStringElapsed());
    }

    public void rollback() {
        try {
            ctx.getRepoLock().writeLock().lock();
            List<RepoDTO> daoDTOs = repoDao.readRepoData();
            repoHandler = new RepoHandler(daoDTOs, ctx);
            LOG.info("Rollback repo completed");
        } finally {
            ctx.getRepoLock().writeLock().unlock();
        }
    }

    public void commit() {
        try {
            ctx.getRepoLock().writeLock().lock();
            JkTimer timer = new JkTimer();
            List<RepoDTO> handlerDTOs = repoHandler.getRepoEntityDTOs();
            repoDao.saveRepoData(handlerDTOs);
            LOG.info("Committed repo in {}", timer.toStringElapsed());
        } finally {
            ctx.getRepoLock().writeLock().unlock();
        }
    }

    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return (Set<T>) repoHandler.getDataSet(entityClazz);
    }

    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoHandler.getDataSets();
    }

    public TreeMap<Long, RepoEntity> getDataByID() {
        return repoHandler.getDataByID();
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
        Path resBase = ctx.getResourcesFolder();
        Path outPath = resBase.resolve(uriType.name().toLowerCase()).resolve(outName);
        if(!Files.exists(outPath)) {
            if(sourcePath.startsWith(resBase)) {
                JkFiles.move(sourcePath, outPath);
            } else {
                JkFiles.copy(sourcePath, outPath);
            }
            LOG.info("Added new file to resources: {}", sourcePath);
        }

        RepoMetaData md = new RepoMetaData();
        if(uriType == RepoUriType.IMAGE) {
            JkImage img = JkMedia.parseImage(outPath);
            md.add(Attrib.WIDTH, String.valueOf(img.getWidth()));
            md.add(Attrib.HEIGHT, String.valueOf(img.getHeight()));
        }

        RepoUri uri = new RepoUri();
        uri.setMd5(sourceMd5);
        uri.setType(uriType);
        uri.setPath(resBase.relativize(outPath));
        uri.setMetaData(md);

        return uri;
    }

    private List<Class<?>> getCommonEntityClasses() {
        List<Class<?>> classes = new ArrayList<>();
//        classes.add(RepoUri.class);
//        classes.add(RepoProperty.class);
//        classes.add(RepoResource.class);
        classes.addAll(JkRepoFile.scanPackages(RepoManager.class, "xxx.joker.libs.repository.entities"));
        return classes;
    }

}
