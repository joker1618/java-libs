package stuff.checkers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelChecker;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.apps.formula1.webParser.WikiParser;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkStruct;
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
        checkTeams();
        checkDrivers();
        checkCircuits();
        model.getAvailableYears().forEach(this::doYearChecks);
    }

    @Test
    public void doYearChecks() {
        int year = JkStruct.getLastElem(model.getAvailableYears());
//        int year = 2012;
        checkTeams();
        checkDrivers();
        checkCircuits();
        doYearChecks(year);
    }

    private void doYearChecks(int year) {
        display("CHECK YEAR {}", year);
        checkEntrants(year);
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

    private void checkCircuits() {
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
            JkDuration fastTime = gp.getFastLap().getLapTime();
            if(fastTime == null || fastTime.toMillis() < (1000*60) || fastTime.toMillis() > (1000*60*3)) {
                display("Invalid fast lap time {}", gp);
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

        display("{}   CHECK POINT", (!tres || !dres ? "--KO--" : "OK"));
    }
    private boolean checkPoints(Map<String, List<F1Race>> raceMap, Map<String, Double> expected) {
        List<Pair<String,Double>> list = new ArrayList<>();
        raceMap.forEach((k,v) -> list.add(Pair.of(k, v.stream().mapToDouble(F1Race::getPoints).sum())));
        List<Pair<String, Double>> sorted = JkStreams.reverseOrder(list, Comparator.comparingDouble(Pair::getValue));
        for (Pair<String, Double> pair : sorted) {
            Double exp = expected.get(pair.getKey());
            if(exp.doubleValue() != pair.getValue()) {
                return false;
            }
        }
        return true;
    }

    private void checkQualifies(int year) {
        List<F1Qualify> qList = JkStreams.flatMap(model.getGranPrixs(year), F1GranPrix::getQualifies);
        Map<F1Qualify, List<String>> qErrors = F1ModelChecker.checkNullEmptyFields(qList);
        printRes("QUALIFIES", qErrors);

        int numRounds = model.getNumQualifyRounds(year);
        model.getGranPrixs(year).forEach(gp -> {
            if(gp.getYear() == 2003 && JkTests.equalsAny(gp.getNum(), 15, 16)) {
                return;
            }

            List<F1Qualify> qlist = gp.getQualifies();
            F1Qualify winner = qlist.get(0);
            int wrounds = JkStreams.filter(winner.getTimes(), Objects::nonNull).size();
            int expectedRounds;
            if(gp.getPrimaryKey().equals("gp-2015-16")) {
                expectedRounds = 2;
            } else {
                expectedRounds = numRounds;
            }
            if(expectedRounds != wrounds || !checkQualTimes(winner)) {
                display("Invalid winner qualify times {}", winner);
            }
            JkStreams.filter(qlist, q -> !checkQualTimes(q)).forEach(q -> display("Invalid qualify times {}", q));
        });
    }
    private boolean checkQualTimes(F1Qualify qual) {
        int min = 1000 * 60;
        int max = 1000 * 60 * 3;

        if(qual.getGpPK().matches("^gp-2005-0[123]")) {
            max += 1000 * 60;
        }

        boolean foundNull = false;
        for (JkDuration time : qual.getTimes()) {
            if(time != null && (time.toMillis() < min || time.toMillis() > max)) {
                return false;
            }
            if(foundNull && time != null) {
                return false;
            } else if(time == null) {
                foundNull = true;
            }
        }
        return true;
    }

    private void checkRaces(int year) {
        List<F1Race> rList = JkStreams.flatMap(model.getGranPrixs(year), F1GranPrix::getRaces);
        Map<F1Race, List<String>> rErrors = F1ModelChecker.checkNullEmptyFields(rList);
        rErrors.values().forEach(c -> c.remove("time"));
        rErrors.entrySet().removeIf(e -> e.getValue().isEmpty());
        printRes("RACES", rErrors);

        model.getGranPrixs(year).forEach(gp -> {
            List<F1Race> races = gp.getRaces();
            F1Race winnerRace = races.get(0);
            JkDuration wtime = winnerRace.getTime();
            if(wtime == null || !checkRaceTime(winnerRace)) {
                display("Wrong winner time {}", winnerRace);
            }
            Integer numLaps = winnerRace.getLaps();
            if(numLaps == null || numLaps <= 0) {
                display("Invalid winner num laps {}", winnerRace);
            }
            races.forEach(r -> {
                if(!r.isRetired() && r.getTime() == null && r.getLaps() == numLaps) {
                    display("Invalid race time {}", r);
                }
                if(r.getTime() != null && r.getLaps() < numLaps) {
                    display("Invalid race laps {}", r);
                }
                if(r.getTime() != null && !checkRaceTime(r)) {
                    display("Invalid race time range {}", r);
                }
            });
        });
    }
    private boolean checkRaceTime(F1Race race) {
        int min = 1000 * 60 * 60;
        int max = 1000 * 60 * 60 * 3;
        if(race.getGpPK().equals("gp-2016-20")) {
            max += 1000 * 60 * 3;
        } else if(race.getGpPK().equals("gp-2011-07")) {
            max += 1000 * 60 * 60;
            max += 1000 * 60 * 6;
        } else if(race.getGpPK().equals("gp-2009-02")) {
            min = 1000 * 60 * 55;
            max = 1000 * 60 * 60;
        }
        return !(race.getTime().toMillis() < min || race.getTime().toMillis() > max);
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
