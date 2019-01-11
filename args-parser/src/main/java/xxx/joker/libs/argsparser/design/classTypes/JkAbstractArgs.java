package xxx.joker.libs.argsparser.design.classTypes;

import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.Arrays;
import java.util.function.UnaryOperator;


public abstract class JkAbstractArgs<T extends JkCommands> {
	
	private T selectedCommand;

	public T getSelectedCommand() {
		return selectedCommand;
	}

	public void setSelectedCommand(T selectedCommand) {
		this.selectedCommand = selectedCommand;
	}

	public abstract boolean isArgsIgnoreCase();

}
