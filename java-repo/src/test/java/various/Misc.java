package various;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Misc {

    @Test
    public void t22t() {
        Path p1 = Paths.get("pippo");
        Path p2 = p1.toAbsolutePath();
        display("1 {}", p1.relativize(p1.resolve("file")));
        display("2 {}", p2.relativize(p2.resolve("file")));
    }

    @Test
    public void tt() {
        Path p1 = Paths.get("pippo").toAbsolutePath();
        Path p2 = p1.resolve("../pippo");
        display("A =   {}\nB =   {}\nres = {}", p1, p2, p1.compareTo(p2));
    }
}
