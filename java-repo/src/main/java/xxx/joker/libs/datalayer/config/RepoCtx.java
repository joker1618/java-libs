package xxx.joker.libs.datalayer.config;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.datalayer.wrapper.ClazzWrap;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.datalayer.config.RepoConfig.*;

public class RepoCtx {

    private Path repoFolder;
    private String dbName;
    private Map<Class<?>, ClazzWrap> clazzWraps;
    private String encrPwd;

    private ReadWriteLock repoLock;

    public RepoCtx(Path repoFolder, String dbName, Collection<Class<?>> classes) {
        this(repoFolder, dbName, classes, null);
    }
    public RepoCtx(Path repoFolder, String dbName, Collection<Class<?>> classes, String encrPwd) {
        this.repoFolder = repoFolder;
        this.dbName = dbName;
        this.clazzWraps = JkStreams.toMapSingle(classes, c -> c, ClazzWrap::new);
        this.repoLock = new ReentrantReadWriteLock(true);
        this.encrPwd = encrPwd;
    }

    public String getEncrPwd() {
        return encrPwd;
    }

    public Path getRepoFolder() {
        return repoFolder;
    }
    public Path getDbFolder() {
        return repoFolder.resolve(FOLDER_DB);
    }
    public Path getMetadataFolder() {
        return repoFolder.resolve(FOLDER_METADATA);
    }
    public Path getResourcesFolder() {
        return repoFolder.resolve(FOLDER_RESOURCES);
    }
    public Path getDecryptFolder() {
        return repoFolder.resolve(FOLDER_DECRYPTED);
    }

    /**
     * This folder is used to prevent to loose resources when the repo is non committed.
     * The resources deleted are moved here (are not actually deleted).
     * This folder is deleted when the repo is committed.
     * On startup, must be used to recover the resources deleted but not committed, that so are still presents in the REPO.
     */
    public Path getUncommittedDeletedResourcesFolder() {
        return repoFolder.resolve(FOLDER_UNCOMMITTED_DEL_RES);
    }
    public Path getTempFolder() {
        return repoFolder.resolve(FOLDER_TEMP);
    }

    public String getDbName() {
        return dbName;
    }

    public Map<Class<?>, ClazzWrap> getClazzWraps() {
        return clazzWraps;
    }

    public Lock getReadLock() {
        return repoLock.readLock();
    }
    public Lock getWriteLock() {
        return repoLock.writeLock();
    }

    public boolean isEntityFilePath(Path fpath) {
        String fn = fpath.getFileName().toString();
        return fn.startsWith(dbName) && fn.contains(DB_JKREPO_KEYWORD);
    }
    public Path getEntityDataPath(ClazzWrap clazzWrap) {
        return getEntityDataPath(clazzWrap.getEClazz().getSimpleName());
    }
    public Path getEntityDataPath(String clazzSimpleName) {
        return getEntityPath(clazzSimpleName, DB_EXT_DATA_FILE);
    }
    public Path getForeignKeysPath() {
        return getDbFolder().resolve(strf(DB_FKEYS_FORMAT, getDbName()));
    }
    public Path getMetadataPath(ClazzWrap cw) {
        return getMetadataPath(cw.getEClazz().getSimpleName());
    }
     public Path getMetadataPath(String clazzSimpleName) {
        return getMetadataFolder().resolve(strf(METADATA_FILENAME_FORMAT, getDbName(), clazzSimpleName));
    }
    private Path getEntityPath(String clazzSimpleName, String extension) {
        String fname = strf(DB_FILENAME_FORMAT, dbName, clazzSimpleName, extension);
        return getDbFolder().resolve(fname);
    }
}
