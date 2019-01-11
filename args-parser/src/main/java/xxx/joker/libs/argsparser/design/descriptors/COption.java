package xxx.joker.libs.argsparser.design.descriptors;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkConvert;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Created by f.barbano on 26/08/2017.
 */

public class COption {

	private Enum<? extends JkArgsTypes> argType;
	private String defaultValue;
	private Class<?> optionClass;
	private UnaryOperator<String[]> transformBefore;  // value transformation before parse to correct class
	private UnaryOperator<Object[]> transformAfter;  // value transformation after parse to correct class
	private List<Function<Object[], String>> valueCheckers;
	private DateTimeFormatter dateTimeFormatter;

	public COption(Enum<? extends JkArgsTypes> argType, Function<Object[], String>... valueCheckers) {
		this(argType, null, null, null, null, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType, String defaultValue, Function<Object[], String>... valueCheckers) {
		this(argType, defaultValue, null, null, null, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType, Class<?> optionClass, Function<Object[], String>... valueCheckers) {
		this(argType, optionClass, null, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType, UnaryOperator<String[]> transformBefore, Function<Object[], String>... valueCheckers) {
		this(argType, transformBefore, null, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType, UnaryOperator<String[]> transformBefore, UnaryOperator<Object[]> transformAfter, Function<Object[], String>... valueCheckers) {
		this(argType, null, null, transformBefore, transformAfter, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType, Class<?> optionClass, UnaryOperator<String[]> transformBefore, Function<Object[], String>... valueCheckers) {
		this(argType, null, optionClass, transformBefore, null, valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType,
				   Class<?> optionClass,
				   UnaryOperator<String[]> transformBefore,
				   UnaryOperator<Object[]> transformAfter,
				   Function<Object[], String>... valueCheckers) {
		this.argType = argType;
		this.optionClass = optionClass;
		this.transformBefore = transformBefore;
		this.transformAfter = transformAfter;
		this.valueCheckers = JkConvert.toArrayList(valueCheckers);
	}
	public COption(Enum<? extends JkArgsTypes> argType,
				   String defaultValue,
				   Class<?> optionClass,
				   UnaryOperator<String[]> transformBefore,
				   UnaryOperator<Object[]> transformAfter,
				   Function<Object[], String>... valueCheckers) {
		this.argType = argType;
		this.defaultValue = defaultValue;
		this.optionClass = optionClass;
		this.transformBefore = transformBefore;
		this.transformAfter = transformAfter;
		this.valueCheckers = JkConvert.toArrayList(valueCheckers);
	}

	public Enum<? extends JkArgsTypes> getArgType() {
		return argType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getArgName() {
		return ((JkArgsTypes) argType).getArgName();
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

	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
}
