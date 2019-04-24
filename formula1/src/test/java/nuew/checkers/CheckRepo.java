package nuew.checkers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelChecker;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;
import xxx.joker.apps.formula1.nuew.model.entities.*;
import xxx.joker.apps.formula1.nuew.webParser.WikiParser;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.design.RepoEntity;

import java.util.*;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CheckRepo {
    
    F1Model model = F1ModelImpl.getInstance();

    @Before
    public void before() {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("xxx.joker");
        rootLogger.setLevel(Level.OFF);
    }
    
    @Test
    public void doAllYearChecks() {
        model.getAvailableYears().forEach(this::doYearChecks);
    }

    @Test
    public void doYearChecks() {
        int year = 2012;
        doYearChecks(year);
    }

    private void doYearChecks(int year) {
        display("CHECK YEAR {}", year);
        checkEntrants(year);
        checkTeams();
        checkDrivers();
        checkCicuits();
        checkGranPrix(year);
        checkQualifies(year);
        checkRaces(year);
        checkPoints(year);
    }

    private void checkEntrants(int year) {
        List<F1Entrant> entrants = model.getEntrants(year);
        Map<F1Entrant, List<String>> entrantErrors = F1ModelChecker.checkNullEmptyFields(entrants);
        printRes("ENTRANTS", entrantErrors);
    }

    private void checkDrivers() {
        Set<F1Driver> drivers = model.getDrivers();
        Map<F1Driver, List<String>> driversError = F1ModelChecker.checkNullEmptyFields(drivers);
        printRes("DRIVERS", driversError);

        for (F1Driver driver : drivers) {
            if(model.getDriverCover(driver) == null) {
                display("No driver cover for {}", driver);
            }
        }
    }

    private void checkTeams() {
        Set<F1Team> teams = model.getTeams();
        Map<F1Team, List<String>> teamsError = F1ModelChecker.checkNullEmptyFields(teams);
        printRes("TEAMS", teamsError);
    }

    private void checkCicuits() {
        Set<F1Circuit> circuits = model.getCircuits();
        Map<F1Circuit, List<String>> circuitsError = F1ModelChecker.checkNullEmptyFields(circuits);
        printRes("CIRCUITS", circuitsError);
    }

    private void checkGranPrix(int year) {
        List<F1GranPrix> gpList = model.getGranPrixs(year);
        Map<F1GranPrix, List<String>> gpErrors = F1ModelChecker.checkNullEmptyFields(gpList);
        printRes("GRAN PRIX", gpErrors);

        gpList.forEach(gp -> {
            if(model.getGpTrackMap(gp) == null) {
                display("No track map for {}", gp);
            }
        });
    }

    private void checkPoints(int year) {
        List<F1GranPrix> gp = model.getGranPrixs(year);
        List<F1Race> races = gp.stream().flatMap(g -> g.getRaces().stream()).collect(Collectors.toList());

        Map<String, List<F1Race>> byDriver = JkStreams.toMap(races, r -> r.getEntrant().getDriver().getFullName());
        boolean dres = checkPoints(byDriver, WikiParser.getParser(year).getExpectedDriverPoints());

        Map<String, List<F1Race>> byTeam = JkStreams.toMap(races, r -> r.getEntrant().getTeam().getTeamName());
        boolean tres = checkPoints(byTeam, WikiParser.getParser(year).getExpectedTeamPoints());

        display("{}   CHECK POINT", (!tres || !dres ? "KO" : "OK"));
    }
    private boolean checkPoints(Map<String, List<F1Race>> raceMap, Map<String, Integer> expected) {
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

    private void checkQualifies(int year) {
        List<F1Qualify> qList = JkStreams.flatMap(model.getGranPrixs(year), F1GranPrix::getQualifies);
        Map<F1Qualify, List<String>> qErrors = F1ModelChecker.checkNullEmptyFields(qList);
        printRes("QUALIFIES", qErrors);
    }

    private void checkRaces(int year) {
        List<F1Race> rList = JkStreams.flatMap(model.getGranPrixs(year), F1GranPrix::getRaces);
        Map<F1Race, List<String>> rErrors = F1ModelChecker.checkNullEmptyFields(rList);
        rErrors.values().forEach(c -> c.remove("time"));
        rErrors.entrySet().removeIf(e -> e.getValue().isEmpty());
        printRes("RACES", rErrors);
    }

    private void printRes(String label, Map<? extends RepoEntity, List<String>> mapErr) {
        StringBuilder sb = new StringBuilder(label);
        if(mapErr.isEmpty()) {
            sb.insert(0, "OK   ");
        } else {
            mapErr.forEach((e,fl) -> sb.append(strf("\n  - {}\n     {}", e, fl)));
        }
        display(sb.toString());
    }

}
