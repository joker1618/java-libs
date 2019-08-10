package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

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
        return strf("property[{}]", getKey().toLowerCase());
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
	public Long getLong() {
        return Long.parseLong(value);
    }
	public void setValue(String value) {
        this.value = value;
    }
}
