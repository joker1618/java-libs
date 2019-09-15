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
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}

	public String strInfo() {
		String str = ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
		List<String> split = JkStrings.splitList(str.replaceAll("^\\{", "").replaceAll("}$", ""), ",");
		split.removeIf(s -> s.endsWith("null"));
		split.removeIf(s -> s.endsWith("false"));
		split = JkStrings.leftPadLines(split, " ", 3);
		return strf("{}\n{}", getClass().getSimpleName(), JkStreams.joinLines(split));
	}
}
