package xxx.joker.libs.datalayer;

import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.entities.RepoProperty;
import xxx.joker.libs.datalayer.entities.RepoResource;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public interface JkRepo {

    <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets();
    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);
    <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters);
    <K, T extends RepoEntity> Map<K,List<T>> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters);
    <K, T extends RepoEntity> Map<K,T> getMapSingle(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters);

    <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters);
    <T extends RepoEntity> T getById(long id);
    <T extends RepoEntity> T getByPk(T entity);
    <T extends RepoEntity> T getOrAddByPk(T entity);

    <T extends RepoEntity> boolean add(T toAdd);
    <T extends RepoEntity> boolean addAll(Collection<T> coll);
    <T extends RepoEntity> T removeId(long entityId);
    <T extends RepoEntity> T remove(T toRemove);
    <T extends RepoEntity> boolean removeAll(Collection<T> coll);

    void clearAll();

    void initRepoContent(List<RepoEntity> repoData);
    void rollback();
    void commit();
    void commit(LocalDateTime commitTime);

    Set<RepoProperty> getProperties();
    String getProperty(String key);
    String setProperty(String key, String value);
    String delProperty(String key);

    RepoResource getResource(String resName, String... tags);
    List<RepoResource> findResources(String... tags);
    RepoResource addResource(Path sourcePath, String resName, String... tags);
    boolean removeResource(RepoResource resource);
    boolean removeResource(String resName, String... tags);
    boolean removeResources(String... tags);
    void exportResources(Path outFolder, String... tags);

    // use this methods to get resource data: if repo is encrypted, read directly will get an error
    Path getResourcePath(RepoResource resource);

    RepoCtx getRepoCtx();

    String toStringRepo(boolean sortById);
    String toStringClass(boolean sortById, Class<?>... classes);
    String toStringEntities(Collection<? extends RepoEntity> entities);

}
