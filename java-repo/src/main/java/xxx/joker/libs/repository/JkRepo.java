package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design.RepoEntity;

import java.util.Set;

public interface JkRepo {

    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);

    void rollback();
    void commit();

    String getProperty(String propKey);
    String getProperty(String propKey, String _default);
    String setProperty(String propKey, String propValue);
    String delProperty(String propKey);

}
