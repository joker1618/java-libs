package xxx.joker.libs.repository.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

public class RepoError extends JkRuntimeException {

    public RepoError(String message) {
        super(message);
    }

    public RepoError(String message, Object... params) {
        super(message, params);
    }

    public RepoError(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    public RepoError(Throwable cause) {
        super(cause);
    }

}
