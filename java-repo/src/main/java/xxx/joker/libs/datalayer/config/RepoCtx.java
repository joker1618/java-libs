package xxx.joker.libs.datalayer.config;

import xxx.joker.libs.core.files.JkZip;
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

    public Path getDecryptFolder() {
        return repoFolder.resolve("decrypted");
    }

    public Path getRepoFolder() {
        return repoFolder;
    }
    public Path getDbFolder() {
        return repoFolder.resolve(DB_FOLDER_NAME);
    }
    public Path getResourcesFolder() {
        return repoFolder.resolve(RESOURCES_FOLDER_NAME);
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
        return getEntityPath(clazzWrap, DB_EXT_DATA_FILE);
    }
    public Path getForeignKeysPath() {
        return getDbFolder().resolve(strf(DB_FKEYS_FORMAT, getDbName()));
    }
    public Path getEntityDescrPath(ClazzWrap clazzWrap) {
        return getEntityPath(clazzWrap, DB_EXT_DESCR_FILE);
    }
    private Path getEntityPath(ClazzWrap clazzWrap, String extension) {
        String fname = strf(DB_FILENAME_FORMAT, dbName, clazzWrap.getEClazz().getSimpleName(), extension);
        return getDbFolder().resolve(fname);
    }
}
