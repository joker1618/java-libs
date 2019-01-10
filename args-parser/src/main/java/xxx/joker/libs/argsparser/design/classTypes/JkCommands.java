package xxx.joker.libs.argsparser.design.classTypes;

import xxx.joker.libs.argsparser.design.descriptors.CmdParam;
import xxx.joker.libs.core.ToAnalyze;

import java.util.List;

public interface JkCommands {

	String name();

	List<CmdParam> paramList();

}
