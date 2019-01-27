package xxx.joker.libs.argsparser.design.functions;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static xxx.joker.libs.core.utils.JkStrings.strf;

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
