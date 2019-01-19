package xxx.joker.libs.argsparser.design.functions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ValueCheck {

    public static Function<Object, String> isFile() {
        return o -> {
            Path p = (Path) o;
            if(!Files.exists(p)) {
                return strf("File {} does not exists", p);
            }
            if(!Files.isRegularFile(p)) {
                return strf("File {} is not regular file", p);
            }
            return null;
        };
    }

}
