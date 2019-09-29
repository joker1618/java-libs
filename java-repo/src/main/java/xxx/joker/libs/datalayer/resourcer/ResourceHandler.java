package xxx.joker.libs.datalayer.resourcer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.entities.RepoResource;
import xxx.joker.libs.datalayer.entities.RepoResourceType;
import xxx.joker.libs.datalayer.entities.RepoTags;
import xxx.joker.libs.datalayer.exceptions.RepoError;
import xxx.joker.libs.datalayer.jpa.JpaHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ResourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHandler.class);

    private final RepoCtx ctx;
    private final JpaHandler jpaHandler;

    public ResourceHandler(RepoCtx ctx, JpaHandler jpaHandler) {
        this.ctx = ctx;
        this.jpaHandler = jpaHandler;
        performResourcesCheck();
    }

    public void performResourcesCheck() {
        Set<RepoResource> dsResources = jpaHandler.getDataSet(RepoResource.class);
        List<Path> resPaths = JkStreams.mapUniq(dsResources, RepoResource::getPath);

        // Restore resources that were previously deleted, but for any reason the repo was not committed (probably a failure)
        // In this case the resources are still presents in the repo data set, and the files are located in 'RepoConfig.FOLDER_UNCOMMITTED_DEL_RES'.
        List<RepoResource> toRestoreList = JkStreams.filter(dsResources, res -> !Files.exists(res.getPath()));
        for (RepoResource res : toRestoreList) {
            Path from = ctx.getUncommittedDeletedResourcesFolder().resolve(res.getPath().getFileName());
            if(Files.exists(from)) {
                JkFiles.move(from, res.getPath());
                LOG.info("Restored file for resource {}", res);
            } else {
                dsResources.remove(res);
                LOG.info("Removed resource {}: file not found, unable to restore", res);
            }
        }

        // Move the files that are not associated to any resource to 'RepoConfig.FOLDER_UNCOMMITTED_DEL_RES'.
        List<Path> notUsedList = JkFiles.findFiles(ctx.getResourcesFolder(), true, p -> !resPaths.contains(p));
        for (Path path : notUsedList) {
            JkFiles.moveInFolder(path, ctx.getUncommittedDeletedResourcesFolder());
            LOG.info("Deleted unused file {}", path);
        }
    }

    public RepoResource getResource(String resName, RepoTags tags) {
        try {
            ctx.getReadLock().lock();
            RepoResource res = new RepoResource();
            res.setName(resName);
            res.setTags(tags);
            return jpaHandler.get(RepoResource.class, res::equals);

        } finally {
            ctx.getReadLock().unlock();
        }
    }

    public RepoResource addResource(Path sourcePath, String resName, RepoTags tags) {
        try {
            ctx.getWriteLock().lock();

            RepoResource foundRes = getResource(resName, tags);
            String sourceMd5 = JkEncryption.getMD5(sourcePath);
            if(foundRes != null) {
                if(!foundRes.getMd5().equals(sourceMd5)) {
                    throw new RepoError("Another resource with name='{}' and tags='{}' already exists");
                }
                return foundRes;
            }

            RepoResourceType resType = RepoResourceType.fromExtension(sourcePath);

            String outName = strf("{}{}", sourceMd5, JkFiles.getExtension(sourcePath, true).toLowerCase());
            Path resBase = ctx.getResourcesFolder();
            Path outPath = resBase.resolve(resType.name().toLowerCase()).resolve(outName);
            if(!Files.exists(outPath)) {
                JkFiles.copy(sourcePath, outPath);
                LOG.info("New resource added: from [{}] to [{}]", sourcePath, outPath);
            }

            RepoResource repoRes = new RepoResource();
            repoRes.setPath(outPath);
            repoRes.setName(resName);
            repoRes.setTags(tags);
            repoRes.setMd5(sourceMd5);
            repoRes.setType(resType);

            Set<RepoResource> dsRes = jpaHandler.getDataSet(RepoResource.class);
            dsRes.add(repoRes);
            return repoRes;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    public boolean removeResource(String resName, RepoTags tags) {
        return removeResource(getResource(resName, tags));
    }
    public boolean removeResource(RepoResource resource) {
        try {
            ctx.getWriteLock().lock();
            return removeResource1(resource);
        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    public boolean removeResources(RepoTags tags) {
        try {
            ctx.getWriteLock().lock();
            List<RepoResource> resList = findResources(tags);
            boolean result = false;
            for (RepoResource res : resList) {
                result |= removeResource1(res);
            }
            return result;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }
    private boolean removeResource1(RepoResource resource) {
        if(resource == null) {
            return false;
        }
        Path resPath = ctx.getResourcesFolder().resolve(resource.getPath());
        JkFiles.moveInFolder(resPath, ctx.getUncommittedDeletedResourcesFolder());
        jpaHandler.getDataSet(RepoResource.class).remove(resource);
        return true;
    }

    public void exportResources(Path outFolder, RepoTags tags) {
        List<RepoResource> resources = findResources(tags);
        for (RepoResource res : resources) {
            String outName = strf("{}/{}/{}{}", res.getType(), res.getTags().format(), res.getName(), JkFiles.getExtension(res.getPath(), true));
            JkFiles.copy(res.getPath(), outFolder.resolve(outName));
        }
    }

    public List<RepoResource> findResources(RepoTags tags) {
        Set<RepoResource> dsRes = jpaHandler.getDataSet(RepoResource.class);
        return JkStreams.filter(dsRes, res -> res.getTags().belongToGroup(tags));
    }

    public void commitChanges() {
        Path toDel = ctx.getUncommittedDeletedResourcesFolder();
        if(Files.exists(toDel)) {
            JkFiles.delete(toDel);
            LOG.debug("Deleted folder {}", toDel);
        }
    }

}
