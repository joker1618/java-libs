package xxx.joker.libs.datalayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.debug.JkDebug;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.datalayer.config.RepoConfig;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.dao.DaoHandler;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.entities.RepoResource;
import xxx.joker.libs.datalayer.entities.RepoTags;
import xxx.joker.libs.datalayer.jpa.JpaHandler;
import xxx.joker.libs.datalayer.resourcer.ResourceHandler;
import xxx.joker.libs.datalayer.util.RepoUtil;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class JkRepoFile implements JkRepo {

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

        LOG.info("Init repo [folder={}, dbName={}, encr={}", ctx.getRepoFolder(), ctx.getDbName(), ctx.getEncrPwd());
        eclasses.forEach(ec -> LOG.info("Repo entity class: {}", ec));

        JkDebug.startTimer("dao");
        this.daoHandler = new DaoHandler(ctx);
        List<RepoEntity> lines = daoHandler.loadDataFromFiles();
        JkDebug.stopAndStartTimer("dao", "jpa");
        this.jpaHandler = new JpaHandler(ctx, lines);
        JkDebug.stopTimer("jpa");
        this.resourceHandler = new ResourceHandler(ctx, jpaHandler);
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
    public <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters) {
        return JkStreams.filter(getDataSet(entityClazz), filters);
    }

    @Override
    public <K, T extends RepoEntity> Map<K, List<T>> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMap(getDataSet(entityClazz), keyMapper, v -> v, filters);
    }

    @Override
    public <K, T extends RepoEntity> Map<K, T> getMapSingle(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMapSingle(getDataSet(entityClazz), keyMapper, v -> v, filters);
    }

    @Override
    public <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters) {
        return jpaHandler.get(entityClazz, filters);
    }

    @Override
    public <T extends RepoEntity> T getByPk(T entity) {
        return (T) get(entity.getClass(), entity::equals);
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
    public <T extends RepoEntity> T removeID(long entityID) {
        T e = getById(entityID);
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
            LOG.info("Rollback done");
        } finally {
            ctx.getWriteLock().unlock();
        }

    }

    @Override
    public void commit() {
        try {
            ctx.getWriteLock().lock();
            JkTimer timer = new JkTimer();
            Collection<RepoEntity> values = jpaHandler.getDataById().values();
            daoHandler.persistData(values);
            LOG.info("Committed repo in {}", timer.toStringElapsed());
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public RepoResource getResource(String resName, String... tags) {
        return resourceHandler.getResource(resName, RepoTags.of(tags));
    }

    @Override
    public Path getUriPath(RepoResource resource) {
        Path up = resource.getUri().getPath();
        if(!up.isAbsolute()) {
            up = ctx.getResourcesFolder().resolve(up);
        }
        return up;
    }

    @Override
    public RepoResource addResource(Path sourcePath, String resName, String... tags) {
        return resourceHandler.addResource(sourcePath, resName, RepoTags.of(tags));
    }

    @Override
    public RepoCtx getRepoCtx() {
        return ctx;
    }

    @Override
    public String toStringRepo() {
        List<Class<?>> keys = JkStreams.mapSort(getDataSets().entrySet(), Map.Entry::getKey, Comparator.comparing(Class::getName));
        return toStringRepoClass(keys);
    }

    @Override
    public String toStringClass(Class<?>... classes) {
        return toStringRepoClass(JkConvert.toList(classes));
    }

    @Override
    public String toStringEntities(Collection<? extends RepoEntity> entities) {
        return RepoUtil.toStringEntities(entities);
    }

    private String toStringRepoClass(Collection<Class<?>> classes) {
        List<String> tables = new ArrayList<>();
        for (Class<?> clazz : classes) {
            Set<RepoEntity> coll = getDataSet((Class<RepoEntity>) clazz);
            tables.add(RepoUtil.toStringEntities(coll));
        }
        return JkStreams.join(tables, "\n\n");
    }
}
