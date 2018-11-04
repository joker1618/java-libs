package xxx.joker.libs.core.utils;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 25/05/2018.
 */
public class JkStrings {

	public static String strf(String format, Object... params) {
		return String.format(format, params);
	}

	public static String[] splitAllFields(String source, String separatorString) {
		return splitAllFields(source, separatorString, false);
	}
	public static String[] splitAllFields(String source, String separatorString, boolean trimValues) {
		return splitAllFields(source, separatorString, trimValues, true);
	}
	public static String[] splitAllFields(String source, String separatorString, boolean trimValues, boolean removeSeparator) {
		if(StringUtils.isEmpty(source)) {
			return new String[0];
		}

		String[] splitted = source.split(Pattern.quote(separatorString));
		int numFields = StringUtils.countMatches(source, separatorString) + 1;

		String[] toRet = new String[numFields];

		int pos = 0;
		for(; pos < splitted.length; pos++) {
			String str = removeSeparator || pos == numFields-1 ? splitted[pos] : splitted[pos] + separatorString;
			toRet[pos] = trimValues ? str.trim() : str;
		}
		for(; pos < numFields; pos++) {
			toRet[pos] = "";
		}

		return toRet;
	}

	public static List<String> splitFieldsList(String source, String separatorString) {
		return splitFieldsList(source, separatorString, false);
	}
	public static List<String> splitFieldsList(String source, String separatorString, boolean trimValues) {
		return splitFieldsList(source, separatorString, trimValues, true);
	}
	public static List<String> splitFieldsList(String source, String separatorString, boolean trimValues, boolean removeSeparator) {
		return JkConverter.toArrayList(splitAllFields(source, separatorString, trimValues, removeSeparator));
	}

	public static boolean matchRegExp(String regex, String source) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(source);
		return matcher.matches();
	}

	public static int indexOfIgnoreCase(String source, String toFind) {
		return source.toLowerCase().indexOf(toFind.toLowerCase());
	}

	public static String leftPadLines(String source, String padStr, int padSize) {
		String[] lines = splitAllFields(source, "\n");
		List<String> padded = leftPadLines(JkConverter.toArrayList(lines), padStr, padSize);
		return JkStreams.join(padded, "\n");
	}
	public static List<String> leftPadLines(List<String> list, String padStr, int padSize) {
		return list.stream().map(str -> StringUtils.repeat(padStr, padSize) + str).collect(Collectors.toList());
	}

	public static String mergeLines(String left, String right, String separator) {
		List<String> mergedLines = mergeLines(splitFieldsList(left, StringUtils.LF), splitFieldsList(right, StringUtils.LF), separator);
		return JkStreams.join(mergedLines, StringUtils.LF);
	}
	public static List<String> mergeLines(List<String> left, List<String> right, String separator) {
		List<String> merged = new ArrayList<>();
		for(int i = 0; i < Math.max(left.size(), right.size()); i++) {
			String l = i < left.size() ? left.get(i) : "";
			String r = i < right.size() ? right.get(i) : "";
			merged.add(l + separator + r);
		}
		return merged;
	}

	public static String safeTrim(String source) {
		return StringUtils.isBlank(source) ? "" : source.trim();
	}


    public static String unescapeHtmlSpecialChars(String htmlText) {
        // Custom replacements before undecoding using StringUtils
        htmlText = htmlText.replace("&#160;", "");  // Non-breaking space

        htmlText = StringEscapeUtils.unescapeHtml4(htmlText);
        return htmlText;
    }
}
