package various;

import org.junit.Test;
import xxx.joker.libs.core.runtime.JkReflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConsole.displayColl;

public class Spikes {

    public String simpleString;
    public Map<Double, List<Integer>> mapList = new HashMap<>();

    @Test
    public void testParTypes() {
        Field fa = JkReflection.getFieldByName(AA.class, "field");
        Field fb = JkReflection.getFieldByName(BB.class, "field");
        Field fa1 = JkReflection.getFieldByName(AA.class, "field");
        display(fa.toString());
        display(fb.toString());
        display(fa1.toString());
        display("fa, fb:  {}  {}", fa == fb, fa.equals(fb));
        display("fa, fa1: {}  {}", fa == fa1, fa.equals(fa1));
    }

    class AA {
        String field;
    }
    class BB {
        String field;
    }
}
