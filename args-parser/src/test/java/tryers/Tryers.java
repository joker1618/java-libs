package tryers;

import org.junit.Test;
import xxx.joker.libs.argsparser.common.Configs;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Tryers {


    @Test
    public void displaySupportedClasses() {
        display(Configs.toStringSupportedClasses());
    }

}
