package xxx.joker.apps.formula1.old.dataCreator.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

public class F1Team extends RepoEntity {

    @RepoField
    private String teamName;
    @RepoField
    private String nation;

    public F1Team() {
    }

    public F1Team(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String getPrimaryKey() {
        return "team-" + teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }
}
