package code.checks;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.apps.formula1.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Driver;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Entrant;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Team;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.repository.design.RepoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CheckRepo {

    private F1Model model = F1ModelImpl.getInstance();

    @Test
    public void checkAllYears() {
        model.getAvailableYears().forEach(this::checkYear);
    }

    @Test
    public void checkYear() {
        int year = 2018;
        checkYear(year);
    }

    public void checkYear(int year) {
        // todo impl
    }

    public String checkEntrants(List<F1Entrant> entrants) {
        List<String> lines = new ArrayList<>();

        // Check entrants
        List<F1Entrant> res = JkStreams.filter(entrants, e -> e.getCarNo() <= 0);
        if(!res.isEmpty()) {
            lines.add(strf("* car no <= 0 ({})", res.size()));
            res.forEach(e -> lines.add("    "+e.strFull()));
        }

        res = JkStreams.filter(entrants, e -> StringUtils.isBlank(e.getEngine()));
        if(!res.isEmpty()) {
            lines.add(strf("* blank engine ({})", res.size()));
            res.forEach(e -> lines.add("    "+e.strFull()));
        }

        // Check num drivers
        List<F1Team> teams = JkStreams.map(entrants, F1Entrant::getTeam);
        Map<F1Team, List<F1Driver>> map = JkStreams.toMap(entrants, F1Entrant::getTeam, F1Entrant::getDriver);

        List<F1Team> tres = JkStreams.filter(teams, t -> !map.containsKey(t) || map.get(t).size() < 2);
        if(!tres.isEmpty()) {
            lines.add(strf("* Not enough drivers"));
            tres.forEach(e -> lines.add("    "+e.strFull()));
        }

        String toRet = strf("ENTRANTS ({}):", entrants.size());
        if(lines.isEmpty())     toRet += "\tOK";
        else                    toRet += "\n" + JkStreams.join(lines);

        return toRet.trim();
    }

    public String checkDrivers(List<F1Driver> drivers) {
        List<String> lines = new ArrayList<>();

        // Check drivers
        for (F1Driver driver : drivers) {
            boolean res = checkEntityValues(driver, driver.getFullName(), driver.getBirthDate(), driver.getNation(), driver.getCity());
            if(!res) {
                lines.add(driver.strFull());
            }
        }

        String toRet = strf("DRIVERS ({}):", drivers.size());
        if(lines.isEmpty())     toRet += "\tOK";
        else                    toRet += "\n" + JkStreams.join(lines);

        return toRet.trim();
    }

    public String checkTeams(List<F1Team> teams) {
        List<String> lines = new ArrayList<>();

        // Check drivers
        for (F1Team team : teams) {
            boolean res = checkEntityValues(team, team.getTeamName(), team.getNation());
            if(!res) {
                lines.add(team.strFull());
            }
        }

        String toRet = strf("TEAMS ({}):", teams.size());
        if(lines.isEmpty())     toRet += "\tOK";
        else                    toRet += "\n" + JkStreams.join(lines);

        return toRet.trim();
    }

    private boolean checkEntityValues(RepoEntity e, Object... values) {
        if(e == null || e.getEntityID() == null || StringUtils.isBlank(e.getPrimaryKey())) {
            return false;
        }

        for (Object o : values) {
            if(o == null) {
                return false;
            }
            if(o instanceof String && StringUtils.isBlank((String)o)) {
                return false;
            }
            if(JkReflection.isInstanceOf(o.getClass(), JkFormattable.class)) {
                if(StringUtils.isBlank(((JkFormattable)o).format())) {
                    return false;
                }
            }
        }

        return true;
    }

    public String checkGranPrixes(int year) {
        List<String> lines = new ArrayList<>();
        for (F1GranPrix gp : model.getGranPrixs(year)) {

        }
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }

    public String checkTimesRace(int year) {
        List<String> lines = new ArrayList<>();
        for (F1GranPrix gp : model.getGranPrixs(year)) {

        }
        // todo impl
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }

    public String checkTimesQualify(int year) {
        List<String> lines = new ArrayList<>();
        // todo impl
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }

    public String checkCircuits() {
        List<String> lines = new ArrayList<>();
        // todo impl
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }

    public String checkPointsDrivers(int year) {
        List<String> lines = new ArrayList<>();
        // todo impl
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }

    public String checkPointsTeams(int year) {
        List<String> lines = new ArrayList<>();
        // todo impl
        return lines.isEmpty() ? "" : JkOutput.columnsView(lines, "|", 2);
    }
}
