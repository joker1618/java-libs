package xxx.joker.apps.formula1.dataCreator.model.beans;

import xxx.joker.apps.formula1.dataCreator.model.entities.F1Driver;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Race;

import java.util.HashMap;
import java.util.Map;

public class F1SeasonResult implements Comparable<F1SeasonResult>{

    private F1Driver driver;
    private Map<F1GranPrix, F1Race> points;
    private int totPoints;

    public F1SeasonResult() {
        this.points = new HashMap<>();
    }

    public String getFinalPos(F1GranPrix gp) {
        F1Race r = points.get(gp);
        return r.isRetired() ? "RET" : ""+r.getPos();
    }

    public int getTotPoints() {
        return totPoints;
    }

    public void setTotPoints(int totPoints) {
        this.totPoints = totPoints;
    }

    public F1Driver getDriver() {
        return driver;
    }

    public void setDriver(F1Driver driver) {
        this.driver = driver;
    }

    public Map<F1GranPrix, F1Race> getPoints() {
        return points;
    }

    public void setPoints(Map<F1GranPrix, F1Race> points) {
        this.points = points;
    }

    @Override
    public int compareTo(F1SeasonResult o) {
        return -1 * (getTotPoints() - o.getTotPoints());
    }
}
