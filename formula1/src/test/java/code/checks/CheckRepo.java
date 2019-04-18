package code.checks;

import org.junit.Test;
import xxx.joker.apps.formula1.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1GranPrix;
import xxx.joker.libs.core.format.JkOutput;

import java.util.ArrayList;
import java.util.List;

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
