package xxx.joker.libs.core.utils;

import xxx.joker.libs.core.exception.JkRuntimeException;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by f.barbano on 25/05/2018.
 */
public class JkStreams {

	public static <T> String join(T[] source, String separator) {
		return join(Arrays.asList(source), separator);
	}
	public static <T> String join(Collection<T> source, String separator) {
		return join(source, separator, String::valueOf);
	}
	public static <T> String join(T[] source, String separator, Function<T,String> mapFunc) {
		return join(Arrays.asList(source), separator, mapFunc);
	}
	public static <T> String join(Collection<T> source, String separator, Function<T,String> mapFunc) {
		return source.stream().map(mapFunc).collect(Collectors.joining(separator));
	}

	public static <T> List<T> filter(Collection<T> source, Predicate<T>... filters) {
		Stream<T> stream = source.stream();
		for(Predicate<T> filter : filters) {
			stream = stream.filter(filter);
		}
		return stream.collect(Collectors.toList());
	}
	public static <T,U> List<U> map(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> filterAndMap(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> mapAndFilter(Collection<T> source, Function<T,U> mapper, Predicate<U>... filters) {
		Stream<U> stream = source.stream().map(mapper);
		for(Predicate<U> filter : filters) {
			stream = stream.filter(filter);
		}
		return stream.collect(Collectors.toList());
	}

    public static <T> List<T> distinct(Collection<T> source) {
        return source.stream().distinct().collect(Collectors.toList());
    }
	public static <T> List<T> distinctSorted(Collection<T> source) {
		return source.stream().sorted().distinct().collect(Collectors.toList());
	}
	public static <T> List<T> distinctSorted(Collection<T> source, Comparator<T> comparator) {
		return source.stream().sorted(comparator).distinct().collect(Collectors.toList());
	}
	public static <T> List<T> sorted(Collection<T> source) {
		return source.stream().sorted().collect(Collectors.toList());
	}
    public static <T> List<T> sorted(Collection<T> source, Comparator<T> comparator) {
        return source.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static <T> List<T> duplicates(Collection<T> source) {
        List<T> uniques = new ArrayList<>();
        List<T> dups = new ArrayList<>();
        for(T elem : source) {
            if(!uniques.contains(elem))     uniques.add(elem);
            else                            dups.add(elem);
        }
        return sorted(dups);
    }

	public static <V,K> Map<K,List<V>> toMap(Collection<V> source, Function<V,K> keyMapper) {
		return toMap(source, keyMapper, v -> v);
	}
    public static <V,K,T> Map<K,List<T>> toMap(Collection<V> source, Function<V,K> keyMapper, Function<V,T> valueMapper, Predicate<V>... filters) {
        Map<K,List<T>> map = new HashMap<>();

        if(source != null && !source.isEmpty()) {
            Stream<V> stream = source.stream();
            for(Predicate<V> filter : filters) {
                stream = stream.filter(filter);
            }
            stream.forEach(v -> {
                K key = keyMapper.apply(v);
                List<T> value = map.get(key);
                if(value == null) {
                    value = new ArrayList<>();
                    map.put(key, value);
                }
                value.add(valueMapper.apply(v));
            });
        }

        return map;
    }
	public static <V,K> Map<K,V> toMapSingle(Collection<V> source, Function<V,K> keyMapper) {
		return toMapSingle(source, keyMapper, v -> v);
	}
	public static <V,K,T> Map<K,T> toMapSingle(Collection<V> source, Function<V,K> keyMapper, Function<V,T> valueMapper, Predicate<V>... filters) {
		Map<K,T> map = new HashMap<>();

		if(source != null && !source.isEmpty()) {
            Stream<V> stream = source.stream();
            for(Predicate<V> filter : filters) {
                stream = stream.filter(filter);
            }
            stream.forEach(v -> {
				K key = keyMapper.apply(v);
				if(map.containsKey(key)) {
					throw new JkRuntimeException("Multiple values found for key [%s]", key);
				}
				map.put(key, valueMapper.apply(v));
			});
		}

		return map;
	}

	public static <K,V> List<K> getMapKeys(Map<K,V> map, Predicate<V> valuePred) {
		List<K> keys = new ArrayList<>();
		map.entrySet().forEach(e -> {
			if(valuePred.test(e.getValue())) {
				keys.add(e.getKey());
			}
		});
		return keys;
	}
}
