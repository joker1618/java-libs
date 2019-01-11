package xxx.joker.libs.core.repositoryOLD;

import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.repositoryOLD.entity.JkEntity;
import xxx.joker.libs.core.repositoryOLD.property.JkModelProperty;

import java.util.List;

@ToAnalyze
@Deprecated
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
