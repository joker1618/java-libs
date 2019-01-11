package xxx.joker.libs.argsparser.design.classTypes;

import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.Arrays;
import java.util.function.UnaryOperator;


public abstract class JkAbstractArgs<T extends JkCommands> {

	@JkCmdElem
	private T selectedCommand;

	public T getSelectedCommand() {
		return selectedCommand;
	}

	public abstract boolean isArgsIgnoreCase();
	
}
