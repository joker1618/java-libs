package xxx.joker.libs.argsparser.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ParseError extends JkRuntimeException {

    public ParseError(String message, Object... params) {
        super(message, params);
    }

    public ParseError(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

}
