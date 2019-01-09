package stuff;

import org.junit.Test;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.oldargsparser.model.CmdParam;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

@ToAnalyze
@Deprecated
public class Various {

    boolean fieldBool;
    Boolean fieldBoolean;

    @Test
    public void testVari() throws NoSuchFieldException {
        Field field = Various.class.getDeclaredField("fieldBool");
        display("{}\t{}\t{}\t{}", field.getName(), field.getType(), field.getType().getName());
        field = Various.class.getDeclaredField("fieldBoolean");
        display("{}\t{}\t{}", field.getName(), field.getType(), field.getType().getName());
    }

}
