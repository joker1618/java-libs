package xxx.joker.apps.formula1.old.dataCreator.model.util;

import xxx.joker.apps.formula1.old.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.old.dataCreator.model.F1ModelImpl;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1Entrant;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1Qualify;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1Race;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1Formatter {

    private final F1Model model = F1ModelImpl.getInstance();

    public String toColsEntrants(int year) {
        List<F1Entrant> elist = model.getEntrants(year);
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|TEAM|T_NATION|ENGINE|NUM|DRIVER|D_NATION"));
        lines.addAll(JkStreams.map(elist, this::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Entrants ({})\n{}", elist.size(), cols);
    }



    private String toColsQualify(List<F1Qualify> qList) {
        List<String> lines = new ArrayList<>();
        String header = "GP|ID|POS|DRIVER";
        for(int i = 0; i < qList.get(0).getTimes().size(); i++) {
            header += "|Q" + i;
        }
        header += "|GRID";
        lines.add(header);
        lines.addAll(JkStreams.map(qList, this::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Qualify ({})\n{}", qList.size(), cols);
    }

    private String toColsRaces(List<F1Race> rList) {
        List<String> lines = new ArrayList<>();
        lines.add("GP|ID|POS|DRIVER|LAPS|RETIRED|TIME|POINTS");
        if(!rList.isEmpty()) {
            JkDuration winnerTime = rList.get(0).getTime();
            lines.addAll(JkStreams.map(rList, r -> toLine(r, winnerTime)));
        }
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Race ({})\n{}", rList.size(), cols);
    }

    private String toLine(F1Entrant e) {
        return strf("{}|{}|{}|{}|{}|{}|{}|{}",
                e.getYear(),
                e.getEntityID(),
                e.getTeam().getTeamName(),
                e.getTeam().getNation(),
                e.getEngine(),
                e.getCarNo(),
                e.getDriver().getFullName(),
                e.getDriver().getNation()
        );
    }

    private String toLine(F1GranPrix gp) {
        return strf("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
                gp.getYear(),
                gp.getEntityID(),
                gp.getNum(),
                gp.getDate(),
                gp.getLapLength(),
                gp.getNumLapsRace(),
                gp.getCircuit(),
                gp.getQualifies().size(),
                gp.getRaces().size(),
                gp.getFastLap().format()
        );
    }

    private String toLine(F1Qualify q) {
        String strTimes = JkStreams.join(q.getTimes(), "|", d -> d == null ? "-" : d.toStringElapsed());
        return strf("{}|{}|{}|{}|{}|{}",
                q.getGpPK(),
                q.getEntityID(),
                q.getPos(),
                q.getEntrant().getDriver().getFullName(),
                strTimes,
                q.getFinalGrid()
        );
    }

    private String toLine(F1Race r, JkDuration winnerTime) {
        String strTime = "";
        if(r.getTime() != null) {
            JkDuration diff = r.getTime().diff(winnerTime);
            strTime = diff.toMillis() == 0L ? r.getTime().toStringElapsed() : "+" + diff.toStringElapsed();
        }
        return strf("{}|{}|{}|{}|{}|{}|{}|{}",
                r.getGpPK(),
                r.getEntityID(),
                r.getPos(),
                r.getEntrant().getDriver().getFullName(),
                r.getLaps(),
                r.isRetired() ? "RET" : "",
                strTime,
                r.getPoints()
        );
    }
}
