package xxx.joker.apps.formula1.old.dataCreator.model.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Qualify extends RepoEntity {

    @RepoField
    private String gpPK;
    @RepoField
    private int pos;
    @RepoField
    private F1Entrant entrant;
    @RepoField
    private int finalGrid;
    @RepoField
    private List<JkDuration> times;

    public F1Qualify() {

    }

    @Override
    public String getPrimaryKey() {
        return strf("%s-qualify-%02d", gpPK, pos);
    }

    public String getGpPK() {
        return gpPK;
    }

    public void setGpPK(String gpPK) {
        this.gpPK = gpPK;
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

    public int getFinalGrid() {
        return finalGrid;
    }

    public void setFinalGrid(int finalGrid) {
        this.finalGrid = finalGrid;
    }

    public List<JkDuration> getTimes() {
        return times;
    }

    public void setTimes(List<JkDuration> times) {
        this.times = times;
    }
}
