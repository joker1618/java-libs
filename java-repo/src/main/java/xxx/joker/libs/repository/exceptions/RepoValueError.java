package xxx.joker.libs.repository.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

public class RepoValueError extends JkRuntimeException {

    public RepoValueError(String message, Object... params) {
        super(message, params);
    }

}
