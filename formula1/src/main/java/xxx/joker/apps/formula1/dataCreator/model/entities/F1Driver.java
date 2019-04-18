package xxx.joker.apps.formula1.dataCreator.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.time.LocalDate;

public class F1Driver extends RepoEntity {

    @RepoField
    private String fullName;
    @RepoField
    private String nation;
    @RepoField
    private String city;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
