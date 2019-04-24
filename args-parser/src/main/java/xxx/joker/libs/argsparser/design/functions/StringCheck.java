package xxx.joker.libs.argsparser.design.functions;

import org.apache.commons.lang3.StringUtils;

import java.util.function.UnaryOperator;

public class StringCheck {

    public static UnaryOperator<String> isNotBlank() {
        return str -> {
            if(StringUtils.isBlank(str)) {
                return "Blank string not allowed";
            }
            return null;
        };
    }

}
