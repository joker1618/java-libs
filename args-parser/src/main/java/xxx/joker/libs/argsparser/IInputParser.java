package xxx.joker.libs.argsparser;

import xxx.joker.libs.argsparser.exception.InputParserException;

/**
 * Created by f.barbano on 11/03/2017.
 */
public interface IInputParser {

	void parse(String[] inputArgs) throws InputParserException;
	
}
