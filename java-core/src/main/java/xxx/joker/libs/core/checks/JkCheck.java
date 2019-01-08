package xxx.joker.libs.core.checks;

import java.nio.file.Path;
import java.util.List;

public class JkCheck {

    /* PATHS */
    public static boolean areEquals(Path p1, Path p2) {
        return p1.toAbsolutePath().normalize().equals(p2.toAbsolutePath().normalize());
    }
    public static boolean containsPath(List<Path> source, Path toFind) {
        for(Path p : source) {
            if(areEquals(p, toFind)) {
                return true;
            }
        }
        return false;
    }


}
