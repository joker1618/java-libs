package util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.apps.formula1.parsers.WikiParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CheckPoints {

    F1Model model = F1ModelImpl.getInstance();

    @Test
    public void checkPoints() {
        int year = 2017;
        checkPoints(year);
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
