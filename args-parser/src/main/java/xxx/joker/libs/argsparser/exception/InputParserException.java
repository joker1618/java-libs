package xxx.joker.libs.argsparser.exception;

/**
 * Created by f.barbano on 27/08/2017.
 */
public class InputParserException extends Exception {

	public InputParserException(String message, Object... params) {
		super(String.format("PARSER ERROR: " + message, params));
	}
}
