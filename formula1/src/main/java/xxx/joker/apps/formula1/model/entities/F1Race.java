package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.tests.JkTests;
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
    private F1RaceOutcome outcome;
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

    public Integer getLaps() {
        return laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    public F1RaceOutcome getOutcome() {
        return outcome;
    }

    public void setOutcome(F1RaceOutcome outcome) {
        this.outcome = outcome;
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

    public enum F1RaceOutcome {
        EXCLUDED("EX"),
        NOT_QUALIFIED("DNQ"),
        NOT_CLASSIFIABLE("NC"),
        DISQUALIFIED("DSQ"),
        RETIRED("Ret"),
        NOT_STARTED("DNS"),
        FINISHED
        ;

        private String label;

        F1RaceOutcome() {
            label = "";
        }

        F1RaceOutcome(String label) {
            this.label = label;
        }

        public static F1RaceOutcome byLabel(String label) {
            if(JkTests.isInt(label))    return FINISHED;

            for (F1RaceOutcome ro : values()) {
                if(label.equalsIgnoreCase(ro.label)) {
                    return ro;
                }
            }
            return null;
        }

        public String getLabel() {
            return label;
        }
    }
}
