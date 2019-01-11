package xxx.joker.libs.oldargsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.oldargsparser.common.Const;
import xxx.joker.libs.oldargsparser.design.annotation.Opt;
import xxx.joker.libs.oldargsparser.design.classType.InputOption;
import xxx.joker.libs.oldargsparser.design.classType.OptionName;
import xxx.joker.libs.oldargsparser.exception.DesignParserException;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkStreams;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by f.barbano on 03/09/2017.
 */

@ToAnalyze
@Deprecated
class OptServiceImpl implements IOptService {

	 private Class<? extends InputOption> optClass;
	 // key: option argName
	 private Map<String, OptWrapper> optMap;


	OptServiceImpl(Class<? extends InputOption> optClass, boolean performDesignCheck, IOptNameService optNameService) {
	 	this.optClass = optClass;
	 	this.optMap = new HashMap<>();
	 	init(performDesignCheck, optNameService);
	 }

	private void init(boolean performDesignCheck, IOptNameService optNameService) {
		List<Field> annFields = JkReflection.getFieldsByAnnotation(optClass, Opt.class);
		Set<String> allNameAlias = new HashSet<>();

		for(Field field : annFields) {
			Opt annot = field.getAnnotation(Opt.class);
			if(performDesignCheck) {
				checkNameAliasUniqueness(allNameAlias, annot);
				checkClassCoherence(field, annot);
				checkRelatedOptName(optNameService, annot);
			}
			OptionName optName = optNameService.getOptName(annot.name());
			OptWrapper optWrapper = new OptWrapper(field, optName);
			optMap.put(optWrapper.getName(), optWrapper);
		}
	}

	private void checkNameAliasUniqueness(Set<String> allNameAlias, Opt annot) {
		String annName = annot.name();
		List<String> annAliases = JkConvert.toArrayList(annot.aliases());

		// No spaces in argName and aliases
		if(StringUtils.isBlank(annName)) {
			throw new DesignParserException(optClass, "option argName \"%s\" is blank", annName);
		}
		if(annName.contains(" ")) {
			throw new DesignParserException(optClass, "option argName \"%s\" contains spaces", annName);
		}
		annAliases.forEach(alias -> {
			if(StringUtils.isBlank(alias)) {
				throw new DesignParserException(optClass, "option alias \"%s\" is blank", alias);
			}
			if(alias.contains(" ")) {
				throw new DesignParserException(optClass, "option alias \"%s\" contains spaces", alias);
			}
		});

		// argName and aliases must be unique
		if (!allNameAlias.add(annName)) {
			throw new DesignParserException(optClass, "option argName \"%s\" duplicated", annName);
		} else {
			annAliases.forEach(alias -> {
				if (!allNameAlias.add(alias)) {
					throw new DesignParserException(optClass, "option alias \"%s\" duplicated", alias);
				}
			});
		}
	}

	private void checkClassCoherence(Field field, Opt annot) {
		if (annot.classes().length == 0) {
			// no annotation classes -> class is the field type (must be supported)
			if (!Const.SUPPORTED_CLASSES.contains(field.getType())) {
				throw new DesignParserException(optClass,
							   "field \"%s\": type \"%s\" not allowed\nAllowed classes:\n%s",
							   field.getName(),
							   field.getType().getSimpleName(),
							   Const.toStringSupportedClasses()
				);
			}

		} else if (annot.classes().length == 1) {
		    throw new DesignParserException(optClass,
							   "field \"%s\": single annotation classes value not allowed",
							   field.getName()
			);

		} else {
			List<Class<?>> annotClasses = JkConvert.toArrayList(annot.classes());

			// annotation classes found --> field type must be Object
			if (field.getType() != Object.class) {
				throw new DesignParserException(optClass,
							   "annotation classes %s, but field \"%s\" is not an Object",
							   JkStreams.map(annotClasses, Class::getSimpleName),
							   field.getName()
				);
			}

			// annotation classes must be supported
			annotClasses.removeIf(Const.SUPPORTED_CLASSES::contains);
			if (!annotClasses.isEmpty()) {
				throw new DesignParserException(optClass,
							   "field \"%s\": annotation class %s not allowed\nAllowed classes:\n%s",
							   field.getName(),
							   JkStreams.map(annotClasses, Class::getSimpleName),
							   Const.toStringSupportedClasses()
				);
			}
		}
	}

	private void checkRelatedOptName(IOptNameService optNameService, Opt annot) {
		// check if exists an Enum<? extends OptionName> with the same option argName
		Set<String> optNames = optNameService.getOptNameMap().keySet();
		String annotName = annot.name();
		if(!optNames.contains(annotName)) {
			String optNameClass = optNameService.getOptNameClass().getSimpleName();
			throw new DesignParserException(optClass, "option argName [%s] not related with Enum<%s>", annotName, optNameClass);
		}
	}


	@Override
	public Class<? extends InputOption> getOptClass() {
		return optClass;
	}

	@Override
	public Map<String, OptWrapper> getOptions() {
		return optMap;
	}

	@Override
	public OptWrapper getOptionByNameOrAlias(String nameOrAlias) {
		List<OptWrapper> filter = JkStreams.filter(optMap.values(), ow -> ow.getName().equals(nameOrAlias) || ow.getAliases().contains(nameOrAlias));
		return !filter.isEmpty() ? filter.get(0) : null;
	}
}
