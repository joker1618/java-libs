package xxx.joker.libs.argsparser.design.classType;

import xxx.joker.libs.argsparser.design.annotation.Cmd;
import xxx.joker.libs.core.ToAnalyze;

/**
 * Created by f.barbano on 26/08/2017.
 */

@ToAnalyze
@Deprecated
public abstract class InputOption<T extends InputCommand> {

	@Cmd
	private T selectedCommand;

	public T getSelectedCommand() {
		return selectedCommand;
	}

}
