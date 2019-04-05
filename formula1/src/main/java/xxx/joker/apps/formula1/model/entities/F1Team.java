package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

public class F1Team extends RepoEntity {

    @RepoField
    private String teamName;

    public F1Team() {
    }

    public F1Team(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String getPrimaryKey() {
        return teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
