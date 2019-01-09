package xxx.joker.libs.oldargsparser.design.classType;

import xxx.joker.libs.oldargsparser.model.CmdParam;
import xxx.joker.libs.core.ToAnalyze;

import java.util.List;

/**
 * Created by f.barbano on 26/08/2017.
 */

@ToAnalyze
@Deprecated
public interface InputCommand {

	String name();

	List<CmdParam> paramList();

}
