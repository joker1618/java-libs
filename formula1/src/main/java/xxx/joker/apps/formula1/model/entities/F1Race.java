package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Race extends RepoEntity {

    @RepoField
    private String gpPK;
    @RepoField
    private Integer pos;
    @RepoField
    private Integer startGrid;
    @RepoField
    private F1Entrant entrant;
    @RepoField
    private Integer laps;
    @RepoField
    private Boolean retired;
    @RepoField
    private JkDuration time;
    @RepoField
    private Double points;

    public F1Race() {
    }

    @Override
    public String getPrimaryKey() {
        return strf("%s-race-%02d", gpPK, pos);
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

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getStartGrid() {
        return startGrid;
    }

    public void setStartGrid(Integer startGrid) {
        this.startGrid = startGrid;
    }

    public Boolean getRetired() {
        return retired;
    }

    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    public Boolean isRetired() {
        return retired;
    }

    public void setRetired(Boolean retired) {
        this.retired = retired;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public JkDuration getTime() {
        return time;
    }

    public void setTime(JkDuration time) {
        this.time = time;
    }
}
