package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.annotation.Opt;
import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.core.utils.JkConverter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by f.barbano on 03/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class OptWrapper {

	private Field field;
	private String name;
	private OptionName optName;
	private List<String> aliases;
	private List<Class<?>> classes;

	protected OptWrapper(Field field, OptionName optName) {
		this.field = field;
		this.optName = optName;

		Opt ann = field.getAnnotation(Opt.class);
		this.name = ann.name();
		this.aliases = JkConverter.toArrayList(ann.aliases());
		this.classes = JkConverter.toArrayList(ann.classes());
	}

	public Field getField() {
		return field;
	}

	public String getName() {
		return name;
	}

	public OptionName getOptName() {
		return optName;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public List<Class<?>> getClasses() {
		return classes;
	}
}
