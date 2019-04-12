package xxx.joker.apps.formula1.model.beans;

import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;

import java.util.List;

public class F1Season {

    private final int year;
    private List<F1Entrant> entrants;
    private List<F1GranPrix> gpList;
    private List<F1SeasonResult> results;
    private int numberOfQualifyRounds = -1;

    public F1Season(int year) {
        this.year = year;
    }

    public int numberOfQualifyRounds() {
        if(numberOfQualifyRounds == -1) {
            numberOfQualifyRounds = gpList.get(0).getQualifies().get(0).getTimes().size();
        }
        return numberOfQualifyRounds;
    }

    public int getYear() {
        return year;
    }

    public List<F1Entrant> getEntrants() {
        return entrants;
    }

    public void setEntrants(List<F1Entrant> entrants) {
        this.entrants = entrants;
    }

    public List<F1GranPrix> getGpList() {
        return gpList;
    }

    public void setGpList(List<F1GranPrix> gpList) {
        this.gpList = gpList;
    }

    public List<F1SeasonResult> getResults() {
        return results;
    }

    public void setResults(List<F1SeasonResult> results) {
        this.results = results;
    }
}
