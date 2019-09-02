package unitTests;

import xxx.joker.libs.argsparser.design.annotations.JkArgType;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;

public enum TmcArgType implements JkArgsTypes {

	@JkArgType
	B1("b1"),
	@JkArgType
	B2("b2"),
	@JkArgType
	B3("bool3"),
	@JkArgType
	B4("bool4"),
	@JkArgType
	ALTRO("altro"),
	@JkArgType
	STR1("str1"),

	;

	private String argName;

	TmcArgType(String argName) {
		this.argName = argName;
	}

	@Override
	public String getArgName() {
		return argName;
	}
}
