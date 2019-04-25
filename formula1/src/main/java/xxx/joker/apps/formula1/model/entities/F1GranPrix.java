package xxx.joker.apps.formula1.model.entities;

import xxx.joker.apps.formula1.model.fields.F1FastLap;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.time.LocalDate;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1GranPrix extends RepoEntity {

    @RepoField
    private Integer year;
    @RepoField
    private Integer num;
    @RepoField
    private LocalDate date;
    @RepoField
    private F1Circuit circuit;
    @RepoField
    private Double lapLength;
    @RepoField
    private Integer numLapsRace;
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
    public String toString() {
        return strShort();
    }

    @Override
    public String getPrimaryKey() {
        return strf("gp-%04d-%02d", year, num);
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public F1Circuit getCircuit() {
        return circuit;
    }

    public void setCircuit(F1Circuit circuit) {
        this.circuit = circuit;
    }

    public Double getLapLength() {
        return lapLength;
    }

    public void setLapLength(Double lapLength) {
        this.lapLength = lapLength;
    }

    public Integer getNumLapsRace() {
        return numLapsRace;
    }

    public void setNumLapsRace(Integer numLapsRace) {
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
