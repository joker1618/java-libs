package util;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.apps.formula1.parsers.IWikiParser;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;

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
        int year = 2018;
        List<F1GranPrix> gp = model.getGranPrixs(year);
        List<F1Race> races = gp.stream().flatMap(g -> g.getRaces().stream()).collect(Collectors.toList());
        Map<String, List<F1Race>> byDriver = JkStreams.toMap(races, r -> r.getEntrant().getDriver().getDriverName());
        List<Pair<String,Integer>> list = new ArrayList<>();
        byDriver.forEach((k,v) -> list.add(Pair.of(k, v.stream().mapToInt(F1Race::getPoints).sum())));
        List<Pair<String, Integer>> sorted = JkStreams.reverseOrder(list, Comparator.comparingInt(Pair::getValue));
        Map<String, Integer> expected = IWikiParser.getParser(year).getExpectedDriverPoints();
        List<String> lines = new ArrayList<>();
        lines.add("DRIVER|EXPECTED||COMPUTED");
        for (Pair<String, Integer> pair : sorted) {
            Integer exp = expected.get(pair.getKey());
            String line = strf("{}|{}|{}|{}", pair.getKey(), exp, exp.intValue() == pair.getValue() ? "" : "<>", pair.getValue());
            lines.add(line);
        }
        display("DRIVER POINT CHECK {}\n{}", year, JkOutput.columnsView(lines, "|", 2));
    }
}
