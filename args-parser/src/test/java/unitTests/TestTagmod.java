package unitTests;

import org.junit.Test;
import xxx.joker.libs.argsparser.ConsoleInputParser;
import xxx.joker.libs.argsparser.InputParser;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.Arrays;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class TestTagmod {


    @Test
    public void testTagmod() {
        String strArgs = "b1 bb4";
        InputParser parser = new ConsoleInputParser(TmcArgs.class, TmcArgType.class, TmcCmd.class, true);

        TmcArgs iarg = parser.parse(strArgs);
        display(iarg.strInfo());

    }

}
