package xxx.joker.libs.oldargsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.oldargsparser.design.annotation.OptName;
import xxx.joker.libs.oldargsparser.design.classType.OptionName;
import xxx.joker.libs.oldargsparser.exception.DesignParserException;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkReflection;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */

@ToAnalyze
@Deprecated
class OptNameServiceImpl implements IOptNameService {

	private Class<? extends OptionName> optNameClass;
	// key: option argName, value: enum
	private Map<String, OptionName> optNameMap;


	OptNameServiceImpl(Class<? extends OptionName> optNameClass, boolean performDesignCheck) {
		this.optNameClass = optNameClass;
		this.optNameMap = new HashMap<>();
		init(performDesignCheck);
	}

	private void init(boolean performDesignCheck) {
		List<Field> fields = JkReflection.getFieldsByAnnotation(optNameClass, OptName.class);
		for(Field field : fields) {
			// check if field is an Enum
			if(performDesignCheck && !field.isEnumConstant()) {
				throw new DesignParserException(optNameClass, "field [%s] is not an Enum<? extends OptionName>", field.getName());
			}

			Enum<?> enumOptName = JkReflection.getEnumByName(optNameClass, field.getName());
			OptionName optName = (OptionName) enumOptName;

			if(performDesignCheck) {
				// check if option argName is empty
				if(StringUtils.isBlank(optName.optName())) {
					throw new DesignParserException(optNameClass, "option argName [%s] is blank", optName.optName());
				}
				// check if option argName contains spaces
				if(optName.optName().contains(" ")) {
					throw new DesignParserException(optNameClass, "option argName [%s] contains spaces", optName.optName());
				}
				// check if option argName is duplicated
				if(optNameMap.containsKey(optName.optName())) {
					throw new DesignParserException(optNameClass, "option argName [%s] duplicated", optName.optName());
				}
			}

			optNameMap.put(optName.optName(), optName);
		}
	}

	@Override
	public Class<?> getOptNameClass() {
		return optNameClass;
	}

	@Override
	public Map<String, OptionName> getOptNameMap() {
		return optNameMap;
	}

	@Override
	public OptionName getOptName(String optionName) {
		return optNameMap.get(optionName);
	}
}
