package xxx.joker.libs.javalibs.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by f.barbano on 26/05/2018.
 */
public class JkTests {

	/* BOOLEAN TESTS */
	public static boolean isBoolean(String source) {
		return source.toLowerCase().equals("true") || source.toLowerCase().equals("false");
	}
	public static boolean isBooleanArray(String[] source) {
		return isBooleanList(Arrays.asList(source));
	}
	public static boolean isBooleanList(List<String> source) {
		for(String elem : source) {
			if(!isBoolean(elem)) {
				return false;
			}
		}
		return true;
	}


	/* INTEGER TESTS */
	public static boolean isInteger(String str) {
		try {
			new Integer(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	public static boolean isIntegerArray(String[] source) {
		return isIntegerList(Arrays.asList(source));
	}
	public static boolean isIntegerList(List<String> source) {
		for(String elem : source) {
			if(!isInteger(elem)) {
				return false;
			}
		}
		return true;
	}


	/* DOUBLE TESTS */
	public static boolean isDouble(String str) {
		try {
			new Double(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	public static boolean isDoubleArray(String[] source) {
		return isDoubleList(Arrays.asList(source));
	}
	public static boolean isDoubleList(List<String> source) {
		for(String elem : source) {
			if(!isDouble(elem)) {
				return false;
			}
		}
		return true;
	}


	/* LONG TESTS */
	public static boolean isLong(String str) {
		try {
			new Long(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	public static boolean isLongArray(String[] source) {
		return isLongList(Arrays.asList(source));
	}
	public static boolean isLongList(List<String> source) {
		for(String elem : source) {
			if(!isLong(elem)) {
				return false;
			}
		}
		return true;
	}


	/* NUMBERS TEST */
	public static boolean isNumber(String str) {
		return isInteger(str) || isLong(str) || isDouble(str);
	}
	public static boolean isNumberArray(String[] source) {
		return isNumberList(Arrays.asList(source));
	}
	public static boolean isNumberList(List<String> source) {
		for(String str : source) {
			if(!isNumber(str)) {
				return false;
			}
		}
		return true;
	}


	/* DATE AND TIME TESTS */
	public static boolean isLocalDate(String source, DateTimeFormatter format) {
		try {
			format.parse(source);
			return true;
		} catch(DateTimeParseException ex) {
			return false;
		}
	}
	public static boolean isLocalTime(String source, DateTimeFormatter format) {
		try {
			format.parse(source);
			return true;
		} catch(DateTimeParseException ex) {
			return false;
		}
	}
	public static boolean isLocalDateTime(String source, DateTimeFormatter format) {
		try {
			format.parse(source);
			return true;
		} catch(DateTimeParseException ex) {
			return false;
		}
	}
	public static boolean isLocalDateArray(String[] source, DateTimeFormatter format) {
		for(String elem : source) {
			if(!isLocalDate(elem, format)) {
				return false;
			}
		}
		return true;
	}
	public static boolean isLocalTimeArray(String[] source, DateTimeFormatter format) {
		for(String elem : source) {
			if(!isLocalTime(elem, format)) {
				return false;
			}
		}
		return true;
	}
	public static boolean isLocalDateTimeArray(String[] source, DateTimeFormatter format) {
		for(String elem : source) {
			if(!isLocalDateTime(elem, format)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isValidDateTimeFormatter(String format) {
		try {
			DateTimeFormatter.ofPattern(format);
			return true;
		} catch(IllegalArgumentException ex) {
			return false;
		}
	}


	/* GENERICS TESTS */
	public static <T> boolean duplicatesPresents(List<T> sourceList) {
		for(int i = 0; i < sourceList.size(); i++) {
			for(int j = i+1; j < sourceList.size(); j++) {
				if(sourceList.get(i).equals(sourceList.get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	public static <T> boolean duplicatesPresents(T[] sourceArray) {
		return duplicatesPresents(Arrays.asList(sourceArray));
	}

	public static <T> boolean isEmpty(T[] array) {
		return (array == null || array.length == 0);
	}
	public static <T> boolean isEmpty(List<T> list) {
		return (list == null || list.size() == 0);
	}
	public static <K, V> boolean isEmpty(Map<K, V> map) {
		return (map == null || map.size() == 0);
	}

}
