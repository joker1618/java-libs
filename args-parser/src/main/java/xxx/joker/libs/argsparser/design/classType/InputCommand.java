package xxx.joker.libs.argsparser.design.classType;

import xxx.joker.libs.argsparser.model.CmdParam;

import java.util.List;

/**
 * Created by f.barbano on 26/08/2017.
 */
public interface InputCommand {

	String name();

	List<CmdParam> paramList();

}
