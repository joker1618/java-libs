package xxx.joker.libs.oldargsparser.service;

import xxx.joker.libs.oldargsparser.design.classType.OptionName;
import xxx.joker.libs.core.ToAnalyze;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */

@ToAnalyze
@Deprecated
public interface IOptNameService {

	Class<?> getOptNameClass();

	Map<String, OptionName> getOptNameMap();

	OptionName getOptName(String optionName);
}