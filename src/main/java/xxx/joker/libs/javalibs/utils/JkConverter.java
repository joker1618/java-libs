package xxx.joker.libs.javalibs.utils;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.util.*;

/**
 * Created by f.barbano on 29/05/2017.
 */
public class JkConverter {

	/* Conversions between data structures types */
	public static <T> TreeSet<T> toTreeSet(T[] source) {
		return source == null ? null : new TreeSet<>(Arrays.asList(source));
	}
	public static <T> TreeSet<T> toTreeSet(Collection<T> source) {
		return source == null ? null : new TreeSet<>(source);
	}

	public static <T> HashSet<T> toHashSet(T[] source) {
		return source == null ? null : new HashSet<>(Arrays.asList(source));
	}
	public static <T> HashSet<T> toHashSet(Collection<T> source) {
		return source == null ? null : new HashSet<>(source);
	}

	public static <T> ArrayList<T> toArrayList(T[] source) {
		return source == null ? null : new ArrayList<>(Arrays.asList(source));
	}
	public static <T> ArrayList<T> toArrayList(Collection<T> source) {
		return source == null ? null : new ArrayList<>(source);
	}

	/* Conversions between numbers */
	public static Integer stringToInteger(String str) {
		try {
			return Integer.valueOf(str);
		} catch(NumberFormatException ex) {
			return null;
		}
	}
	public static Integer stringToInteger(String str, int defaultValue) {
		try {
			return Integer.valueOf(str);
		} catch(NumberFormatException ex) {
			return defaultValue;
		}
	}
	public static Integer[] stringToInteger(String[] source) {
		Integer[] toRet = new Integer[source.length];
		for(int i = 0; i < source.length; i++) {
			Integer num = stringToInteger(source[i]);
			if(num == null)		return null;
			toRet[i] = num;
		}
		return toRet;
	}

	public static Long stringToLong(String str) {
		try {
			return Long.valueOf(str);
		} catch(NumberFormatException ex) {
			return null;
		}
	}
	public static Long[] stringToLong(String[] source) {
		Long[] toRet = new Long[source.length];
		for(int i = 0; i < source.length; i++) {
			Long num = stringToLong(source[i]);
			if(num == null)		return null;
			toRet[i] = num;
		}
		return toRet;
	}

	public static Double stringToDouble(String str) {
		try {
			return Double.valueOf(str);
		} catch(NumberFormatException ex) {
			return null;
		}
	}
	public static Double[] stringToDouble(String[] source) {
		Double[] toRet = new Double[source.length];
		for(int i = 0; i < source.length; i++) {
			Double num = stringToDouble(source[i]);
			if(num == null)		return null;
			toRet[i] = num;
		}
		return toRet;
	}

	public static Float stringToFloat(String str) {
		try {
			return Float.valueOf(str);
		} catch(NumberFormatException ex) {
			return null;
		}
	}
	public static Float[] stringToFloat(String[] source) {
		Float[] toRet = new Float[source.length];
		for(int i = 0; i < source.length; i++) {
			Float num = stringToFloat(source[i]);
			if(num == null)		return null;
			toRet[i] = num;
		}
		return toRet;
	}

	public static Boolean[] stringToBoolean(String[] source) {
		Boolean[] toRet = new Boolean[source.length];
		for(int i = 0; i < source.length; i++) {
			toRet[i] = Boolean.valueOf(source[i]);
		}
		return toRet;
	}
	public static Path[] stringToPath(String[] source) {
		Path[] toRet = new Path[source.length];
		for(int i = 0; i < source.length; i++) {
			toRet[i] = Paths.get(source[i]);
		}
		return toRet;
	}

	// Windows <--> Cygwin  path format conversions
	public static String cygwinPathFormat(Path windowsPath) {
		return cygwinPathFormat(windowsPath.toString());
	}
	public static String cygwinPathFormat(String windowsPath) {
		return changePathFormat(windowsPath, true);
	}
	public static String[] cygwinPathFormat(String[] windowsPaths) {
		return changePathFormat(windowsPaths, true);
	}

	public static String windowsPathFormat(Path cygwinPath) {
		return windowsPathFormat(cygwinPath.toString());
	}
	public static String windowsPathFormat(String cygwinPath) {
		return changePathFormat(cygwinPath, false);
	}
	public static String[] windowsPathFormat(String[] cygwinPaths) {
		return changePathFormat(cygwinPaths, false);
	}

	private static String[] changePathFormat(String[] paths, boolean toCygPath) {
		String[] toRet = new String[paths.length];
		for(int i = 0; i < toRet.length; i++) {
			toRet[i] = toCygPath ? cygwinPathFormat(paths[i]) : windowsPathFormat(paths[i]);
		}
		return toRet;
	}
	private static String changePathFormat(String sourcePath, boolean toCygPath) {
		if(sourcePath == null) {
			return null;
		}

		String toRet = "";

		if(StringUtils.isNotBlank(sourcePath)) {
			if(toCygPath) {
				toRet = sourcePath.trim().replace("\\", "/").replaceAll("/+$", "");

				if(toRet.length() >= 2) {
					char firstChar = toRet.charAt(0);

					if (Character.isAlphabetic(toRet.charAt(0)) && toRet.charAt(1) == ':') {
						String temp = "/cygdrive/" + Character.toLowerCase(firstChar);
						if(toRet.length() > 2) {
							temp += "/" + toRet.substring(2);
						}
						toRet = temp.replace("//", "/");
					}
				}
			} else {
				toRet = sourcePath.trim().replaceAll("/+$", "");

				if(toRet.startsWith("/cygdrive")) {
					toRet = toRet.replaceFirst("/cygdrive", "");
					toRet = toRet.replaceAll("^/", "");

					if(toRet.isEmpty()) {
						/**
						 * NB: assignment not totally right!!!
						 * 'cygdrive' has to be followed by a Character identifying the Volume
						 * As a workaround, will be assigned the file system root (the Volume where the O.S. reside)
						 */
						toRet = "/";
					} else {
						int idx = toRet.indexOf("/");
						String temp = idx == -1 ? "" : "/" + toRet.substring(idx+1);
						toRet = toRet.substring(0, 1).toUpperCase() + ":" + temp;
						toRet = toRet.replace("/", "\\");
					}
				}
			}
		}

		return toRet;
	}
}
