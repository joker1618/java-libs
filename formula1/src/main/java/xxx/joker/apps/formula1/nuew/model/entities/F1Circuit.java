package xxx.joker.apps.formula1.nuew.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Circuit extends RepoEntity {

    @RepoField
    private String city;
    @RepoField
    private String nation;

    public F1Circuit() {
    }

    public F1Circuit(String city, String nation) {
        this.city = city;
        this.nation = nation;
    }

    @Override
    public String getPrimaryKey() {
        return strf("circuit-{}-{}", nation, city);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

}
