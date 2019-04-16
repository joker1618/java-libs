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
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ToStringRepo {

    static F1Model model = F1ModelImpl.getInstance();
    static int yearDef = 2016;



    @Test
    public void showCDT() {
        showCircuits();
        showDrivers();
        showTeams();
    }

    @Test
    public void showTeams() {
        List<String> lines = new ArrayList<>();
        lines.add("ID|TEAM NAME|NATION");
        lines.addAll(JkStreams.map(model.getTeams(), t -> strf("{}|{}|{}", t.getEntityID(), t.getTeamName(), t.getNation())));
        display("TEAMS\n{}", JkOutput.columnsView(lines, "|", 2));
    }

    @Test
    public void showDrivers() {
        List<String> lines = new ArrayList<>();
        lines.add("ID|DRIVER NAME|NATION|BDATE|CITY");
        lines.addAll(JkStreams.map(model.getDrivers(), t -> strf("{}|{}|{}|{}|{}",
                t.getEntityID(), t.getFullName(), t.getNation(), t.getBirthDate(), t.getCity()
        )));
        display("DRIVERS\n{}", JkOutput.columnsView(lines, "|", 2));
    }

    @Test
    public void showCircuits() {
        List<String> lines = new ArrayList<>();
        lines.add("ID|NATION|CITY");
        lines.addAll(JkStreams.map(model.getCircuits(), t -> strf("{}|{}|{}",
                t.getEntityID(), t.getNation(), t.getCity()
        )));
        display("CIRCUITS\n{}", JkOutput.columnsView(lines, "|", 2));
    }

    @Test
    public void showEntrants() {
        int year = 2018;
//        int year = -1;
        showEntrants(year != -1 ? year : yearDef);
    }
    public void showEntrants(int year) {
        List<F1Entrant> elist = model.getEntrants(year);
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|TEAM|T_NATION|ENGINE|NUM|DRIVER|D_NATION"));
        lines.addAll(JkStreams.map(elist, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        display("*** Entrants ({})\n{}", elist.size(), cols);
    }

    @Test
    public void showAllGPDescription() {
        model.getAvailableYears().forEach(this::showGPDescription);
    }

    @Test
    public void showGPDescription() {
        int year = 2011;
        showGPDescription(year != -1 ? year : yearDef);
    }
    public void showGPDescription(int year) {
        List<F1GranPrix> gpList = model.getGranPrixs(year);
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|NUM|DATE|LAP LEN|NUM LAPS|CITY|NATION|N.Q|N.R|FAST LAP"));
        lines.addAll(JkStreams.map(gpList, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        display("*** Gran Prix ({})\n{}", gpList.size(), cols);
    }

    @Test
    public void showGPTimes() {
        int year = 2016;
        showGPTimes(year != -1 ? year : yearDef);
    }
    public void showGPTimes(int year) {
        List<F1GranPrix> gplist = model.getGranPrixs(year);
        gplist.forEach(gp -> display("{}\n\n{}\n\n\n\n", toColsQualify(gp.getQualifies()), toColsRaces(gp.getRaces())));
    }


    private static String toLine(F1Entrant e) {
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

    private static String toLine(F1GranPrix gp) {
        return strf("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",
                gp.getYear(),
                gp.getEntityID(),
                gp.getNum(),
                gp.getDate(),
                gp.getLapLength(),
                gp.getNumLapsRace(),
                gp.getCircuit().getCity(),
                gp.getCircuit().getNation(),
                gp.getQualifies().size(),
                gp.getRaces().size(),
                gp.getFastLap().toLine()
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
            q.getEntrant().getDriver().getFullName(),
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
