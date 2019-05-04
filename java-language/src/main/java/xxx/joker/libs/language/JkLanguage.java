package xxx.joker.libs.language;

import xxx.joker.libs.core.ToAnalyze;

/**
 * Created by f.barbano on 19/01/2018.
 */

@ToAnalyze
@Deprecated
public enum JkLanguage {

	ITALIAN("ita"),
	ENGLISH("eng"),
	GERMAN("ger"),
	FRENCH("fre"),
	SPANISH("spa"),


	;

	private String label;

	JkLanguage(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public static JkLanguage getByLabel(String label) {
		for (JkLanguage lan : values()) {
			if (lan.getLabel().equalsIgnoreCase(label)) {
				return lan;
			}
		}
		return null;
	}


}
