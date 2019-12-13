package unitTests;

import org.junit.Test;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;

import static xxx.joker.libs.core.util.JkConsole.display;

public class TestTagmod {


    @Test
    public void testTagmod() {
        String strArgs = "b1 bb4";
        InputParser parser = new ConsoleInputParser(TmcArgs.class, TmcArgType.class, TmcCmd.class, true);

        TmcArgs iarg = parser.parse(strArgs);
        display(iarg.strInfo());

    }

}
