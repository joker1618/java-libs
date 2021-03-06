package xxx.joker.libs.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.repo.config.RepoChecker;
import xxx.joker.libs.repo.config.RepoConfig;
import xxx.joker.libs.repo.config.RepoCtx;
import xxx.joker.libs.repo.design.RepoEntity;
import xxx.joker.libs.repo.design.entities.RepoProperty;
import xxx.joker.libs.repo.design.entities.RepoResource;
import xxx.joker.libs.repo.design.entities.RepoTags;
import xxx.joker.libs.repo.exceptions.RepoError;
import xxx.joker.libs.repo.jpa.JpaHandler;
import xxx.joker.libs.repo.resources.AddType;
import xxx.joker.libs.repo.resources.ResourceHandler;
import xxx.joker.libs.repo.util.RepoUtil;
import xxx.joker.libs.repo.wrapper.RepoWClazz;
import xxx.joker.libs.repo.wrapper.RepoWField;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static xxx.joker.libs.core.lambda.JkStreams.*;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkStrings.strf;
import static xxx.joker.libs.repo.exceptions.ErrorType.DESIGN_CIRCULAR_DEPENDENCIES;

public class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    protected final RepoCtx ctx;

    private JpaHandler jpaHandler;
    private ResourceHandler resourceHandler;

    protected JkRepoFile(RepoCtx ctx) {
        this.ctx = ctx;
        initialize(ctx);
    }
    protected JkRepoFile(Path repoFolder, String dbName, String... packages) {
        this(repoFolder, dbName, Collections.emptyList(), packages);
    }
    protected JkRepoFile(Path repoFolder, String dbName, Collection<Class<?>> classes, String... packages) {
        this(repoFolder, dbName, classes, toList(packages));
    }
    protected JkRepoFile(Path repoFolder, String dbName, Collection<Class<?>> classes, Collection<String> packages) {
        this.ctx = checkRepoAndCreateCtx(repoFolder, dbName, classes, packages);
        initialize(ctx);
    }
    private void initialize(RepoCtx ctx) {
        LOG.info("Init repo [folder={}, dbName={}]", ctx.getRepoFolder(), ctx.getDbName());
        ctx.getWClazzMap().keySet().forEach(ec -> LOG.debug("Repo entity class: {}", ec.getName()));
        this.jpaHandler = JpaHandler.createHandler(ctx);
        this.resourceHandler = ResourceHandler.createHandler(ctx, getDataSet(RepoResource.class));
    }
    private RepoCtx checkRepoAndCreateCtx(Path repoFolder, String dbName, Collection<Class<?>> classes, Collection<String> packages) {
        // Find entity classes
        Set<Class<?>> eClasses = new HashSet<>(classes);
        eClasses.addAll(RepoUtil.scanPackages(getClass(), packages));
        eClasses.addAll(RepoConfig.PACKAGE_COMMON_ENTITIES);
        eClasses.removeIf(ec -> !RepoConfig.isValidRepoClass(ec));

        List<RepoWClazz> wcList = map(eClasses, ec -> RepoWClazz.get((Class<RepoEntity>)ec));
        wcList.forEach(RepoChecker::checkEntityClass);

        RepoCtx ctx = new RepoCtx(repoFolder, dbName, wcList);
        checkCircularDependencies(ctx);

        return ctx;
    }
    private void checkCircularDependencies(RepoCtx ctx) {
        Map<Class<?>, Set<Class<?>>> refMap = new HashMap<>();

        for (RepoWClazz cw : ctx.getWClazzMap().values()) {
            refMap.put(cw.getEClazz(), new HashSet<>());
            for (RepoWField fw : cw.getFields()) {
                List<Class<?>> classes = fw.retrieveEntityParametrizedTypes();
                refMap.get(cw.getEClazz()).addAll(classes);
            }
        }

        List<Class<?>> recursives = JkStreams.filter(refMap.keySet(), c -> isRecursive(refMap, c, c));
        if(!recursives.isEmpty()) {
            throw new RepoError(DESIGN_CIRCULAR_DEPENDENCIES, "Circular dependency found for classes: {}", recursives);
        }
    }
    private boolean isRecursive(Map<Class<?>, Set<Class<?>>> refMap, Class<?> sourceClazz, Class<?> toCheck) {
        Set<Class<?>> cset = refMap.get(toCheck);
        if(cset.contains(sourceClazz))
            return true;

        boolean res = false;
        for (Class<?> c : cset)
            res |= isRecursive(refMap, sourceClazz, c);

        return res;
    }

    @Override
    public Map<Class<? extends RepoEntity>, Set<RepoEntity>> getDataSets() {
        return jpaHandler.getDataSets();
    }

    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return jpaHandler.getDataSet(entityClazz);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters) {
        return filter(getDataSet(entityClazz), filters);
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
    public <T extends RepoEntity> T getOrAdd(T entity) {
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
    @SafeVarargs
    public final <T extends RepoEntity> boolean add(T... toAdd) {
        return addAll(toList(toAdd));
    }

    @Override
    public <T extends RepoEntity> boolean addAll(Collection<T> coll) {
        boolean res = false;
        if(!coll.isEmpty()) {
            T elem = toList(coll).get(0);
            Set<T> dataSet = (Set<T>)getDataSet(elem.getClass());
            res = dataSet.addAll(coll);
        }
        return res;
    }

    @Override
    public <T extends RepoEntity> T removeId(long entityId) {
        T e = getById(entityId);
        if(e == null)   return null;
        return remove(e) ? e : null;
    }
    @Override
    public <T extends RepoEntity> boolean remove(T toRemove) {
        return getDataSet(toRemove.getClass()).remove(toRemove);
    }
    @Override
    public boolean removeAll(Collection<? extends RepoEntity> toRemove) {
        boolean res = false;
        for (RepoEntity e : toRemove) {
            res |= remove(e);
        }
        return res;
    }

    @Override
    public void updateDependencies(RepoEntity... entities) {
        updateDependencies(toList(entities));
    }
    @Override
    public void updateDependencies(Collection<? extends RepoEntity> entities) {
        jpaHandler.updateDependencies(entities);
    }
    @Override
    public void removeFromDependencies(RepoEntity toRemove, RepoEntity... entities) {
        removeFromDependencies(toRemove, toList(entities));
    }
    @Override
    public void removeFromDependencies(RepoEntity toRemove, Collection<? extends RepoEntity> entities) {
        jpaHandler.removeFromDependencies(toRemove, entities);
    }

    @Override
    public void clearAll() {
        jpaHandler.clearAll(false);
    }

    @Override
    public void erase() {
        jpaHandler.clearAll(true);
    }

    @Override
    public void initDataSets(Collection<RepoEntity> repoData) {
        jpaHandler.initRepoContent(repoData);
    }

    @Override
    public void rollback() {
        try {
            ctx.getWriteLock().lock();
            jpaHandler.rollback();
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
            jpaHandler.commit();
            LOG.info("Committed repo in {}", timer.strElapsed());
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
        return jpaHandler.setProperty(key, value);
    }

    @Override
    public String delProperty(String key) {
        return jpaHandler.delProperty(key);
    }

    @Override
    public String getProperty(String key) {
        return jpaHandler.getProperty(key);
    }

    @Override
    public Set<RepoResource> getResources() {
        return jpaHandler.getDataSet(RepoResource.class);
    }

    @Override
    public RepoResource getResource(String resName, String... tags) {
        return getResource(resName, RepoTags.of(tags));
    }

    @Override
    public RepoResource getResource(String resName, RepoTags repoTags) {
        try {
            ctx.getReadLock().lock();
            return resourceHandler.getResource(resName, repoTags);
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    @Override
    public List<RepoResource> findResources(String... tags) {
        return findResources(RepoTags.of(tags));
    }

    @Override
    public List<RepoResource> findResources(RepoTags repoTags) {
        try {
            ctx.getReadLock().lock();
            return resourceHandler.findResources(repoTags);
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    @Override
    public RepoResource addResource(Path sourcePath, String resName, RepoTags repoTags) {
        return addResource(sourcePath, resName, repoTags, AddType.COPY);
    }
    @Override
    public RepoResource addResource(Path sourcePath, String resName, RepoTags repoTags, AddType addType) {
        try {
            ctx.getWriteLock().lock();
            return resourceHandler.getOrAddResource(sourcePath, resName, repoTags, addType);
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    @Override
    public void exportResources(Path outFolder) {
        exportResources(outFolder, RepoTags.of(""));
    }
    @Override
    public void exportResources(Path outFolder, RepoTags repoTags) {
        try {
            ctx.getReadLock().lock();
            resourceHandler.exportResources(outFolder, repoTags);
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    @Override
    public void exportResources(Path outFolder, Collection<RepoResource> resources) {
        try {
            ctx.getReadLock().lock();
            resourceHandler.exportResources(outFolder, resources);
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    @Override
    public RepoCtx getRepoCtx() {
        return ctx;
    }

    @Override
    public String toStringRepo() {
        return toStringRepo(false);
    }
    @Override
    public String toStringRepo(boolean sortById) {
        List<Class<?>> keys = JkStreams.mapSort(getDataSets().entrySet(), Map.Entry::getKey, Comparator.comparing(Class::getName));
        return toStringRepoClass(sortById, keys);
    }

    @Override
    public String toStringClass(Class<?>... classes) {
        return toStringClass(false, classes);
    }

    @Override
    public String toStringClass(boolean sortById, Class<?>... classes) {
        return toStringRepoClass(sortById, toList(classes));
    }

    @Override
    public String toStringEntities(Collection<? extends RepoEntity> entities) {
        try {
            ctx.getReadLock().lock();
            return RepoUtil.toStringEntities(entities);
        } finally {
            ctx.getReadLock().unlock();
        }
    }

    /**
     * Remove resource files not used (when a resource is deleted, the file is not removed)
     */
    @Override
    public List<Path> cleanResources() {
        try {
            ctx.getWriteLock().lock();
            return resourceHandler.cleanResources();
        } finally {
            ctx.getWriteLock().unlock();
        }
    }

    private String toStringRepoClass(boolean sortById, Collection<Class<?>> classes) {
        try {
            ctx.getReadLock().lock();
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
        } finally {
            ctx.getReadLock().unlock();
        }
    }
}
