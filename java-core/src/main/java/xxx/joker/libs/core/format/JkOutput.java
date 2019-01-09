package xxx.joker.libs.core.format;

import xxx.joker.libs.core.enums.JkSizeUnit;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by f.barbano on 26/05/2018.
 */

public class JkOutput {

	public static String humanSize(double bytes) {
		if (bytes >= JkSizeUnit.GB.size()) {
			return humanSize(bytes, JkSizeUnit.GB, false);
		} else if (bytes >= JkSizeUnit.MB.size()) {
			return humanSize(bytes, JkSizeUnit.MB, false);
		} else {
			return humanSize(bytes, JkSizeUnit.KB, false);
		}
	}
	public static String humanSize(double bytes, JkSizeUnit scale, boolean roundInt) {
		double value = scale.parse(bytes);
		if(roundInt) {
			return String.format("%d %s", (int)value, scale.label());
		} else {
			return String.format("%s %s", getNumberFmtEN(2).format(value), scale.label());
		}
	}


	// Number formatter
	private static NumberFormat getNumberFmtEN(int numFractionDigits) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setMinimumFractionDigits(numFractionDigits);
		nf.setMaximumFractionDigits(numFractionDigits);
		return nf;
	}

	// TODO review
//	public static NumberFormat getNumberFmtIT(int numFractionDigits) {
//		return getNumberFmtIT(numFractionDigits, numFractionDigits);
//	}
//	public static NumberFormat getNumberFmtIT(int minFractionDigits, int maxFractionDigits) {
//		return getNumberFormat(Locale.ITALIAN, minFractionDigits, maxFractionDigits);
//	}
//	public static NumberFormat getNumberFmtEN(int numFractionDigits) {
//		return getNumberFmtEN(numFractionDigits, numFractionDigits);
//	}
//	public static NumberFormat getNumberFmtEN(int minFractionDigits, int maxFractionDigits) {
//		return getNumberFormat(Locale.ENGLISH, minFractionDigits, maxFractionDigits);
//	}
//	private static NumberFormat getNumberFormat(Locale locale, int minFractionDigits, int maxFractionDigits) {
//		NumberFormat nf = NumberFormat.getNumberInstance(locale);
//		nf.setMinimumFractionDigits(minFractionDigits);
//		nf.setMaximumFractionDigits(maxFractionDigits);
//		return nf;
//	}
	
}
