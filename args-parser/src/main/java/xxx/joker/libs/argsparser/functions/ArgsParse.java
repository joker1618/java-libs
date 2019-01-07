package xxx.joker.libs.argsparser.functions;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.utils.JkConverter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by f.barbano on 10/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class ArgsParse {

	public static UnaryOperator<String[]> normalizeBoolean() {
		return arr -> {
			String[] toRet = new String[arr.length];
			for(int i = 0; i < arr.length; i++) {
				toRet[i] = StringUtils.equalsAnyIgnoreCase(arr[i], "true", "false") ? arr[i] : "false";
			}
			return toRet;
		};
	}

	public static UnaryOperator<String[]> windowsPathFormat() {
		return JkConverter::windowsPathFormat;
	}

	public static UnaryOperator<Object[]> orderInt(boolean desc) {
		return orderInt(desc, false);
	}

	public static UnaryOperator<Object[]> orderDistinctInt(boolean desc) {
		return orderInt(desc, true);
	}

	private static UnaryOperator<Object[]> orderInt(boolean desc, boolean distinct) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			Stream<Integer> stream = Arrays.stream(intArr);
			if(distinct) {
				stream = stream.distinct();
			}
			List<Integer> collect = stream.sorted((o1, o2) -> desc ? o2 - o1 : o1 - o2).collect(Collectors.toList());
			return collect.toArray(new Integer[0]);
		};
	}

	public static UnaryOperator<Object[]> distinctInt() {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			List<Integer> distincts = Arrays.stream(intArr).distinct().collect(Collectors.toList());
			return distincts.toArray(new Integer[0]);
		};
	}

	public static UnaryOperator<Object[]> distinctPath() {
		return distinctPath(false);
	}

	public static UnaryOperator<Object[]> distinctPath(boolean absolutePath) {
		return arr -> {
			Path[] paths = (Path[]) arr;
			List<Path> abs = new ArrayList<>();
			List<Path> distinct = new ArrayList<>();
			for(Path p : paths) {
				Path absPath = p.toAbsolutePath();
				if(!abs.contains(absPath)) {
					abs.add(absPath);
					distinct.add(absolutePath ? absPath : p);
				}
			}
			return distinct.toArray(new Path[0]);
		};
	}

}
