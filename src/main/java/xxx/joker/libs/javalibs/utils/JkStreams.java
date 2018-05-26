package xxx.joker.libs.javalibs.utils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

	public static <T> List<T> filter(Collection<T> source, Predicate<T> filter) {
		return source.stream().filter(filter).collect(Collectors.toList());
	}
	public static <T,U> List<U> map(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> filterAndMap(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> mapAndFilter(Collection<T> source, Function<T,U> mapper, Predicate<U> filter) {
		return source.stream().map(mapper).filter(filter).collect(Collectors.toList());
	}

	public static <T> List<T> distinct(List<T> source) {
		return source.stream().distinct().collect(Collectors.toList());
	}
	public static <T> List<T> duplicates(List<T> source) {
		List<T> duplicates = new ArrayList<>(source);
		source.stream().distinct().forEach(duplicates::remove);
		return duplicates;
	}

	public static <V,K> Map<K,List<V>> toMap(Collection<V> source, Function<V,K> keyMapper) {
		Map<K,List<V>> map = new HashMap<>();

		if(source != null) {
			source.forEach(v -> {
				K key = keyMapper.apply(v);
				List<V> value = map.get(key);
				if(value == null) {
					value = new ArrayList<>();
					map.put(key, value);
				}
				value.add(v);
			});
		}

		return map;
	}
	public static <V,K> Map<K,V> toMapSingle(Collection<V> source, Function<V,K> keyMapper) {
		Map<K,V> map = new HashMap<>();

		if(source != null) {
			source.forEach(v -> {
				K key = keyMapper.apply(v);
				map.put(key, v);
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
