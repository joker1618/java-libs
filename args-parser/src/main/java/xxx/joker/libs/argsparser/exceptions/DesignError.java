package xxx.joker.libs.argsparser.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

import static xxx.joker.libs.core.util.JkStrings.strf;

public class DesignError extends JkRuntimeException {

    public DesignError(String format, Object... params) {
        super(strf(format, params));
    }
    public DesignError(Class<?> clazz, String format, Object... params) {
        super(strf("Class [{}]: ", clazz.getSimpleName())+strf(format, params));
    }

}
