package xxx.joker.apps.formula1.dataCreator.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Entrant extends RepoEntity {

    @RepoField
    private int year;
    @RepoField
    private F1Team team;
    @RepoField
    private String engine;
    @RepoField
    private int carNo;
    @RepoField
    private F1Driver driver;

    @Override
    public String getPrimaryKey() {
        return strf("entrant-{}-{}-{}", year, team.getTeamName(), driver.getFullName());
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public F1Team getTeam() {
        return team;
    }

    public void setTeam(F1Team team) {
        this.team = team;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getCarNo() {
        return carNo;
    }

    public void setCarNo(int carNo) {
        this.carNo = carNo;
    }

    public F1Driver getDriver() {
        return driver;
    }

    public void setDriver(F1Driver driver) {
        this.driver = driver;
    }
}
