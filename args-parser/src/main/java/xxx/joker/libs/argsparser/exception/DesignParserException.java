package xxx.joker.libs.argsparser.exception;

/**
 * Created by f.barbano on 27/08/2017.
 */
public class DesignParserException extends RuntimeException {

	public DesignParserException(Class<?> clazz, String message, Object... params) {
		super(String.format("DESIGN ERROR (%s.class) - %s", clazz.getSimpleName(), String.format(message, params)));
	}
}