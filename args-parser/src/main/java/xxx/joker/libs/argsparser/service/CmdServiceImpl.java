package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.common.Const;
import xxx.joker.libs.argsparser.design.annotation.Cmd;
import xxx.joker.libs.argsparser.design.classType.InputCommand;
import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.argsparser.exception.DesignParserException;
import xxx.joker.libs.argsparser.model.CmdOption;
import xxx.joker.libs.argsparser.model.CmdParam;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkStreams;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by f.barbano on 03/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
class CmdServiceImpl implements ICmdService {

	private Class<? extends InputCommand> cmdClass;
	// value: evolutions
	private Map<CmdWrapper, List<String>> cmdMap;

	CmdServiceImpl(Class<? extends InputCommand> cmdClass, boolean checkDesign, IOptService optionService, IOptNameService optNameService) {
		this.cmdClass = cmdClass;
		this.cmdMap = new HashMap<>();
		init(checkDesign, optionService, optNameService);
	}

	private void init(boolean checkDesign, IOptService optionService, IOptNameService optNameService) {
		List<Field> fields = JkReflection.getFieldsByAnnotation(cmdClass, Cmd.class);
		if (checkDesign && fields.isEmpty()) {
			throw new DesignParserException(cmdClass, "no fields annotated with @Cmd");
		}

		for(Field field : fields) {
			if (checkDesign && !field.isEnumConstant()) {
				throw new DesignParserException(cmdClass, "field [%s] is not an Enum<? extends InputCommand>", field.getName());
			}

			Enum<?> enumCmd = JkReflection.getEnumByName(cmdClass, field.getName());

			CmdWrapper cmd = new CmdWrapper((InputCommand)enumCmd);

			if(checkDesign) {
				checkParamListSize(cmd);
				checkOptionsUniqueness(cmd);
				checkParamDependency(cmd);
			}

			setAndCheckOptionClass(cmd, optionService, optNameService, checkDesign);

			List<String> evolutions = computeCmdEvolutions(cmd, optNameService.getOptNameMap().size());
			if(checkDesign) {
				checkEvolutions(cmd.getName(), evolutions);
			}

			cmdMap.put(cmd, evolutions);
		}
	}

	private void checkParamListSize(CmdWrapper cmd) {
		List<CmdParam> params = cmd.getParamList();

		// must exists at least one cmdParam
		if(params.isEmpty()) {
			throw new DesignParserException(cmdClass, "command %s: no cmdParam", cmd.getName());
		}

		// all cmdParam must be at least one cmdOption
		params.forEach(cp -> {
			if(cp.getOptionList().isEmpty()) {
				throw new DesignParserException(cmdClass, "command %s: no cmdOption found for one cmdParam", cmd.getName());
			}
		});
	}

	private void checkOptionsUniqueness(CmdWrapper cmd) {
		List<String> optNameList = JkStreams.map(cmd.getOptionList(), CmdOption::getOptionName);
		List<String> dups = JkStreams.duplicates(optNameList);
		if (!dups.isEmpty()) {
			throw new DesignParserException(cmdClass, "command %s: options %s duplicated", cmd.getName(), dups);
		}
	}

	private void checkParamDependency(CmdWrapper cmd) {
		List<CmdParam> params = cmd.getParamList();

		for(int i = 0; i < params.size(); i++) {
			CmdParam cp1 = params.get(i);
			Enum<?> dependOn = cp1.getDependOn();
			if(dependOn != null) {
				boolean found = false;
				for(int j = 0; j < params.size() && !found; j++) {
					CmdParam cp2 = params.get(j);
					int count = JkStreams.filter(cp2.getOptionList(), co -> co.getOption() == dependOn).size();
					if(count > 0) {
						if(i == j) {
							throw new DesignParserException(cmdClass, "command %s: param %d dependency must specify an option of another param", cmd.getName(), i);
						}
						found = true;
					}
				}
				if(!found) {
					throw new DesignParserException(cmdClass, "command %s: param %d dependency not found", dependOn, cmd.getName(), i);
				}
			}
		}
	}

	private void setAndCheckOptionClass(CmdWrapper cmd, IOptService optionService, IOptNameService optNameService, boolean checkDesign) {
		String cmdName = cmd.getName();
		List<CmdOption> options = cmd.getOptionList();

		for(int i = 0; i < options.size(); i++) {
			CmdOption cmdOption = options.get(i);
			OptWrapper optWrapper = optionService.getOptions().get(cmdOption.getOptionName());
			OptionName optName = optNameService.getOptName(optWrapper.getName());

			Class<?> optType = optWrapper.getField().getType();
			Class<?> cmdOptionClass = cmdOption.getOptionClass();

			if(cmdOptionClass == null) {
				// class not specified: option must have empty 'classes' (so field type class != Object.class)
				// option class will be option field type
				if(checkDesign && optType == Object.class) {
					throw new DesignParserException(cmdClass, "command %s, option [%s]: class not selected", cmdName, optName);
				}
				cmdOption.setOptionClass(optType);

			} else if(checkDesign) {
				// class specified: must be included in option 'classes' (so field type class == Object.class)
				if(optType != Object.class) {
					throw new DesignParserException(cmdClass, "command %s, option [%s]: class cannot be selected", cmdName, optName);
				}
				if(!optWrapper.getClasses().contains(cmdOptionClass)) {
					String selected = cmdOptionClass.getSimpleName();
					List<String> allowed = JkStreams.map(optWrapper.getClasses(), Class::getSimpleName);
					throw new DesignParserException(cmdClass, "command %s, option [%s]: selected class [%s], allowed classes %s", cmdName, optName, selected, allowed);
				}
			}

			// Boolean options cannot be 'valueTransform' or 'valueCheckers' set, because has no input values
			if(checkDesign && cmdOptionClass == Boolean.class) {
				if(cmdOption.getTransformBefore() != null) {
					throw new DesignParserException(cmdClass, "command %s, option [%s]: 'beforeTransform' function not allowed for Boolean fields", cmdName, optName);
				}
				if(cmdOption.getTransformAfter() != null) {
					throw new DesignParserException(cmdClass, "command %s, option [%s]: 'afterTransform' function not allowed for Boolean fields", cmdName, optName);
				}
				if(!cmdOption.getValueCheckers().isEmpty()) {
					throw new DesignParserException(cmdClass, "command %s, option [%s]: 'valueCheckers' functions not allowed for Boolean fields", cmdName, optName);
				}
			}
		}
	}

	private List<String> computeCmdEvolutions(CmdWrapper cmd, int numOptions) {
		boolean onlyRequired = cmd.countIndependentEvolutions() > Const.MAX_INDEPENDENT_EVOLUTIONS;
		List<CmdParam> params = onlyRequired ? JkStreams.filter(cmd.getParamList(), CmdParam::isRequired) : cmd.getParamList();

		List<char[]> evolutions = new ArrayList<>();
		char[] noParamEvol = new char[numOptions];
		for(int i = 0; i < noParamEvol.length; i++)		noParamEvol[i] = '0';
		evolutions.add(noParamEvol);

		Set<Enum<?>> usedOptions = new HashSet<>();

		// Analyze first all params without option dependency
		List<CmdParam> noDeps = JkStreams.filter(params, cpar -> cpar.getDependOn() == null);
		for(CmdParam cp : noDeps) {
			List<char[]> tot = new ArrayList<>();
			for (CmdOption co : cp.getOptionList()) {
				evolutions.forEach(arr -> tot.add(setUpChar(arr, co)));
				usedOptions.add(co.getOption());
			}

			// if param is optional, previous evolutions must be holds
			if (!cp.isRequired()) {
				tot.addAll(evolutions);
			}

			evolutions = tot;
		}

		if(!onlyRequired) {
			// Now analyze params with option dependency
			List<CmdParam> withDeps = JkStreams.filter(params, cpar -> cpar.getDependOn() != null);
			while (!withDeps.isEmpty()) {
				// a param can depends on another param that in turn has an option dependency
				// to avoid problem in computing evolution, a param is analyzed only if his dependency is already analyzed
				int idx = -1;
				for (int i = 0; i < withDeps.size() && idx == -1; i++) {
					if (usedOptions.contains(withDeps.get(i).getDependOn())) {
						idx = i;
					}
				}

				if (idx == -1) {
					throw new DesignParserException(cmdClass, "command %s: wrong option dependencies (%s not analyzed)", cmd.getName(), JkStreams.map(withDeps, CmdParam::getDependOn));
				}

				CmdParam removed = withDeps.remove(idx);
				int depIdx = removed.getDependOn().ordinal();
				List<char[]> sourceEvols = JkStreams.filter(evolutions, arr -> arr[depIdx] == '1');
				List<char[]> tot = new ArrayList<>();
				for (CmdOption co : removed.getOptionList()) {
					sourceEvols.forEach(arr -> tot.add(setUpChar(arr, co)));
					usedOptions.add(co.getOption());
				}

				if (!removed.isRequired()) {
					tot.addAll(sourceEvols);
				}

				evolutions.removeIf(arr -> arr[depIdx] == '1');
				evolutions.addAll(tot);
			}
		}

		return JkStreams.map(evolutions, String::new);
	}

	private void checkEvolutions(String cmdName, List<String> evolutions) {
		Set<CmdWrapper> keys = new HashSet<>();
		evolutions.forEach(evol -> keys.addAll(JkStreams.getMapKeys(cmdMap, list -> list.contains(evol))));
		if(!keys.isEmpty()) {
			List<String> keysName = JkStreams.map(keys, CmdWrapper::getName);
			throw new DesignParserException(cmdClass, "command %s: evolutions overlaps with commands %s", cmdName, keysName);
		}
	}

	private char[] setUpChar(char[] arr, CmdOption cmdOption) {
		int index = cmdOption.getOption().ordinal();
		char[] evol = Arrays.copyOf(arr, arr.length);
		evol[index] = '1';
		return evol;
	}

	@Override
	public CmdWrapper getByEvolution(String evolution) {
		List<CmdWrapper> mapKeys = JkStreams.getMapKeys(cmdMap, list -> list.contains(evolution));

		for(Map.Entry<CmdWrapper, List<String>> entry : cmdMap.entrySet()) {
			if(entry.getKey().countIndependentEvolutions() > Const.MAX_INDEPENDENT_EVOLUTIONS) {
				if(containsEvolutionRequired(entry.getValue(), evolution)) {
					return entry.getKey();
				}
			} else if(entry.getValue().contains(evolution)) {
				return entry.getKey();
			}
		}

		return mapKeys.isEmpty() ? null : mapKeys.get(0);
	}

	private boolean containsEvolutionRequired(List<String> list, String evolution) {
		for(String str : list) {
			boolean valid = true;
			for(int i = 0; valid && i < str.length(); i++) {
				char charStr = str.charAt(i);
				char charEv = evolution.charAt(i);
				if(charStr == '1' && charEv == '0') {
					valid = false;
				}
			}
			if(valid) return true;
		}
		return false;
	}

}
