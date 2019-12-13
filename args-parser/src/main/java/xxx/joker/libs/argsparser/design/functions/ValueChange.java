package xxx.joker.libs.argsparser.design.functions;

import xxx.joker.libs.core.util.JkConvert;

import java.util.function.UnaryOperator;

public class ValueChange {

    public static UnaryOperator<String> toWindowsPath() {
        return JkConvert::unixToWinPath;
    }


}
