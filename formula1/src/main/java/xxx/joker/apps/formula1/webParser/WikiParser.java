package xxx.joker.apps.formula1.webParser;

import xxx.joker.libs.core.runtimes.JkReflection;

import java.util.Map;

public interface WikiParser {

    void parse();

    Map<String, Double> getExpectedDriverPoints();
    Map<String, Double> getExpectedTeamPoints();

    static WikiParser getParser(int year) {
        return JkReflection.createInstanceSafe("xxx.joker.apps.formula1.webParser.years.Year"+year);
    }

}
