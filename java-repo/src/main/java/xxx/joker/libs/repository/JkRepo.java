package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.RepoProperty;
import xxx.joker.libs.repository.entities.RepoResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public interface JkRepo {

    <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets();
    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);
    <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters);
    <K,T extends RepoEntity> Map<K,T> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters);

    <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters);
    <T extends RepoEntity> T getById(long id);
    <T extends RepoEntity> T getByPk(T entity);
    <T extends RepoEntity> T getByPkOrAdd(T entity);

    <T extends RepoEntity> boolean add(T toAdd);
    <T extends RepoEntity> boolean removeId(long eid);
    <T extends RepoEntity> boolean remove(T toRemove);

    void clearAll();

    void rollback();
    void commit();

    Set<RepoProperty> getProperties();
    String getProperty(String key);
    String getProperty(String key, String _default);
    String setProperty(String key, String value);
    String delProperty(String key);

    RepoResource getResource(String resName, String... tags);
    RepoResource addResource(Path sourcePath, String resName, String... tags);

}
