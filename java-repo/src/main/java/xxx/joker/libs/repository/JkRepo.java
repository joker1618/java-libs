package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.entities.RepoProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public interface JkRepo {

    <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets();
    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);
    <T extends RepoEntity> List<T> getDataList(Class<T> entityClazz, Predicate<T>... filters);
    <K,T extends RepoEntity> Map<K,T> getDataMap(Class<T> entityClazz, Function<T,K> keyMapper, Predicate<T>... filters);

    <T extends RepoEntity> T retrieveByPK(T entity);
    <T extends RepoEntity> boolean add(T toAdd);
    <T extends RepoEntity> boolean remove(T toRemove);

    void clearDataSets();

    void rollback();
    void commit();

    Set<RepoProperty> getProperties();
    String getProperty(String propKey);
    String getProperty(String propKey, String _default);
    String setProperty(String propKey, String propValue);
    String delProperty(String propKey);

}
