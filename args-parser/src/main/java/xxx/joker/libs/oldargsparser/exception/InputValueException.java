package xxx.joker.libs.oldargsparser.exception;

/**
 * Created by f.barbano on 30/08/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class InputValueException extends RuntimeException {

	public InputValueException(String message, Object... params) {
		super(String.format("WRONG INPUT VALUE --> " + message, params));
	}
}
