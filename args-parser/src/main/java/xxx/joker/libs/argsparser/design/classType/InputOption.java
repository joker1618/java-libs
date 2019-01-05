package xxx.joker.libs.argsparser.design.classType;

import xxx.joker.libs.argsparser.design.annotation.Cmd;

/**
 * Created by f.barbano on 26/08/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public abstract class InputOption<T extends InputCommand> {

	@Cmd
	private T selectedCommand;

	public T getSelectedCommand() {
		return selectedCommand;
	}

}
