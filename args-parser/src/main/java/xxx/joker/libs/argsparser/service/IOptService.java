package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classType.InputOption;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */
public interface IOptService {

	Class<? extends InputOption> getOptClass();

	Map<String, OptWrapper> getOptions();

	OptWrapper getOptionByNameOrAlias(String nameOrAlias);
}
