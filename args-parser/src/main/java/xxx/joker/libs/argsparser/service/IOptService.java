package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classType.InputOption;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public interface IOptService {

	Class<? extends InputOption> getOptClass();

	Map<String, OptWrapper> getOptions();

	OptWrapper getOptionByNameOrAlias(String nameOrAlias);
}
