package xxx.joker.libs.core.exception;

/**
 * Created by f.barbano on 19/11/2017.
 */
public class JkRuntimeException extends RuntimeException {

	public JkRuntimeException(String message, Object... params) {
		super(String.format(message, params));
	}

	public JkRuntimeException(Throwable cause, String message, Object... params) {
		super(String.format(message, params), cause);
	}

	public JkRuntimeException(Throwable cause) {
		super(cause);
	}
}
