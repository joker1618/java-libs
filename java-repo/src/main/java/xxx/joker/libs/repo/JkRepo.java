package xxx.joker.libs.repo;

import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.repo.config.RepoConfig;
import xxx.joker.libs.repo.config.RepoCtx;
import xxx.joker.libs.repo.design.RepoEntity;
import xxx.joker.libs.repo.design.entities.RepoProperty;
import xxx.joker.libs.repo.design.entities.RepoResource;
import xxx.joker.libs.repo.design.entities.RepoTags;
import xxx.joker.libs.repo.resources.AddType;
import xxx.joker.libs.repo.util.RepoUtil;
import xxx.joker.libs.repo.wrapper.RepoWClazz;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static xxx.joker.libs.core.util.JkConvert.toList;

/**
 * When an entity is added to the repo, also its dependencies are added
 *
 * When an entity is deleted, is remove from the related data set,
 * but not from other entities where it is used as a dependency.
 * Pay attention to call the method 'updateDependencies' or 'removeFromDependencies if it's used in other entities
 * This choice is made for performance reasons
 */
public interface JkRepo {

    static Builder builder() {
        return new Builder();
    }


    void initDataSets(Collection<RepoEntity> repoData);

    Map<Class<? extends RepoEntity>, Set<RepoEntity>> getDataSets();
    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);
    <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters);
    <K, T extends RepoEntity> Map<K,List<T>> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters);
    <K, T extends RepoEntity> Map<K,T> getMapSingle(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters);

    <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters);
    <T extends RepoEntity> T getById(long id);
    <T extends RepoEntity> T getByPk(T entity);
    <T extends RepoEntity> T getOrAdd(T entity);

    <T extends RepoEntity> boolean add(T... toAdd);
    <T extends RepoEntity> boolean addAll(Collection<T> coll);

    <T extends RepoEntity> T removeId(long entityId);
    <T extends RepoEntity> boolean remove(T toRemove);
    boolean removeAll(Collection<? extends RepoEntity> toRemove);

    void updateDependencies(RepoEntity... entities);
    void updateDependencies(Collection<? extends RepoEntity> entities);
    void removeFromDependencies(RepoEntity toRemove, RepoEntity... entities);
    void removeFromDependencies(RepoEntity toRemove, Collection<? extends RepoEntity> entities);

    void clearAll();

    void rollback();
    void commit();

    Set<RepoProperty> getProperties();
    String getProperty(String key);
    String setProperty(String key, String value);
    String delProperty(String key);

    Set<RepoResource> getResources();
    RepoResource getResource(String resName, String... tags);
    RepoResource getResource(String resName, RepoTags repoTags);
    List<RepoResource> findResources(String... tags);
    List<RepoResource> findResources(RepoTags repoTags);
    RepoResource addResource(Path sourcePath, String resName, RepoTags repoTags);
    RepoResource addResource(Path sourcePath, String resName, RepoTags repoTags, AddType addType);
    void exportResources(Path outFolder);
    void exportResources(Path outFolder, RepoTags repoTags);
    void exportResources(Path outFolder, Collection<RepoResource> resources);
    List<Path> cleanResources();

    RepoCtx getRepoCtx();

    String toStringRepo();
    String toStringRepo(boolean sortById);

    String toStringClass(Class<?>... classes);

    String toStringClass(boolean sortById, Class<?>... classes);
    String toStringEntities(Collection<? extends RepoEntity> entities);


    class Builder {
        private Path repoFolder;
        private String dbName;
        private Set<Class<?>> classes = new LinkedHashSet<>();
        private List<String> packages = new ArrayList<>();

        public RepoCtx buildCtx() {
            Set<Class> eClasses = new HashSet<>(classes);
            eClasses.addAll(RepoUtil.scanPackages(getClass(), packages));
            eClasses.addAll(RepoConfig.PACKAGE_COMMON_ENTITIES);
            eClasses.removeIf(ec -> !RepoConfig.isValidRepoClass(ec));
            return new RepoCtx(repoFolder, dbName, JkStreams.map(eClasses, RepoWClazz::get));
        }

        public JkRepo buildRepo() {
            return new JkRepoFile(repoFolder, dbName, classes, packages);
        }
        public JkRepo buildRepo(RepoCtx ctx) {
            return new JkRepoFile(ctx);
        }

        public Builder setRepoFolder(Path repoFolder) {
            this.repoFolder = repoFolder;
            return this;
        }

        public Builder setDbName(String dbName) {
            this.dbName = dbName;
            return this;
        }

        public Builder addClasses(Class<?>... clazzes) {
            classes.addAll(toList(clazzes));
            return this;
        }

        public Builder addPackage(String... pkgs) {
            packages.addAll(toList(pkgs));
            return this;
        }
    }
}

