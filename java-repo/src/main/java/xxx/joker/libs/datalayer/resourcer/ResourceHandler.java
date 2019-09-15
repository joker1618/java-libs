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
import java.util.Set;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ResourceHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHandler.class);

    private final RepoCtx ctx;
    private final JpaHandler jpaHandler;

    public ResourceHandler(RepoCtx ctx, JpaHandler jpaHandler) {
        this.ctx = ctx;
        this.jpaHandler = jpaHandler;
    }

    public RepoResource getResource(String resName, RepoTags tags) {
        try {
            ctx.getReadLock().lock();
            Set<RepoResource> dsRes = jpaHandler.getDataSet(RepoResource.class);
            RepoResource res = new RepoResource();
            res.setName(resName);
            res.setTags(tags);
            return JkStreams.findUnique(dsRes, res::equals);

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

            if(resource == null) {
                return false;
            }

            Path resPath = ctx.getResourcesFolder().resolve(resource.getPath());
            JkFiles.delete(resPath);
            jpaHandler.getDataSet(RepoResource.class).remove(resource);
            return true;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    public boolean removeResources(RepoTags tags) {
        try {
            ctx.getWriteLock().lock();

            List<RepoResource> resList = findResources(tags);
            if(resList.isEmpty()) {
                return false;
            }

            for (RepoResource res : resList) {
                Path resPath = ctx.getResourcesFolder().resolve(res.getPath());
                JkFiles.delete(resPath);
                jpaHandler.getDataSet(RepoResource.class).remove(res);
            }
            return true;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    public void exportResources(Path outFolder) {
        Set<RepoResource> dsResources = jpaHandler.getDataSet(RepoResource.class);
        for (RepoResource res : dsResources) {
            String outName = strf("{}/{}/{}{}", res.getType(), res.getTags().format(), res.getName(), JkFiles.getExtension(res.getPath(), true));
            JkFiles.copy(res.getPath(), outFolder.resolve(outName));
        }
    }

    public List<RepoResource> findResources(RepoTags tags) {
        Set<RepoResource> dsRes = jpaHandler.getDataSet(RepoResource.class);
        return JkStreams.filter(dsRes, res -> res.getTags().belongToGroup(tags));
    }
}
