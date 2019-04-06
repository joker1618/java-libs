package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Link extends RepoEntity {

    @RepoField
    private String key;
    @RepoField
    private String url;

    public F1Link() {
    }

    public F1Link(String key, String url) {
        this.key = key;
        this.url = url;
    }

    @Override
    public String getPrimaryKey() {
        return strf("link-{}:{}", key, url).toLowerCase();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
