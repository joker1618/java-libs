package xxx.joker.libs.repository;

import xxx.joker.libs.repository.design.JkEntity;

import java.util.Set;

public interface JkDataRepo {

    <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz);

    void commit();

}
