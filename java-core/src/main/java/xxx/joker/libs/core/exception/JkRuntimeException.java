package xxx.joker.libs.core.exception;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 19/11/2017.
 */

public class JkRuntimeException extends RuntimeException implements JkThrowable {

	private boolean simpleClassName;

	public JkRuntimeException(String message, Object... params) {
		super(strf(message, params));
	}

	public JkRuntimeException(boolean simpleClassName, String message, Object... params) {
		super(strf(message, params));
		this.simpleClassName = simpleClassName;
	}

	public JkRuntimeException(Throwable cause, String message, Object... params) {
		super(strf(message, params), cause);
	}

	public JkRuntimeException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getErrorMex() {
		return super.getMessage();
	}

	@Override
	public List<String> getCauses() {
		List<String> causes = new ArrayList<>();
		Throwable iter = getCause();
		while (iter != null) {
			causes.add(iter.getMessage());
			iter = iter.getCause();
		}
		return causes;
	}

	@Override
	public String toString() {
		return JkThrowableUtil.toString(this, simpleClassName);
	}


}
