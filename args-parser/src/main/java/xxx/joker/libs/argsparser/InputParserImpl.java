package xxx.joker.libs.argsparser;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.design.annotation.Cmd;
import xxx.joker.libs.argsparser.design.classType.InputCommand;
import xxx.joker.libs.argsparser.design.classType.InputOption;
import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.argsparser.exception.InputParserException;
import xxx.joker.libs.argsparser.exception.InputValueException;
import xxx.joker.libs.argsparser.model.CmdOption;
import xxx.joker.libs.argsparser.service.*;
import xxx.joker.libs.core.utils.JkConverter;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkTests;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Created by f.barbano on 27/08/2017.
 */
public class InputParserImpl implements IInputParser {


	private InputOption inputOption;

	private IOptNameService optNameService;
	private IOptService optService;
	private ICmdService cmdService;


	public InputParserImpl(InputOption inputOption,
						   Class<? extends OptionName> optNameClass,
						   Class<? extends InputCommand> inputCmdClass,
						   Path launcherJarPath) {

		this.inputOption = inputOption;

		DesignServices.init(optNameClass, inputOption.getClass(), inputCmdClass, launcherJarPath);
		optNameService = DesignServices.getOptNameService();
		optService = DesignServices.getOptService();
		cmdService = DesignServices.getCmdService();
	}

	@Override
	public void parse(String[] inputArgs) throws InputParserException {
		Map<OptWrapper, List<String>> inputOptMap = inputArgsToMap(inputArgs);

		// find associated command using evolution
		String evolution = computeEvolution(inputOptMap.keySet());
		CmdWrapper selectedCmd = cmdService.getByEvolution(evolution);
		if(selectedCmd == null) {
			throw new InputParserException("Wrong input command");
		}

		// set field 'selectedCommand' in InputOption instance
		Field fieldSelCmd = JkReflection.getFieldsByAnnotation(InputOption.class, Cmd.class).get(0);
		setFieldValue(fieldSelCmd, selectedCmd.getCmdEnum());

		// check input options using 'selectedCmd'
		for(Map.Entry<OptWrapper, List<String>> arg : inputOptMap.entrySet()) {
			parseInputArg(arg.getKey(), arg.getValue(), selectedCmd);
		}
	}

	private Map<OptWrapper, List<String>> inputArgsToMap(String[] inputArgs) throws InputParserException {
		Map<OptWrapper, List<String>> map = new HashMap<>();

		if(inputArgs.length > 0) {
			OptWrapper optWrapper = null;
			List<String> values = null;

			for (int i = 0; i < inputArgs.length; i++) {
				String elem = inputArgs[i];
				OptWrapper opt = optService.getOptionByNameOrAlias(elem);
				if (opt == null) {
					if (i == 0) {
						throw new InputParserException("First arg is not an option");
					}
					values.add(elem);
				} else {
					if (i > 0) {
						if (map.containsKey(opt)) {
							throw new InputParserException("Option [%s] duplicated", elem);
						}
						map.put(optWrapper, values);
					}
					optWrapper = opt;
					values = new ArrayList<>();
				}
			}

			map.put(optWrapper, values);
		}

		return map;
	}

	private String computeEvolution(Set<OptWrapper> options) {
		String base = StringUtils.repeat('0', optNameService.getOptNameMap().size());
		StringBuilder sb = new StringBuilder(base);
		options.forEach(ow -> sb.setCharAt(ow.getOptName().ordinal(), '1'));
		return sb.toString();
	}

	private void setFieldValue(Field field, Object value) throws InputParserException {
		try {
			if(field.isAccessible()) {
				field.set(inputOption, value);
			} else {
				field.setAccessible(true);
				field.set(inputOption, value);
				field.setAccessible(false);
			}
		} catch (IllegalAccessException e) {
			throw new InputParserException("Error while set value (%s) for field (%s)", value, field.getName());
		}
	}

	private void parseInputArg(OptWrapper optWrapper, List<String> values, CmdWrapper selectedCmd) throws InputParserException {
		CmdOption cmdOption = selectedCmd.getOption(optWrapper.getOptName());
		String cmdName = selectedCmd.getName();

		Class<?> expectedClass = cmdOption.getOptionClass();

		String errorStart = String.format("Cmd [%s], option [%s]", cmdName, optWrapper.getName());

		// check values cardinality
		if(expectedClass.isArray()) {
			if(values.isEmpty()) {
				throw new InputValueException("%s: no input values found (expected class is %s.class)", errorStart, expectedClass.getSimpleName());
			}
		} else {
			if(expectedClass == Boolean.class) {
				if(!values.isEmpty()) {
					throw new InputValueException("%s: %d values found, but Boolean expected (no values)", errorStart, values.size());
				}
			} else {
				if(values.isEmpty() && cmdOption.getDefaultValue() != null) {
					values.add(cmdOption.getDefaultValue());
				} else if(values.size() != 1) {
					throw new InputValueException("%s: %d values found, but 1 expected", errorStart, values.size());
				}
			}
		}

		Object fieldValue;
		if(expectedClass == Boolean.class) {
			fieldValue = Boolean.TRUE;

		} else {
			Predicate<String[]> classCheck;
			Function<String[], Object[]> classConverter;

			if(expectedClass == Boolean[].class) {
				classCheck = JkTests::isBooleanArray;
				classConverter = JkConverter::stringToBoolean;
			} else if(expectedClass == Integer.class || expectedClass == Integer[].class) {
				classCheck = JkTests::isIntegerArray;
				classConverter = JkConverter::stringToInteger;
			} else if(expectedClass == Double.class || expectedClass == Double[].class) {
				classCheck = JkTests::isDoubleArray;
				classConverter = JkConverter::stringToDouble;
			} else if(expectedClass == Long.class || expectedClass == Long[].class) {
				classCheck = JkTests::isLongArray;
				classConverter = JkConverter::stringToLong;
			} else if(expectedClass == Path.class || expectedClass == Path[].class) {
				classCheck = arr -> true;
				classConverter = JkConverter::stringToPath;
			} else if(expectedClass == String.class || expectedClass == String[].class) {
				classCheck = arr -> true;
				classConverter = arr -> arr;
			} else {
				throw new InputParserException("%s: wrong expected class %s.class", expectedClass.getSimpleName());
			}

			fieldValue = parseInputValues(cmdOption, errorStart, values, classCheck, classConverter, expectedClass.isArray());
		}

		setFieldValue(optWrapper.getField(), fieldValue);
	}

	private Object parseInputValues(CmdOption cmdOption, String errorStart, List<String> values, Predicate<String[]> classCheck, Function<String[], Object[]> classConverter, boolean isArray) {
		String[] arrValues = values.toArray(new String[0]);

		UnaryOperator<String[]> transform = cmdOption.getTransformBefore();
		String[] sourceArr = transform == null ? arrValues : transform.apply(arrValues);

		if(!classCheck.test(sourceArr)) {
			throw new InputValueException("%s: wrong values type", errorStart);
		}

		Object[] objArr = classConverter.apply(sourceArr);
		if(cmdOption.getTransformAfter() != null) {
			objArr = cmdOption.getTransformAfter().apply(objArr);
		}

		for(Function<Object[], String> checker : cmdOption.getValueCheckers()) {
			String errorMex = checker.apply(objArr);
			if(StringUtils.isNotBlank(errorMex)) {
				throw new InputValueException("%s: %s", errorStart, errorMex);
			}
		}

		return isArray ? objArr : objArr[0];
	}

}
