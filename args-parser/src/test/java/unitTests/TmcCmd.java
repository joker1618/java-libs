package unitTests;

import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.core.util.JkConvert;

import java.util.List;

import static unitTests.TmcArgType.*;

public enum TmcCmd implements JkCommands {

	@JkCmdElem
	CMD_1(
		new CParam(COption.of(B1)),
		new CParam(B2, COption.of(B2)),
		new CParam(B3, COption.of(B3), COption.of(B4))
	),

//	@JkCmdElem
//	CMD_2(
//		new CParam(COption.of(STR1)),
//		new CParam(B2, COption.of(B4))
//	),
//	@JkCmdElem
//	CMD_3(
//		new CParam(COption.of(STR1)),
//		new CParam(ALTRO, COption.of(ALTRO))
//	),

	;

	public static final String AUTO_VALUE = "_auto";

	private List<CParam> paramList;

	TmcCmd(CParam... params) {
		this.paramList = JkConvert.toList(params);
	}

	@Override
	public List<CParam> params() {
		return paramList;
	}

}
