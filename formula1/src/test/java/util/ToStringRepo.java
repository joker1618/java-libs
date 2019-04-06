package util;

import org.junit.Test;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Qualify;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ToStringRepo {

    static F1Model model = F1ModelImpl.getInstance();
    int yearDef = -1;


    @Test
    public void strEntrants() {
        int year = 2018;
        List<F1Entrant> elist = model.getEntrants(yearDef == -1 ? year : yearDef);
        display(toColsEntrants(elist));
    }

    @Test
    public void strGPs() {
        int year = 2018;
        List<F1GranPrix> gplist = model.getGranPrixs(yearDef == -1 ? year : yearDef);
        display(toColsGPs(gplist));
    }

    @Test
    public void strGPsTimes() {
        int year = 2018;
        List<F1GranPrix> gplist = model.getGranPrixs(yearDef == -1 ? year : yearDef);
        display(toColsGPs(gplist));
        gplist.forEach(gp -> display("{}\n\n{}\n\n\n\n", toColsQualify(gp.getQualifies()), toColsRaces(gp.getRaces())));
    }
    public static void strGPsFull() {
        int year = 2018;
        List<F1GranPrix> gplist = model.getGranPrixs(year);
        display(toColsGPs(gplist));
        gplist.forEach(gp -> display("{}\n\n{}\n\n\n\n", toColsQualify(gp.getQualifies()), toColsRaces(gp.getRaces())));
    }


    public static String toColsEntrants(Collection<F1Entrant> el) {
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|TEAM|ENGINE|NUM|DRIVER"));
        lines.addAll(JkStreams.map(el, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Entrants ({})\n{}", el.size(), cols);
    }
    public static String toLine(F1Entrant e) {
        return strf("{}|{}|{}|{}|{}|{}",
            e.getYear(),
            e.getEntityID(),
            e.getTeam().getTeamName(),
            e.getEngine(),
            e.getCarNum(),
            e.getDriver().getDriverName()
        );
    }

    public static String toColsGPs(Collection<F1GranPrix> gpList) {
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|NUM|DATE|LAP LEN|NUM LAPS|CITY|NATION|N.Q|N.R"));
        lines.addAll(JkStreams.map(gpList, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Gran Prix ({})\n{}", gpList.size(), cols);
    }
    public static String toLine(F1GranPrix gp) {
        return strf("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
            gp.getYear(),
            gp.getEntityID(),
            gp.getNum(),
            gp.getDate(),
            gp.getLapLength(),
            gp.getNumLapsRace(),
            gp.getCity(),
            gp.getNation(),
            gp.getQualifies().size(),
            gp.getRaces().size()
        );
    }

    public static String toColsQualify(List<F1Qualify> qList) {
        List<String> lines = new ArrayList<>();
        String header = "GP|ID|POS|DRIVER";
        for(int i = 0; i < qList.get(0).getTimes().size(); i++) {
            header += "|Q" + i;
        }
        header += "|GRID";
        lines.add(header);
        lines.addAll(JkStreams.map(qList, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Qualify ({})\n{}", qList.size(), cols);
    }
    public static String toLine(F1Qualify q) {
        String strTimes = JkStreams.join(q.getTimes(), "|", d -> d == null ? "-" : d.toStringElapsed());
        return strf("{}|{}|{}|{}|{}|{}",
            q.getGpPK(),
            q.getEntityID(),
            q.getPos(),
            q.getEntrant().getDriver().getDriverName(),
            strTimes,
            q.getFinalGrid()
        );
    }

    public static String toColsRaces(List<F1Race> rList) {
        List<String> lines = new ArrayList<>();
        lines.add("GP|ID|POS|DRIVER|LAPS|RETIRED|TIME|POINTS");
        if(!rList.isEmpty()) {
            JkDuration winnerTime = rList.get(0).getTime();
            lines.addAll(JkStreams.map(rList, r -> toLine(r, winnerTime)));
        }
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Race ({})\n{}", rList.size(), cols);
    }
    public static String toLine(F1Race r, JkDuration winnerTime) {
        String strTime = "";
        if(r.getTime() != null) {
            JkDuration diff = r.getTime().diff(winnerTime);
            strTime = diff.toMillis() == 0L ? r.getTime().toStringElapsed() : diff.toStringElapsed();
        }
        return strf("{}|{}|{}|{}|{}|{}|{}|{}",
            r.getGpPK(),
            r.getEntityID(),
            r.getPos(),
            r.getEntrant().getDriver().getDriverName(),
            r.getLaps(),
            r.isRetired() ? "RET" : "",
            strTime,
            r.getPoints()
        );
    }
}
