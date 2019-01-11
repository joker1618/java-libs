package xxx.joker.libs.argsparser.design.classTypes;

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
