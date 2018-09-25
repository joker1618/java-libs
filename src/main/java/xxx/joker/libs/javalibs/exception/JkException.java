package xxx.joker.libs.javalibs.exception;

/**
 * Created by f.barbano on 19/11/2017.
 */
public class JkException extends Exception {

	public JkException(String message, Object... params) {
		super(String.format(message, params));
	}

	public JkException(Throwable cause, String message, Object... params) {
		super(String.format(message, params), cause);
	}

	public JkException(Throwable cause) {
		super(cause);
	}
}
