package util;

import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ToStringRepo {

    public static String toCols(List<F1Entrant> el) {
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|TEAM|ENGINE|NUM|DRIVER"));
        lines.addAll(JkStreams.map(el, ToStringRepo::toLine));
        String cols = JkOutput.columnsView(lines, "|", 2);
        return strf("*** Entrants ({})\n{}", el.size(), cols);
    }
    public static List<String> toLines(List<F1Entrant> el) {
        List<String> lines = new ArrayList<>();
        lines.add(strf("YEAR|ID|TEAM|ENGINE|NUM|DRIVER"));
        lines.addAll(JkStreams.map(el, ToStringRepo::toLine));
        return lines;
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
}
