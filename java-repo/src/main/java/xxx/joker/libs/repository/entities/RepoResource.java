package xxx.joker.libs.repository.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoResource extends RepoEntity {

    @RepoField
    private String name;
    @RepoField
    private RepoUri repoURI;
    @RepoField
    private RepoTags tags;

    public RepoResource() {
    }
    public RepoResource(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryKey() {
        return strf("resource:{}-{}", getName(), getTags().format());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepoUri getRepoURI() {
        return repoURI;
    }

    public void setRepoURI(RepoUri repoURI) {
        this.repoURI = repoURI;
    }

    public RepoTags getTags() {
        return tags;
    }

    public void setTags(RepoTags tags) {
        this.tags = tags;
    }
}
