package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.time.LocalDate;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1GranPrix extends RepoEntity {

    @RepoField
    private int year;
    @RepoField
    private int num;
    @RepoField
    private LocalDate date;
    @RepoField
    private String city;
    @RepoField
    private String nation;
    @RepoField
    private double lapLength;
    @RepoField
    private int numLapsRace;
    @RepoField
    private F1FastLap fastLap;
    @RepoField
    private List<F1Qualify> qualifies;
    @RepoField
    private List<F1Race> races;

    public F1GranPrix() {
    }

    public F1GranPrix(int year, int num) {
        this.year = year;
        this.num = num;
    }

    @Override
    public String getPrimaryKey() {
        return strf("gp-%d-%02d", year, num);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public double getLapLength() {
        return lapLength;
    }

    public void setLapLength(double lapLength) {
        this.lapLength = lapLength;
    }

    public int getNumLapsRace() {
        return numLapsRace;
    }

    public void setNumLapsRace(int numLapsRace) {
        this.numLapsRace = numLapsRace;
    }

    public List<F1Qualify> getQualifies() {
        return qualifies;
    }

    public void setQualifies(List<F1Qualify> qualifies) {
        this.qualifies = qualifies;
    }

    public List<F1Race> getRaces() {
        return races;
    }

    public void setRaces(List<F1Race> races) {
        this.races = races;
    }

    public F1FastLap getFastLap() {
        return fastLap;
    }

    public void setFastLap(F1FastLap fastLap) {
        this.fastLap = fastLap;
    }
}
