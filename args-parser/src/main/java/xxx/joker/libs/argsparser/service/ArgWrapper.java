package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkConvert;

import java.lang.reflect.Field;
import java.util.List;

public class ArgWrapper {

	private Field field;
	private String argName;
	private JkArgsTypes argType;
	private List<String> aliases;
	private List<Class<?>> classes;

	protected ArgWrapper(Field field, JkArgsTypes argType) {
		this.field = field;
		this.argType = argType;

		JkArg ann = field.getAnnotation(JkArg.class);
		this.argName = ann.argName();
		this.aliases = JkConvert.toArrayList(ann.aliases());
		this.classes = JkConvert.toArrayList(ann.classes());
	}

	public Field getField() {
		return field;
	}
	public String getArgName() {
		return argName;
	}
	public JkArgsTypes getArgType() {
		return argType;
	}
	public List<String> getAliases() {
		return aliases;
	}
	public List<Class<?>> getClasses() {
		return classes;
	}
}
