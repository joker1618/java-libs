package xxx.joker.libs.argsparser.design.functions;

import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class ValueCheck {

    public static Function<Object, String> isFile() {
        return isFile(null);
    }

    public static Function<Object, String> isFile(String extension) {
        return o -> {
            Path p = (Path) o;
            if(!Files.exists(p)) {
                return strf("File {} does not exists", p);
            }
            if(!Files.isRegularFile(p)) {
                return strf("File {} is not regular file", p);
            }
            if(extension != null && !StringUtils.endsWithIgnoreCase(p.toString(), extension)) {
                return strf("File {} must have extension {}", p, extension);
            }
            return null;
        };
    }

}
