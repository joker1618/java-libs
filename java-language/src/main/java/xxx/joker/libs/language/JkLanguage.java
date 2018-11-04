package xxx.joker.libs.language;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by f.barbano on 19/01/2018.
 */
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
		if(StringUtils.isNotBlank(label)) {
			for (JkLanguage lan : values()) {
				if (label.equalsIgnoreCase(lan.label)) {
					return lan;
				}
			}
		}
		return null;
	}


}
