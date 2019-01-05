package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classType.OptionName;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public interface IOptNameService {

	Class<?> getOptNameClass();

	Map<String, OptionName> getOptNameMap();

	OptionName getOptName(String optionName);
}
