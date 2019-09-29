package xxx.joker.libs.datalayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.debug.JkDebug;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.datalayer.config.RepoConfig;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.dao.DaoHandler;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.entities.RepoProperty;
import xxx.joker.libs.datalayer.entities.RepoResource;
import xxx.joker.libs.datalayer.entities.RepoTags;
import xxx.joker.libs.datalayer.jpa.JpaHandler;
import xxx.joker.libs.datalayer.resourcer.ResourceHandler;
import xxx.joker.libs.datalayer.util.RepoUtil;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    protected final RepoCtx ctx;

    private DaoHandler daoHandler;
    private JpaHandler jpaHandler;
    private ResourceHandler resourceHandler;


    protected JkRepoFile(Path repoFolder, String dbName, String... packages) {
        this(null, repoFolder, dbName, packages);
    }
    protected JkRepoFile(String encrPwd, Path repoFolder, String dbName, String... packages) {
        Set<Class<?>> eclasses = new HashSet<>();
        eclasses.addAll(RepoUtil.scanPackages(getClass(), packages));
        eclasses.addAll(RepoUtil.scanPackages(RepoConfig.class, RepoConfig.PACKAGE_COMMON_ENTITIES));

        this.ctx = new RepoCtx(repoFolder, dbName, eclasses, encrPwd);

        LOG.info("Init repo [folder={}, dbName={}, encr={}]", ctx.getRepoFolder(), ctx.getDbName(), ctx.getEncrPwd() != null);
        eclasses.forEach(ec -> LOG.info("Repo entity class: {}", ec.getName()));

        JkDebug.startTimer("dao");
        this.daoHandler = new DaoHandler(ctx);
        List<RepoEntity> lines = daoHandler.loadDataFromFiles();
        JkDebug.stopAndStartTimer("dao", "jpa");
        this.jpaHandler = new JpaHandler(ctx, lines);
        JkDebug.stopTimer("jpa");
        this.resourceHandler = new ResourceHandler(ctx, jpaHandler);

        if(ctx.getEncrPwd() != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                JkFiles.delete(ctx.getDecryptFolder());
                LOG.info("Deleted decrypt folder: {}", ctx.getDecryptFolder());
            }));
        }
    }


    @Override
    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return jpaHandler.getDataSets();
    }

    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return jpaHandler.getDataSet(entityClazz);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters) {
        return JkStreams.filter(getDataSet(entityClazz), filters);
    }

    @Override
    @SafeVarargs
    public final <K, T extends RepoEntity> Map<K, List<T>> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMap(getDataSet(entityClazz), keyMapper, v -> v, filters);
    }

    @Override
    @SafeVarargs
    public final <K, T extends RepoEntity> Map<K, T> getMapSingle(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMapSingle(getDataSet(entityClazz), keyMapper, v -> v, filters);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters) {
        return jpaHandler.get(entityClazz, filters);
    }

    @Override
    public <T extends RepoEntity> T getByPk(T entity) {
        return (T) get(entity.getClass(), entity::equals);
    }

    @Override
    public <T extends RepoEntity> T getOrAddByPk(T entity) {
        T found = (T) get(entity.getClass(), entity::equals);
        if(found == null) {
            add(entity);
            found = entity;
        }
        return found;
    }

    @Override
    public <T extends RepoEntity> T getById(long id) {
        return (T) jpaHandler.getDataById().get(id);
    }

    @Override
    public <T extends RepoEntity> boolean add(T toAdd) {
        Set<T> ds = (Set<T>) getDataSet(toAdd.getClass());
        return ds.add(toAdd);
    }

    @Override
    public <T extends RepoEntity> boolean addAll(Collection<T> coll) {
        boolean res = false;
        if(!coll.isEmpty()) {
            T elem = JkConvert.toList(coll).get(0);
            Set<T> dataSet = (Set<T>)getDataSet(elem.getClass());
            res = dataSet.addAll(coll);
        }
        return res;
    }

    @Override
    public <T extends RepoEntity> T removeId(long entityId) {
        T e = getById(entityId);
        return e != null ? remove(e) : null;
    }

    @Override
    public <T extends RepoEntity> T remove(T toRemove) {
        boolean res = getDataSet(toRemove.getClass()).remove(toRemove);
        return res ? toRemove : null;
    }

    @Override
    public <T extends RepoEntity> boolean removeAll(Collection<T> coll) {
        try {
            ctx.getWriteLock().lock();
            boolean res = false;
            if(!coll.isEmpty()) {
                T elem = JkConvert.toList(coll).get(0);
                res = getDataSet(elem.getClass()).removeAll(coll);
            }
            return res;
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public void clearAll() {
        jpaHandler.initDataSets(Collections.emptyList());
    }

    @Override
    public void initRepoContent(List<RepoEntity> repoData) {
        jpaHandler.initDataSets(repoData);
    }

    @Override
    public void rollback() {
        try {
            ctx.getWriteLock().lock();
            List<RepoEntity> fromFiles = daoHandler.loadDataFromFiles();
            jpaHandler.initDataSets(fromFiles);
            resourceHandler.performResourcesCheck();
            LOG.info("Rollback done");
        } finally {
            ctx.getWriteLock().unlock();
        }

    }

    @Override
    public void commit() {
        commit(null);
    }

    @Override
    public void commit(LocalDateTime commitTime) {
        try {
            ctx.getWriteLock().lock();
            JkDateTime tm = commitTime == null ? JkDateTime.now() : JkDateTime.of(commitTime);
            setProperty(RepoConfig.PROP_LAST_COMMIT, tm.format());
            JkTimer timer = new JkTimer();
            Collection<RepoEntity> values = jpaHandler.getDataById().values();
            daoHandler.persistData(values);
            resourceHandler.commitChanges();
            LOG.info("Committed repo in {}", timer.toStringElapsed());
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public Set<RepoProperty> getProperties() {
        return getDataSet(RepoProperty.class);
    }

    @Override
    public String setProperty(String key, String value) {
        RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
        String oldValue;
        if(prop == null) {
            prop = new RepoProperty(key, value);
            add(prop);
            oldValue = null;
        } else {
            oldValue = prop.getValue();
            prop.setValue(value);
        }
        return oldValue;
    }

    @Override
    public String delProperty(String key) {
        RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
        if(prop == null) {
            return null;
        }
        remove(prop);
        return prop.getValue();
    }

    @Override
    public String getProperty(String key) {
        RepoProperty prop = get(RepoProperty.class, p -> p.getKey().equals(key));
        return prop == null ? null : prop.getValue();
    }

    @Override
    public RepoResource getResource(String resName, String... tags) {
        return resourceHandler.getResource(resName, RepoTags.of(tags));
    }

    @Override
    public List<RepoResource> findResources(String... tags) {
        return resourceHandler.findResources(RepoTags.of(tags));
    }

    @Override
    public RepoResource addResource(Path sourcePath, String resName, String... tags) {
        return resourceHandler.addResource(sourcePath, resName, RepoTags.of(tags));
    }

    @Override
    public boolean removeResource(RepoResource resource) {
        return resourceHandler.removeResource(resource);
    }

    @Override
    public boolean removeResource(String resName, String... tags) {
        return resourceHandler.removeResource(resName, RepoTags.of(tags));
    }

    @Override
    public boolean removeResources(String... tags) {
        return resourceHandler.removeResources(RepoTags.of(tags));
    }

    @Override
    public void exportResources(Path outFolder) {
        resourceHandler.exportResources(outFolder);
    }

    @Override
    public Path getResourcePath(RepoResource resource) {
        if(ctx.getEncrPwd() == null) {
            return resource.getPath();
        }
        Path outPath = ctx.getDecryptFolder().resolve(resource.getPath().getFileName());
        JkEncryption.decryptFile(resource.getPath(), outPath, ctx.getEncrPwd());
        return outPath;
    }

    @Override
    public RepoCtx getRepoCtx() {
        return ctx;
    }

    @Override
    public String toStringRepo(boolean sortById) {
        List<Class<?>> keys = JkStreams.mapSort(getDataSets().entrySet(), Map.Entry::getKey, Comparator.comparing(Class::getName));
        return toStringRepoClass(sortById, keys);
    }

    @Override
    public String toStringClass(boolean sortById, Class<?>... classes) {
        return toStringRepoClass(sortById, JkConvert.toList(classes));
    }

    @Override
    public String toStringEntities(Collection<? extends RepoEntity> entities) {
        return RepoUtil.toStringEntities(entities);
    }

    private String toStringRepoClass(boolean sortById, Collection<Class<?>> classes) {
        List<String> tables = new ArrayList<>();
        for (Class<?> clazz : classes) {
            Set<RepoEntity> coll = getDataSet((Class<RepoEntity>) clazz);
            List<RepoEntity> sorted;
            if(sortById) {
                sorted = JkStreams.sorted(coll, Comparator.comparingLong(RepoEntity::getEntityId));
            } else {
                sorted = JkStreams.sorted(coll);
            }
            tables.add(RepoUtil.toStringEntities(sorted));
        }
        return JkStreams.join(tables, "\n\n");
    }
}
