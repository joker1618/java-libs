package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.util.List;

public class F1Race extends RepoEntity {

    @RepoField
    private F1GranPrix gp;
    @RepoField
    private F1Entrant entrant;
    @RepoField
    private int pos;
    @RepoField
    private int laps;
    @RepoField
    private boolean retired;
    @RepoField
    private int points;
    @RepoField
    private JkDuration time;

    public F1Race() {
    }

    @Override
    public String getPrimaryKey() {
        return null;
    }

    public F1GranPrix getGp() {
        return gp;
    }

    public void setGp(F1GranPrix gp) {
        this.gp = gp;
    }

    public F1Entrant getEntrant() {
        return entrant;
    }

    public void setEntrant(F1Entrant entrant) {
        this.entrant = entrant;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getLaps() {
        return laps;
    }

    public void setLaps(int laps) {
        this.laps = laps;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public JkDuration getTime() {
        return time;
    }

    public void setTime(JkDuration time) {
        this.time = time;
    }
}
