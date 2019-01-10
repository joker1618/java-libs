package xxx.joker.libs.argsparser.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

public class DesignError extends JkRuntimeException {

    public DesignError(String message, Object... params) {
        super(message, params);
    }

    public DesignError(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

}
