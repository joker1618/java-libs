package xxx.joker.libs.repository.config;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RepoCtx {

    private Path repoFolder;
    private String dbName;
    private Set<Class<?>> eClasses;

    private ReadWriteLock repoLock;

    public RepoCtx(Path repoFolder, String dbName, Set<Class<?>> eClasses) {
        this.repoFolder = repoFolder;
        this.dbName = dbName;
        this.eClasses = eClasses;
        this.repoLock = new ReentrantReadWriteLock(true);
    }

    public Path getRepoFolder() {
        return repoFolder;
    }

    public String getDbName() {
        return dbName;
    }

    public Set<Class<?>> getEClasses() {
        return eClasses;
    }

    public ReadWriteLock getRepoLock() {
        return repoLock;
    }

}
