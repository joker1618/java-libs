package xxx.joker.libs.repository.entities2;

import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.design2.RepoField;

public class RepoProperty extends RepoEntity {

    @RepoField
    private String key;
    @RepoField
    private String value;

    public RepoProperty() {
    }
    public RepoProperty(String key, String value) {
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
