package xxx.joker.libs.repository.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.util.Set;

public class RepoResource extends RepoEntity {

    @RepoField
    private String name;
    @RepoField
    private RepoUri repoURI;
    @RepoField
    private Set<String> tags;

    public RepoResource() {
    }
    public RepoResource(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryKey() {
        return name;
    }


}
