package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Entrant extends RepoEntity {

    @RepoField
    private Integer year;
    @RepoField
    private F1Team team;
    @RepoField
    private Integer carNo;
    @RepoField
    private F1Driver driver;

    @Override
    public String getPrimaryKey() {
        return strf("entrant-{}-{}-{}-{}", year, team.getTeamName(), driver.getFullName(), carNo);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public F1Team getTeam() {
        return team;
    }

    public void setTeam(F1Team team) {
        this.team = team;
    }

    public Integer getCarNo() {
        return carNo;
    }

    public void setCarNo(Integer carNo) {
        this.carNo = carNo;
    }

    public F1Driver getDriver() {
        return driver;
    }

    public void setDriver(F1Driver driver) {
        this.driver = driver;
    }
}
