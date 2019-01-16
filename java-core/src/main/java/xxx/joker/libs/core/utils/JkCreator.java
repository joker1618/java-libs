package xxx.joker.libs.core.utils;

import java.util.Arrays;
import java.util.TreeSet;

public class JkCreator {

    public static <T extends Comparable> TreeSet<T> newTreeSet(T... elems) {
        return new TreeSet<>(Arrays.asList(elems));
    }


}
