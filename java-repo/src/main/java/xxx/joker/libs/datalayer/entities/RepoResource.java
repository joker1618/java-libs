package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoResource extends RepoEntity {

    @RepoField
    private String name;
    @RepoField
    private RepoUri uri;
    @RepoField
    private RepoTags tags;

    public RepoResource() {
    }
    public RepoResource(String name) {
        this.name = name;
    }

    public RepoResource(String name, RepoTags tags) {
        this.name = name;
        this.tags = tags;
    }

    @Override
    public String getPrimaryKey() {
        return strf("{}-{}", getName(), getTags().format());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepoUri getUri() {
        return uri;
    }


    public void setUri(RepoUri uri) {
        this.uri = uri;
    }

    public RepoTags getTags() {
        return tags;
    }

    public void setTags(RepoTags tags) {
        this.tags = tags;
    }
}
