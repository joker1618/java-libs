package xxx.joker.libs.datalayer.resourcer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.core.media.JkMedia;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.entities.*;
import xxx.joker.libs.datalayer.entities.RepoMetaData.Attrib;
import xxx.joker.libs.datalayer.exceptions.RepoError;
import xxx.joker.libs.datalayer.jpa.JpaHandler;

import java.nio.file.Files;
import java.nio.file.Path;
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
            RepoResource res = new RepoResource(resName, tags);
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
            if(foundRes != null && !foundRes.getUri().getMd5().equals(sourceMd5)) {
                throw new RepoError("Another resource with name='{}' and tags='{}' already exists");
            }
            if(foundRes != null) {
                return foundRes;
            }

            // Check if an RepoUri already exists, else create
            RepoUri uri = getOrAddRepoUri(sourcePath, sourceMd5);
            RepoResource repoRes = new RepoResource();
            repoRes.setName(resName);
            repoRes.setTags(tags);
            repoRes.setUri(uri);

            Set<RepoResource> dsRes = jpaHandler.getDataSet(RepoResource.class);
            dsRes.add(repoRes);
            return repoRes;

        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    private RepoUri getOrAddRepoUri(Path sourcePath, String sourceMd5) {
        RepoUriType uriType = RepoUriType.fromExtension(sourcePath);

        String outName = strf("{}{}", sourceMd5, JkFiles.getExtension(sourcePath, true).toLowerCase());
        Path resBase = ctx.getResourcesFolder();
        Path outPath = resBase.resolve(uriType.name().toLowerCase()).resolve(outName);
        if(!Files.exists(outPath)) {
            JkFiles.copy(sourcePath, outPath);
            LOG.info("Copied from [{}] to [{}]", sourcePath, outPath);
        }

        RepoMetaData md = new RepoMetaData();
        if(uriType == RepoUriType.IMAGE) {
            JkImage img = JkMedia.parseImage(outPath);
            md.add(Attrib.WIDTH, img.getWidth());
            md.add(Attrib.HEIGHT, img.getHeight());
        }

        RepoUri uri = new RepoUri();
        uri.setMd5(sourceMd5);
        uri.setType(uriType);
        uri.setPath(resBase.relativize(outPath));
        uri.setMetaData(md);

        Set<RepoUri> dsUri = jpaHandler.getDataSet(RepoUri.class);
        RepoUri finalUri = JkStreams.findUnique(dsUri, uri::equals);
        if(finalUri == null) {
            dsUri.add(uri);
            finalUri = uri;
        }
        return finalUri;
    }

    public void exportResources(Path outFolder) {
        Set<RepoResource> dsResources = jpaHandler.getDataSet(RepoResource.class);
        for (RepoResource res : dsResources) {
            Path source = getUriPath(res);
            String outName = strf("{}/{}/{}{}", res.getUri().getType(), res.getTags().format(), res.getName(), JkFiles.getExtension(source, true));
            JkFiles.copy(source, outFolder.resolve(outName));
        }
    }

    public Path getUriPath(RepoResource resource) {
        Path up = resource.getUri().getPath();
        if(!up.isAbsolute()) {
            up = ctx.getResourcesFolder().resolve(up);
        }
        return up;
    }
}
