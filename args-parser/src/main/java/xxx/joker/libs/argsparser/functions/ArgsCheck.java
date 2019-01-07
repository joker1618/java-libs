package xxx.joker.libs.argsparser.functions;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.utils.JkFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Created by f.barbano on 30/08/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class ArgsCheck {

	public static Function<Object[], String> stringNotEmpty() {
		return arr -> {
			String[] strArr = (String[]) arr;
			for(String str : strArr) {
				if(str.isEmpty()) {
					return "empty string found";
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> stringNotBlank() {
		return arr -> {
			String[] strArr = (String[]) arr;
			for(String str : strArr) {
				if(StringUtils.isBlank(str)) {
					return "blank string found";
				}
			}
			return null;
		};
	}

	public static Function<Object[], String> allowedValues(boolean ignoreCase, String... allowedValues) {
		return arr -> {
			if(arr != null) {
				String[] strArr = (String[]) arr;
				for (String str : strArr) {
					boolean equals = ignoreCase ?
										 StringUtils.equalsAnyIgnoreCase(str, allowedValues) :
										 StringUtils.equalsAny(str, allowedValues);
					if (!equals) {
						return String.format("found: '%s', allowed values: %s", str, Arrays.toString(allowedValues));
					}
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> allowedValues(Integer... allowedValues) {
		return arr -> {
			if(arr != null) {
				Integer[] intArr = (Integer[]) arr;
				List<Integer> allowedList = Arrays.asList(allowedValues);
				for (int num : intArr) {
					if (!allowedList.contains(num)) {
						return String.format("found: '%d', allowed values: %s", num, allowedList);
					}
				}
			}
			return null;
		};
	}

	public static Function<Object[], String> pathExists() {
		return arr -> {
			Path[] pathArr = (Path[]) arr;
			for(Path path : pathArr) {
				if(!Files.exists(path)) {
					return String.format("path \"%s\" does not exists", path);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> pathIsFile() {
		return arr -> {
			Path[] pathArr = (Path[]) arr;
			String errorMex = pathExists().apply(arr);
			if(StringUtils.isNotBlank(errorMex)) {
				return errorMex;
			}
			for(Path path : pathArr) {
				if(!Files.isRegularFile(path)) {
					return String.format("path \"%s\" is not a file", path);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> pathIsFolder() {
		return arr -> {
			Path[] pathArr = (Path[]) arr;
			String errorMex = pathExists().apply(arr);
			if(StringUtils.isNotBlank(errorMex)) {
				return errorMex;
			}
			for(Path path : pathArr) {
				if(!Files.isDirectory(path)) {
					return String.format("path \"%s\" is not a folder", path);
				}
			}
			return null;
		};
	}

	public static Function<Object[], String> pathsExtensionsAllowed(String... allowedFileExt) {
		return arr -> {
			Path[] pathArr = (Path[]) arr;
			for(Path path : pathArr) {
				String fext = JkFiles.getExtension(path);
				boolean equals = StringUtils.equalsAnyIgnoreCase(fext, allowedFileExt);
				if(!equals) {
					return String.format("File ext allowed: %s, filename: '%s'", allowedFileExt, path.getFileName());
				}
			}
			return null;
		};
	}

	public static Function<Object[], String> pathsParentEquals() {
		return arr -> {
			Path[] pathArr = (Path[]) arr;
			Path parent = null;
			for(Path path : pathArr) {
				Path thisParent = path.toAbsolutePath().getParent();
				if(parent == null) {
					parent = thisParent;
				} else if(!parent.equals(thisParent)) {
					return String.format("all paths must be in the same folder");
				}
			}
			return null;
		};
	}

	public static Function<Object[], String> rangeStartOffset(int minStartNumber) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;

			if(intArr.length != 2)	{
				return String.format("2 values expected (START and OFFSET), %d found", arr.length);
			}

			int start = intArr[0];
			int offset = intArr[1];

			if(start < minStartNumber) {
				return String.format("START must be >= %d (found %d)", minStartNumber, start);
			}
			if(offset <= 0) {
				return String.format("OFFSET must be > 0 (found %d)", offset);
			}
			return null;
		};
	}

	public static Function<Object[], String> numValuesExpected(int num) {
		return numValuesExpected(num, num);
	}

	public static Function<Object[], String> numValuesExpected(int min, int max) {
		return arr -> {
			int len = arr.length;
			if(len < min || len > max)	{
				return String.format("values found %d, allowed values number [%d, %d]", len, min, max);
			}
			return null;
		};
	}

	public static Function<Object[], String> intNE(Integer thresold) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num == thresold) {
					return String.format("number %d must be != %d", num, thresold);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> intGE(Integer thresold) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num < thresold) {
					return String.format("number %d must be >= %d", num, thresold);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> intGT(Integer thresold) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num <= thresold) {
					return String.format("number %d must be > %d", num, thresold);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> intLE(Integer thresold) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num > thresold) {
					return String.format("number %d must be <= %d", num, thresold);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> intLT(Integer thresold) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num >= thresold) {
					return String.format("number %d must be < %d", num, thresold);
				}
			}
			return null;
		};
	}
	public static Function<Object[], String> intBetween(boolean extremesIncluded, Integer from, Integer to) {
		return arr -> {
			Integer[] intArr = (Integer[]) arr;
			for(int num : intArr) {
				if(num < from || num > to || (!extremesIncluded && (num == from || num == to))) {
					return String.format("number %d must belong to range %s%d, %d%s",
						num,
						extremesIncluded ? "[" : "(",
						from, to,
						extremesIncluded ? "]" : ")"
					);
				}
			}
			return null;
		};
	}

}
