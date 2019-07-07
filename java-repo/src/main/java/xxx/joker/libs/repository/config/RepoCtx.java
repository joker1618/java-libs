package xxx.joker.libs.repository.config;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.engine.ClazzWrapper;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoCtx {

    private Path repoFolder;
    private String dbName;
    private Map<Class<?>, ClazzWrapper> eClasses;
    private String encryptionPwd;

    private ReadWriteLock repoLock;

    public RepoCtx(Path repoFolder, String dbName, Collection<Class<?>> eClasses) {
        this.repoFolder = repoFolder;
        this.dbName = dbName;
        this.eClasses = new HashMap<>(JkStreams.toMapSingle(eClasses, c -> c, c -> new ClazzWrapper(c, this)));
        this.repoLock = new ReentrantReadWriteLock(true);
    }

    public RepoCtx(Path repoFolder, String dbName, Collection<Class<?>> eClasses, String encryptionPwd) {
        this.repoFolder = repoFolder;
        this.dbName = dbName;
        this.eClasses = new HashMap<>(JkStreams.toMapSingle(eClasses, c -> c, c -> new ClazzWrapper(c, this)));
        this.repoLock = new ReentrantReadWriteLock(true);
        this.encryptionPwd = encryptionPwd;
    }

    public String getEncryptionPwd() {
        return encryptionPwd;
    }

    public Path getTempFolder() {
        return repoFolder.resolve(strf(".temp.{}", dbName));
    }

    public Path getRepoFolder() {
        return repoFolder;
    }
    public Path getDbFolder() {
        return repoFolder.resolve(dbName);
    }
   public Path getResourcesFolder() {
        return getDbFolder().resolve(strf("{}.resources", dbName));
    }

    public String getDbName() {
        return dbName;
    }

    public Map<Class<?>, ClazzWrapper> getEClasses() {
        return eClasses;
    }

    public ReadWriteLock getRepoLock() {
        return repoLock;
    }

}
