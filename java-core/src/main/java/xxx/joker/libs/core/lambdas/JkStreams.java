package xxx.joker.libs.core.lambdas;

import xxx.joker.libs.core.exception.JkRuntimeException;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JkStreams {

	public static <T,U> List<U> map(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilter(Collection<T> source, Function<T,U> mapper, Predicate<U>... filters) {
		Stream<U> stream = source.stream().map(mapper);
		for(Predicate<U> filter : filters) {
			stream = stream.filter(filter);
		}
		return stream.collect(Collectors.toList());
	}
	public static <T,U> List<U> mapSort(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).sorted().collect(Collectors.toList());
	}
	public static <T,U> List<U> mapSort(Collection<T> source, Function<T,U> mapper, Comparator<U> sorter) {
		return source.stream().map(mapper).sorted(sorter).collect(Collectors.toList());
	}
	public static <T,U> List<U> mapUniq(Collection<T> source, Function<T,U> mapper) {
		return source.stream().map(mapper).distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilterSort(Collection<T> source, Function<T,U> mapper, Predicate<U> filter) {
		return source.stream().map(mapper).filter(filter).sorted().collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilterSort(Collection<T> source, Function<T,U> mapper, Predicate<U> filter, Comparator<U> sorter) {
		return source.stream().map(mapper).filter(filter).sorted(sorter).collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilterUniq(Collection<T> source, Function<T,U> mapper, Predicate<U> filter) {
		return source.stream().map(mapper).filter(filter).distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilterSortUniq(Collection<T> source, Function<T,U> mapper, Predicate<U> filter) {
		return source.stream().map(mapper).filter(filter).sorted().distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> mapFilterSortUniq(Collection<T> source, Function<T,U> mapper, Predicate<U> filter, Comparator<U> sorter) {
		return source.stream().map(mapper).filter(filter).sorted(sorter).distinct().collect(Collectors.toList());
	}

	public static <T> List<T> filter(Collection<T> source, Predicate<T>... filters) {
		Stream<T> stream = source.stream();
		for(Predicate<T> filter : filters) {
			stream = stream.filter(filter);
		}
		return stream.collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMap(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).collect(Collectors.toList());
	}
	public static <T> List<T> filterSort(Collection<T> source, Predicate<T> filter) {
		return source.stream().filter(filter).sorted().collect(Collectors.toList());
	}
	public static <T> List<T> filterSort(Collection<T> source, Predicate<T> filter, Comparator<T> sorter) {
		return source.stream().filter(filter).sorted(sorter).collect(Collectors.toList());
	}
	public static <T> List<T> filterUniq(Collection<T> source, Predicate<T> filter) {
		return source.stream().filter(filter).distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMapSort(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).sorted().collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMapSort(Collection<T> source, Predicate<T> filter, Function<T,U> mapper, Comparator<U> sorter) {
		return source.stream().filter(filter).map(mapper).sorted(sorter).collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMapUniq(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMapSortUniq(Collection<T> source, Predicate<T> filter, Function<T,U> mapper) {
		return source.stream().filter(filter).map(mapper).sorted().distinct().collect(Collectors.toList());
	}
	public static <T,U> List<U> filterMapSortUniq(Collection<T> source, Predicate<T> filter, Function<T,U> mapper, Comparator<U> sorter) {
		return source.stream().filter(filter).map(mapper).sorted(sorter).distinct().collect(Collectors.toList());
	}

	public static <T> List<T> sorted(Collection<T> source) {
		return source.stream().sorted().collect(Collectors.toList());
	}
	public static <T> List<T> sorted(Collection<T> source, Comparator<T> sorter) {
		return source.stream().sorted(sorter).collect(Collectors.toList());
	}
	public static <T> List<T> sortUniq(Collection<T> source) {
		return source.stream().sorted().distinct().collect(Collectors.toList());
	}
	public static <T> List<T> sortUniq(Collection<T> source, Comparator<T> sorter) {
		return source.stream().sorted(sorter).distinct().collect(Collectors.toList());
	}
	public static <T> List<T> distinct(Collection<T> source) {
		return source.stream().distinct().collect(Collectors.toList());
	}
	public static <T> List<T> reverseOrder(Collection<T> source) {
		List<T> list = sorted(source);
		Collections.reverse(list);
		return list;
	}
	public static <T> List<T> reverseOrder(Collection<T> source, Comparator<T> sorter) {
		List<T> list = sorted(source, sorter);
		Collections.reverse(list);
		return list;
	}

	public static <T> String join(Collection<T> source, String separator) {
		return source.stream().map(Object::toString).collect(Collectors.joining(separator));
	}
	public static <T> String join(Collection<T> source, String separator, Function<T,String> mapFunc) {
		return source.stream().map(mapFunc).collect(Collectors.joining(separator));
	}

	public static <T> T findExactMatch(Collection<T> source, Predicate<T>... filters) {
		Stream<T> stream = source.stream();
		for(Predicate<T> filter : filters) {
			stream = stream.filter(filter);
		}
		List<T> list = stream.collect(Collectors.toList());
		return list.size() == 1 ? list.get(0) : null;
	}
	public static <T> T findFirstMatch(Collection<T> source, Predicate<T>... filters) {
		Stream<T> stream = source.stream();
		for(Predicate<T> filter : filters) {
			stream = stream.filter(filter);
		}
		List<T> list = stream.collect(Collectors.toList());
		return !list.isEmpty() ? list.get(0) : null;
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
					throw new JkRuntimeException("Multiple values found for key {}", key);
				}
				map.put(key, valueMapper.apply(v));
			});
		}

		return map;
	}

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
			boolean found = !filter(uniques, u -> comparator.compare(u, elem) == 0).isEmpty();
			if(!found)	uniques.add(elem);
			else        dups.add(elem);
		}
		return dups;
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
	public static <K,V> List<V> getMapValues(Map<K,V> map, Predicate<K> keyPred) {
		List<V> values = new ArrayList<>();
		map.entrySet().forEach(e -> {
			if(keyPred.test(e.getKey())) {
				values.add(e.getValue());
			}
		});
		return values;
	}

}