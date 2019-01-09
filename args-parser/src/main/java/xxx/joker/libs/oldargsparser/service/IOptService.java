package xxx.joker.libs.oldargsparser.service;

import xxx.joker.libs.oldargsparser.design.classType.InputOption;
import xxx.joker.libs.core.ToAnalyze;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */

@ToAnalyze
@Deprecated
public interface IOptService {

	Class<? extends InputOption> getOptClass();

	Map<String, OptWrapper> getOptions();

	OptWrapper getOptionByNameOrAlias(String nameOrAlias);
}
