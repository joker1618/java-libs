package xxx.joker.libs.repository.entities;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

public class JkRepoProperty extends JkRepoEntity {

    @JkEntityField
    private String key;
    @JkEntityField
    private String value;

    public JkRepoProperty() {
    }
    public JkRepoProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public String getPrimaryKey() {
        return getKey().toLowerCase();
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
