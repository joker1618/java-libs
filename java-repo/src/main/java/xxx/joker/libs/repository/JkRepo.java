package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.entities.JkRepoProperty;

import java.util.Set;

public interface JkRepo {

    <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz);

    void commit();

    Set<JkRepoProperty> getProperties();
    String getProperty(String propKey);
    String getProperty(String propKey, String _default);
    void setProperty(String propKey, String propValue);
    String delProperty(String propKey);

}
