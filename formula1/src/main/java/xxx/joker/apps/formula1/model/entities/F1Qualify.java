package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Qualify extends RepoEntity {

    @RepoField
    private F1GranPrix gp;
    @RepoField
    private F1Entrant entrant;
    @RepoField
    private int pos;
    @RepoField
    private int finalGrid;
    @RepoField
    private List<JkDuration> qualTimes;

    public F1Qualify() {

    }

    @Override
    public String getPrimaryKey() {
        return strf("{}-{}", gp.getPrimaryKey(), pos);
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

    public int getFinalGrid() {
        return finalGrid;
    }

    public void setFinalGrid(int finalGrid) {
        this.finalGrid = finalGrid;
    }

    public List<JkDuration> getQualTimes() {
        return qualTimes;
    }

    public void setQualTimes(List<JkDuration> qualTimes) {
        this.qualTimes = qualTimes;
    }
}
