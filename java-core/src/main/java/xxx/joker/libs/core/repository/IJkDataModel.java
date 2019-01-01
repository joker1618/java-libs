package xxx.joker.libs.core.repository;

import xxx.joker.libs.core.repository.entity.JkEntity;
import xxx.joker.libs.core.repository.property.JkModelProperty;

import java.util.*;

public interface IJkDataModel {

    void commit();

    void cascadeDependencies();
    void cascadeDependencies(Class<?> clazz);
    void cascadeDependencies(JkEntity entity);

    List<JkModelProperty> getAllProperties();
    JkModelProperty getProperty(String propertyKey);
    void setProperty(String key, String value);
    void setProperty(JkModelProperty property);

}
