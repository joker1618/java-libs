package stuff;

import org.junit.Test;

import java.lang.reflect.Field;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Various {

    boolean[] fieldBool;
    Boolean[] fieldBoolean;

    @Test
    public void testVari2() throws NoSuchFieldException {
        Field field = Various.class.getDeclaredField("fieldBool");
        display("{}\t{}\t{}\t{}\t{}", field.getName(), field.getType(), field.getType().getName(), field.getType()==boolean[].class, field.getDeclaringClass().getName());
        field = Various.class.getDeclaredField("fieldBoolean");
        display("{}\t{}\t{}\t{}\t{}", field.getName(), field.getType(), field.getType().getName(), field.getDeclaringClass(), field.getDeclaringClass().getName());
    }

}
