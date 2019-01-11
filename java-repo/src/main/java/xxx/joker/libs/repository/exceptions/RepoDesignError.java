package xxx.joker.libs.repository.exceptions;

import xxx.joker.libs.core.exception.JkRuntimeException;

public class RepoDesignError extends JkRuntimeException {

    public RepoDesignError(String message, Object... params) {
        super(message, params);
    }
    
}
