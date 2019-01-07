package xxx.joker.libs.core.exception;

import static xxx.joker.libs.core.utils.JkStrings.strf;

abstract class JkThrowableUtil {

    public static String toString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(toStringMainException(t, ""));
        Throwable actualThrowable = t.getCause();
        while(actualThrowable != null) {
            sb.append(toStringMainException(actualThrowable, "Caused by: "));
            actualThrowable = actualThrowable.getCause();
        }

        return sb.toString();
    }

    private static String toStringMainException(Throwable t, String mexPrefix) {
        StringBuilder sb = new StringBuilder();

        sb.append(mexPrefix);
        sb.append(strf("{}: {}", t.getClass().getName(), t.getMessage()));
        sb.append("\n");

        for(StackTraceElement elem : t.getStackTrace()) {
            sb.append("\tat ");
            sb.append(elem.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

}
