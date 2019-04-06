package xxx.joker.apps.formula1.parsers;

import xxx.joker.libs.core.runtimes.JkReflection;

import java.util.Map;

public interface IWikiParser {

    void parse();

    Map<String, Integer> getExpectedDriverPoints();
    Map<String, Integer> getExpectedTeamPoints();

    static IWikiParser getParser(int year) {
        return JkReflection.createInstanceSafe("xxx.joker.apps.formula1.parsers.Year"+year);
    }

}
