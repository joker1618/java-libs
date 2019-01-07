package xxx.joker.libs.core.exception;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 19/11/2017.
 */
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrBuilder;
import xxx.joker.libs.core.ToAnalyze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JkRuntimeException extends RuntimeException {

	public JkRuntimeException(String message, Object... params) {
		super(strf(message, params));
	}

	public JkRuntimeException(Throwable cause, String message, Object... params) {
		super(strf(message, params), cause);
	}

	public JkRuntimeException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return JkThrowableUtil.toString(this);
	}

}
