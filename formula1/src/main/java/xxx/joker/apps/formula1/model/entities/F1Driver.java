package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.time.LocalDate;

public class F1Driver extends RepoEntity {

    @RepoField
    private String fullName;
    @RepoField
    private String nation;
    @RepoField
    private String birthCity;
    @RepoField
    private LocalDate birthDate;

    public F1Driver() {
    }

    public F1Driver(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getPrimaryKey() {
        return fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getBirthCity() {
        return birthCity;
    }

    public void setBirthCity(String birthCity) {
        this.birthCity = birthCity;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
