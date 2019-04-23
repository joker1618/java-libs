package nuew.checkers;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelChecker;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;
import xxx.joker.apps.formula1.nuew.model.entities.*;
import xxx.joker.apps.formula1.nuew.webParser.WikiParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.*;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CheckRepo {
    
    F1Model model = F1ModelImpl.getInstance();
    
    @Test
    public void doYearChecks() {
        int year = 2018;

        checkEntrants(year);
        checkTeams();
        checkDrivers();
        checkCicuits();
        checkGranPrix(year);
        checkPoints(year);
    }
    
    private void checkEntrants(int year) {
        List<F1Entrant> entrants = model.getEntrants(year);
        Map<F1Entrant, List<String>> entrantErrors = F1ModelChecker.checkNullEmptyFields(entrants);
        StringBuilder sb = new StringBuilder(strf("ENTRANTS {}", year));
        if(entrantErrors.isEmpty()) {
            sb.append("   -->   OK");
        } else {
            entrantErrors.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());
    }

    private void checkDrivers() {
        Set<F1Driver> drivers = model.getDrivers();
        Map<F1Driver, List<String>> driversError = F1ModelChecker.checkNullEmptyFields(drivers);
        StringBuilder sb = new StringBuilder("DRIVERS");
        if(driversError.isEmpty()) {
            sb.append("   -->   OK");
        } else {
            driversError.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());

        for (F1Driver driver : drivers) {
            if(model.getDriverCover(driver) == null) {
                display("No driver cover for {}", driver);
            }
        }
    }

    private void checkTeams() {
        Set<F1Team> teams = model.getTeams();
        Map<F1Team, List<String>> teamsError = F1ModelChecker.checkNullEmptyFields(teams);
        StringBuilder sb = new StringBuilder("TEAMS");
        if(teamsError.isEmpty()) {
            sb.append("   -->   OK");
        } else {
            teamsError.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());
    }

    private void checkCicuits() {
        Set<F1Circuit> circuits = model.getCircuits();
        Map<F1Circuit, List<String>> circuitsError = F1ModelChecker.checkNullEmptyFields(circuits);
        StringBuilder sb = new StringBuilder("CIRCUITS");
        if(circuitsError.isEmpty()) {
            sb.append("   -->   OK");
        } else {
            circuitsError.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());
    }

    private void checkGranPrix(int year) {
        List<F1GranPrix> gpList = model.getGranPrixs(year);
        Map<F1GranPrix, List<String>> gpErrors = F1ModelChecker.checkNullEmptyFields(gpList);
        StringBuilder sb = new StringBuilder(strf("GRAN PRIX {}", year));
        if(gpErrors.isEmpty()) {
            sb.append("   -->   OK");
        } else {
            gpErrors.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());
    }

    public void checkPoints(int year) {
        List<F1GranPrix> gp = model.getGranPrixs(year);
        List<F1Race> races = gp.stream().flatMap(g -> g.getRaces().stream()).collect(Collectors.toList());

        Map<String, List<F1Race>> byDriver = JkStreams.toMap(races, r -> r.getEntrant().getDriver().getFullName());
        boolean dres = checkPoints(byDriver, WikiParser.getParser(year).getExpectedDriverPoints());

        Map<String, List<F1Race>> byTeam = JkStreams.toMap(races, r -> r.getEntrant().getTeam().getTeamName());
        boolean tres = checkPoints(byTeam, WikiParser.getParser(year).getExpectedTeamPoints());

        display("CHECK POINT {}   -->   {}", year, (!tres || !dres ? "KO" : "OK"));
    }

    public boolean checkPoints(Map<String, List<F1Race>> raceMap, Map<String, Integer> expected) {
        List<Pair<String,Integer>> list = new ArrayList<>();
        raceMap.forEach((k,v) -> list.add(Pair.of(k, v.stream().mapToInt(F1Race::getPoints).sum())));
        List<Pair<String, Integer>> sorted = JkStreams.reverseOrder(list, Comparator.comparingInt(Pair::getValue));
        for (Pair<String, Integer> pair : sorted) {
            Integer exp = expected.get(pair.getKey());
            if(exp.intValue() != pair.getValue()) {
                return false;
            }
        }
        return true;
    }
}
