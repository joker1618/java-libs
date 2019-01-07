package xxx.joker.libs.core.exception;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 19/11/2017.
 */
import xxx.joker.libs.core.ToAnalyze;


public class JkException extends Exception {

	public JkException(String message, Object... params) {
		super(strf(message, params));
	}

	public JkException(Throwable cause, String message, Object... params) {
		super(strf(message, params), cause);
	}

	public JkException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return JkThrowableUtil.toString(this);
	}

}
