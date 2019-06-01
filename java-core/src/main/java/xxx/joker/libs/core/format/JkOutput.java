package xxx.joker.libs.core.format;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.enums.JkAlign;
import xxx.joker.libs.core.enums.JkSizeUnit;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by f.barbano on 26/05/2018.
 */

public class JkOutput {

	public static final String DEF_SEP = "|";
	public static final int DEF_DISTANCE = 2;

	public static String columnsView(String lines) {
		return columnsView(lines, DEF_SEP, DEF_DISTANCE, false);
	}
	public static String columnsView(String lines, boolean hasHeader) {
		return columnsView(lines, DEF_SEP, DEF_DISTANCE, hasHeader);
	}
	public static String columnsView(String lines, int colsDistance) {
		return columnsView(lines, DEF_SEP, colsDistance, false);
	}
	public static String columnsView(String lines, String fieldSep) {
		return columnsView(lines, fieldSep, DEF_DISTANCE, false);
	}
	public static String columnsView(String lines, String fieldSep, int colsDistance) {
		return columnsView(lines, fieldSep, colsDistance, false);
	}
	public static String columnsView(String lines, String fieldSep, int colsDistance, boolean hasHeader) {
		String colsFiller = StringUtils.repeat(' ', colsDistance);
		return columnsView(lines, fieldSep, colsFiller, hasHeader);
	}
	public static String columnsView(String lines, String fieldSep, String colsFiller) {
		return columnsView(lines, fieldSep, colsFiller, false);
	}
	public static String columnsView(String lines, String fieldSep, String colsFiller, boolean hasHeader) {
		return columnsView(JkStrings.splitList(lines, StringUtils.LF), fieldSep, colsFiller, hasHeader);
	}

	public static String columnsView(List<String> lines) {
		return columnsView(lines, DEF_SEP, DEF_DISTANCE, false);
	}
	public static String columnsView(List<String> lines, boolean hasHeader) {
		return columnsView(lines, DEF_SEP, DEF_DISTANCE, hasHeader);
	}
	public static String columnsView(List<String> lines, int colsDistance) {
		return columnsView(lines, DEF_SEP, colsDistance, false);
	}
	public static String columnsView(List<String> lines, String fieldSep) {
		return columnsView(lines, fieldSep, DEF_DISTANCE, false);
	}
	public static String columnsView(List<String> lines, String fieldSep, int colsDistance) {
		return columnsView(lines, fieldSep, colsDistance, false);
	}
	public static String columnsView(List<String> lines, String fieldSep, int colsDistance, boolean hasHeader) {
		String colsFiller = StringUtils.repeat(' ', colsDistance);
		return columnsView(lines, fieldSep, colsFiller, hasHeader);
	}
	public static String columnsView(List<String> lines, String fieldSep, String colsFiller) {
		return columnsView(lines, fieldSep, colsFiller, false);
	}
	public static String columnsView(List<String> lines, String fieldSep, String colsFiller, boolean hasHeader) {
		JkViewBuilder viewBuilder = new JkViewBuilder(lines);
		if(hasHeader) {
			viewBuilder.setHeaderAlign(JkAlign.CENTER);
		}
		viewBuilder.setDataAlign(JkAlign.LEFT);
		return viewBuilder.toString(fieldSep, colsFiller, false);
	}

	public static String humanSize(double bytes) {
		if (bytes >= JkSizeUnit.GB.size()) {
			return humanSize(bytes, JkSizeUnit.GB, false);
		} else if (bytes >= JkSizeUnit.MB.size()) {
			return humanSize(bytes, JkSizeUnit.MB, false);
		} else {
			return humanSize(bytes, JkSizeUnit.KB, false);
		}
	}
	public static String humanSize(double bytes, JkSizeUnit scale) {
		return humanSize(bytes, scale, false);
	}
	public static String humanSize(double bytes, JkSizeUnit scale, boolean roundInt) {
		double value = scale.parse(bytes);
		if(roundInt) {
			return String.format("%d %s", (int)value, scale.label());
		} else {
			return String.format("%s %s", getNumberFmtEN(2).format(value), scale.label());
		}
	}

	// Number formatters
	public static NumberFormat getNumberFmtEN(int numFractionDigits) {
		return getNumberFmtEN(numFractionDigits, numFractionDigits);
	}
	public static NumberFormat getNumberFmtEN(int minFractionDigits, int maxFractionDigits) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setMinimumFractionDigits(minFractionDigits);
		nf.setMaximumFractionDigits(maxFractionDigits);
		return nf;
	}

	// Format objects
	public static String formatColl(Collection<?> elems, String... fieldsToDisplay) {
		return JkStreams.join(formatCollLines(elems, fieldsToDisplay));
	}
	public static List<String> formatCollLines(Collection<?> elems, String... fieldsToDisplay) {
		try {
			List<String> fieldNames = getFieldNames(fieldsToDisplay);
			List<String> lines = new ArrayList<>();

			for (Object e : elems) {
				lines.add(formatObject1(e, fieldNames));
			}

			if(!fieldNames.isEmpty()) {
				String header = JkStreams.join(fieldNames, "|", JkOutput::createStringHeader);
				lines.add(0, header);
			}

			return lines;

		} catch (Exception ex) {
			throw new JkRuntimeException(ex);
		}
	}
	public static String formatObject(Object o, String... fieldsToDisplay) {
		return formatObject1(o, getFieldNames(fieldsToDisplay));
	}
	private static String formatObject1(Object o, Collection<String> fieldNames) {
		StringBuilder sb = new StringBuilder();

		if(fieldNames.isEmpty()) {
			fieldNames.addAll(JkStreams.map(JkReflection.findAllFields(o.getClass()), Field::getName));
		}

		for (String fname : fieldNames) {
			if (sb.length() > 0) sb.append("|");

			Object fval = JkReflection.getFieldValue(o, fname);
			if(fval == null) {
				sb.append("NULL");
			} else if(JkReflection.isInstanceOf(fval.getClass(), Collection.class)) {
				sb.append("#" + ((Collection)fval).size());
			} else if(JkReflection.isInstanceOf(fval.getClass(), JkFormattable.class)) {
				sb.append(((JkFormattable)fval).format());
			} else {
				sb.append(fval);
			}
		}
		return sb.toString();
	}

	private static String createStringHeader(String str) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				if(i > 0) {
					char o = str.charAt(i-1);
					if(o >= 'a' && o <= 'z') {
						sb.append(" ");
					}
				}
			}
			sb.append(c);
		}
		String res = sb.toString().replace("_", " ").replaceAll(" +", " ").trim();
		return res.toUpperCase();
	}
	private static List<String> getFieldNames(String... fieldNames) {
		List<String> toRet = new ArrayList<>();
		for (String fstr : fieldNames) {
			String trimmed = fstr.replaceAll(" +", " ").trim();
			List<String> tlist = JkStrings.splitList(trimmed, " ");
			toRet.addAll(tlist);
		}
		return toRet;
	}


}
