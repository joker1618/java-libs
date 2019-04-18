package xxx.joker.apps.formula1.dataCreator.parsers;

import xxx.joker.libs.core.runtimes.JkReflection;

import java.util.Map;

public interface WikiParser {

    void parse();

    Map<String, Integer> getExpectedDriverPoints();
    Map<String, Integer> getExpectedTeamPoints();

    static WikiParser getParser(int year) {
        return JkReflection.createInstanceSafe("xxx.joker.apps.formula1.dataCreator.parsers.years.Year"+year);
    }

}
