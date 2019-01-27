package xxx.joker.libs.argsparser.design.classTypes;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public abstract class JkAbstractArgs<T extends JkCommands> {
	
	private T selectedCommand;

	public T getSelectedCommand() {
		return selectedCommand;
	}

	public void setSelectedCommand(T selectedCommand) {
		this.selectedCommand = selectedCommand;
	}

	@Override
	public String toString() {
		String str = ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
		List<String> split = JkStrings.splitList(str.replaceAll("^\\{", "").replaceAll("}$", ""), ",");
		split.removeIf(s -> s.endsWith("null"));
		split.removeIf(s -> s.endsWith("false"));
		return strf("{} -> {{}}", getClass().getSimpleName(), JkStreams.join(split, ","));
	}
}
