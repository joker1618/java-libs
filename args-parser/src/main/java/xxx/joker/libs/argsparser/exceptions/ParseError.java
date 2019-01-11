package xxx.joker.libs.argsparser.exceptions;

import xxx.joker.libs.argsparser.service.ArgWrapper;
import xxx.joker.libs.argsparser.service.CmdWrapper;
import xxx.joker.libs.core.exception.JkRuntimeException;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ParseError extends JkRuntimeException {

    public ParseError(String message, Object... params) {
        super(message, params);
    }

//    public ParseError(Throwable cause, String message, Object... params) {
//        super(cause, message, params);
//    }

    public ParseError(CmdWrapper cw, ArgWrapper aw, String mex, Object... params) {
        super(strf("Command {}, arg {}: ", cw.getCmdName(), aw.getArgType()) + strf(mex, params));
    }
}
