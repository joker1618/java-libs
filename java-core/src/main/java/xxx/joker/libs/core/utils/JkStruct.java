package xxx.joker.libs.core.utils;

import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.*;
import java.util.function.Predicate;

public class JkStruct {

    public static <T> List<T> getDuplicates(Collection<T> source) {
        List<T> uniques = new ArrayList<>();
        List<T> dups = new ArrayList<>();
        for(T elem : source) {
            if(!uniques.contains(elem))     uniques.add(elem);
            else                            dups.add(elem);
        }
        return dups;
    }
    public static <T> List<T> getDuplicates(Collection<T> source, Comparator<T> comparator) {
        List<T> uniques = new ArrayList<>();
        List<T> dups = new ArrayList<>();
        for(T elem : source) {
            boolean found = !JkStreams.filter(uniques, u -> comparator.compare(u, elem) == 0).isEmpty();
            if(!found)	uniques.add(elem);
            else        dups.add(elem);
        }
        return dups;
    }

    public static <K,V> List<K> getMapKeys(Map<K,V> map, Predicate<V>... valueFilters) {
        List<K> keys = new ArrayList<>();
        map.entrySet().forEach(e -> {
            boolean res = true;
            for (Predicate<V> valueFilter : valueFilters) {
                res &= valueFilter.test(e.getValue());
            }
            if(res) {
                keys.add(e.getKey());
            }
        });
        return keys;
    }
    public static <K,V> List<V> getMapValues(Map<K,V> map, Predicate<K>... keyFilters) {
        List<V> values = new ArrayList<>();
        map.entrySet().forEach(e -> {
            boolean res = true;
            for (Predicate<K> keyFilter : keyFilters) {
                res &= keyFilter.test(e.getKey());
            }
            if(res) {
                values.add(e.getValue());
            }
        });
        return values;
    }

    public static <T> T getLastElem(Collection<T> coll) {
        List<T> list = JkConvert.toList(coll);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

}
