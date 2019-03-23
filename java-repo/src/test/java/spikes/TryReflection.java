package spikes;


import org.junit.Test;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class TryReflection {

    int[] iarr = null;
    Double[] darr = null;

    @Test
    public void tryref() throws Exception {
        display("{}", Arrays.toString(BB.class.getDeclaredFields()));
        display("{}", JkReflection.getFieldsByAnnotation(BB.class, Deprecated.class));

        Field fiarr = TryReflection.class.getDeclaredField("iarr");
        display("{}", fiarr.getType().getName());
        display("{}", fiarr.getType().getComponentType());

        Class<?> fclazz = Boolean.class;
        display("{}", Arrays.asList(boolean.class, Boolean.class).contains(fclazz));
    }
}

class BB {
    @Deprecated
    int primo;
    @Deprecated
    String secondo;
    @Deprecated
    Integer terzo;
}