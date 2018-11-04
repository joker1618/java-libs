package xxx.joker.libs.argsparser.service;

import java.util.Map;

/**
 * Created by f.barbano on 03/09/2017.
 */
public interface IOptService {

	Class<?> getOptClass();

	Map<String, OptWrapper> getOptions();

	OptWrapper getOptionByNameOrAlias(String nameOrAlias);
}
