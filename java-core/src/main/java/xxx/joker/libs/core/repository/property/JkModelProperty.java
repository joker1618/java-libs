package xxx.joker.libs.core.repository.property;

import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.repository.entity.JkDefaultEntity;
import xxx.joker.libs.core.repository.entity.JkEntityField;

@ToAnalyze
@Deprecated
public class JkModelProperty extends JkDefaultEntity {

    @JkEntityField(index = 0)
    private String key;
    @JkEntityField(index = 1)
    private String value;

    public JkModelProperty() {
    }
    public JkModelProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getPrimaryKey() {
        return getKey();
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
