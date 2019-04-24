package nuew.printer;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.BeforeClass;
import org.junit.Test;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;
import xxx.joker.apps.formula1.nuew.model.entities.F1Entrant;
import xxx.joker.apps.formula1.nuew.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.nuew.model.entities.F1Race;
import xxx.joker.apps.formula1.nuew.webParser.WikiParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.core.utils.JkStruct;
import xxx.joker.libs.repository.util.RepoUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ShowData {

    F1Model model = F1ModelImpl.getInstance();
    static int defYear = -1;

    @BeforeClass
    public static void beforeClazz() {
//        defYear = 2015;
        defYear = JkStruct.getLastElem(F1ModelImpl.getInstance().getAvailableYears());
    }

    @Test
    public void singleEntrants() {
        int year = 2018;
        List<F1Entrant> entrants = model.getEntrants(defYear == -1 ? year : defYear);
        display(RepoUtil.formatEntities(entrants));
    }

    @Test
    public void doShowCircuits() {
        display(RepoUtil.formatEntities(model.getCircuits()));
    }

    @Test
    public void doShowTeams() {
        display(RepoUtil.formatEntities(model.getTeams()));
    }

    @Test
    public void doShowDrivers() {
        display(RepoUtil.formatEntities(model.getDrivers()));
    }

    @Test
    public void allGranPrixs() {
        model.getAvailableYears().forEach(year -> {
            List<F1GranPrix> gpList = model.getGranPrixs(year);
            display(RepoUtil.formatEntities(gpList));
        });
    }
    @Test
    public void singleGranPrixs() {
        int year = 2018;
        List<F1GranPrix> gpList = model.getGranPrixs(defYear == -1 ? year : defYear);
        display(RepoUtil.formatEntities(gpList));
    }

    @Test
    public void singleQualifiesRaces() {
        int year = 2018;
        List<F1GranPrix> gpList = model.getGranPrixs(defYear == -1 ? year : defYear);
        for (F1GranPrix gp : gpList) {
            display(RepoUtil.formatEntities(gp.getQualifies()));
            display(RepoUtil.formatEntities(gp.getRaces()));
        }
//        F1GranPrix gp = gpList.get(0);
//        display(RepoUtil.formatEntities(gp.getQualifies()));
//        display(RepoUtil.formatEntities(gp.getRaces()));
    }

    @Test
    public void allCheckPoints() {
        model.getAvailableYears().forEach(this::checkPoints);
    }
    @Test
    public void singleCheckPoints() {
        int year = 2018;
        checkPoints(defYear == -1 ? year : defYear);
    }
    public void checkPoints(int year) {
        List<F1GranPrix> gp = model.getGranPrixs(year);
        List<F1Race> races = gp.stream().flatMap(g -> g.getRaces().stream()).collect(Collectors.toList());

        Map<String, List<F1Race>> byDriver = JkStreams.toMap(races, r -> r.getEntrant().getDriver().getFullName());
        printList(year, "DRIVER", byDriver, WikiParser.getParser(year).getExpectedDriverPoints());

        Map<String, List<F1Race>> byTeam = JkStreams.toMap(races, r -> r.getEntrant().getTeam().getTeamName());
        printList(year, "TEAM", byTeam, WikiParser.getParser(year).getExpectedTeamPoints());
    }

    public void printList(int year, String label, Map<String, List<F1Race>> raceMap, Map<String, Integer> expected) {
        List<Pair<String,Integer>> list = new ArrayList<>();
        raceMap.forEach((k,v) -> list.add(Pair.of(k, v.stream().mapToInt(F1Race::getPoints).sum())));
        List<Pair<String, Integer>> sorted = JkStreams.reverseOrder(list, Comparator.comparingInt(Pair::getValue));
        List<String> lines = new ArrayList<>();
        lines.add(label+"|EXPECTED||COMPUTED");
        for (Pair<String, Integer> pair : sorted) {
            Integer exp = expected.get(pair.getKey());
            String line = strf("{}|{}|{}|{}", pair.getKey(), exp, exp.intValue() == pair.getValue() ? "" : "<>", pair.getValue());
            lines.add(line);
        }
        display("{} POINT CHECK {}\n{}", label, year, JkStrings.leftPadLines(JkOutput.columnsView(lines, "|", 2), " ", 2));
    }

}
