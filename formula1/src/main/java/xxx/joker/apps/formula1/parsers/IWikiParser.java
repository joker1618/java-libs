package xxx.joker.apps.formula1.parsers;

import xxx.joker.libs.core.runtimes.JkReflection;

public interface IWikiParser {

    void parse();

    static IWikiParser getParser(int year) {
        return JkReflection.createInstanceSafe("xxx.joker.apps.formula1.parsers.Year"+year);
    }

}
