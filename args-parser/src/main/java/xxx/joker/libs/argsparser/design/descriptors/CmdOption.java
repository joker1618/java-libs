package xxx.joker.libs.argsparser.design.descriptors;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkConvert;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Created by f.barbano on 26/08/2017.
 */

@ToAnalyze
@Deprecated
public class CmdOption {

	private Enum<? extends JkArgsNames> option;
	private String defaultValue;
	private Class<?> optionClass;
	private UnaryOperator<String[]> transformBefore;  // value transformation before parse to correct class
	private UnaryOperator<Object[]> transformAfter;  // value transformation after parse to correct class
	private List<Function<Object[], String>> valueCheckers;

	public CmdOption(Enum<? extends JkArgsNames> option, Function<Object[], String>... valueCheckers) {
		this(option, null, null, null, null, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option, String defaultValue, Function<Object[], String>... valueCheckers) {
		this(option, defaultValue, null, null, null, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option, Class<?> optionClass, Function<Object[], String>... valueCheckers) {
		this(option, optionClass, null, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option, UnaryOperator<String[]> transformBefore, Function<Object[], String>... valueCheckers) {
		this(option, transformBefore, null, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option, UnaryOperator<String[]> transformBefore, UnaryOperator<Object[]> transformAfter, Function<Object[], String>... valueCheckers) {
		this(option, null, null, transformBefore, transformAfter, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option, Class<?> optionClass, UnaryOperator<String[]> transformBefore, Function<Object[], String>... valueCheckers) {
		this(option, null, optionClass, transformBefore, null, valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option,
					 Class<?> optionClass,
					 UnaryOperator<String[]> transformBefore,
					 UnaryOperator<Object[]> transformAfter,
					 Function<Object[], String>... valueCheckers) {
		this.option = option;
		this.optionClass = optionClass;
		this.transformBefore = transformBefore;
		this.transformAfter = transformAfter;
		this.valueCheckers = JkConvert.toArrayList(valueCheckers);
	}
	public CmdOption(Enum<? extends JkArgsNames> option,
					 String defaultValue,
					 Class<?> optionClass,
					 UnaryOperator<String[]> transformBefore,
					 UnaryOperator<Object[]> transformAfter,
					 Function<Object[], String>... valueCheckers) {
		this.option = option;
		this.defaultValue = defaultValue;
		this.optionClass = optionClass;
		this.transformBefore = transformBefore;
		this.transformAfter = transformAfter;
		this.valueCheckers = JkConvert.toArrayList(valueCheckers);
	}

	public Enum<? extends JkArgsNames> getOption() {
		return option;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getJkArgsNames() {
		return ((JkArgsNames)option).argName();
	}

	public Class<?> getOptionClass() {
		return optionClass;
	}

	public void setOptionClass(Class<?> optionClass) {
		this.optionClass = optionClass;
	}

	public UnaryOperator<String[]> getTransformBefore() {
		return transformBefore;
	}

	public UnaryOperator<Object[]> getTransformAfter() {
		return transformAfter;
	}

	public List<Function<Object[], String>> getValueCheckers() {
		return valueCheckers;
	}
}
