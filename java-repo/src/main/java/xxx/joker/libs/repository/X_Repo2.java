package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.entities2.RepoProperty;

import java.util.Set;

interface X_Repo2 {

    <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz);

    void rollback();
    void commit();

    String getProperty(String propKey);
    String getProperty(String propKey, String _default);
    void setProperty(String propKey, String propValue);
    String delProperty(String propKey);

}
