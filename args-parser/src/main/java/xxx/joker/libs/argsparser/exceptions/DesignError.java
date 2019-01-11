package xxx.joker.libs.argsparser.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class DesignError extends JkRuntimeException {

//    public DesignError(String format, Object... params) {
//        super(format, params);
//    }
//
//    public DesignError(Throwable cause, String format, Object... params) {
//        super(cause, format, params);
//    }

    public DesignError(Class<?> clazz, String format, Object... params) {
        super(strf("Class [{}]: " + format, clazz.getSimpleName(), params));
    }

}
