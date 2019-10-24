package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.datalayer.design.EntityField;
import xxx.joker.libs.datalayer.design.EntityPK;
import xxx.joker.libs.datalayer.design.RepoEntity;

public class RepoProperty extends RepoEntity {

    @EntityPK
    @EntityField
    private String key;
    @EntityField
    private String value;

    public RepoProperty() {
    }
    public RepoProperty(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
//    @Override
//    public String getPrimaryKey() {
//        return strf("{}", getKey().toLowerCase());
//    }

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
