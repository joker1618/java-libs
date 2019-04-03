package various;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void tte() {
        String s = "fe<zio<mimmo>>as>";
        Pattern pattern = Pattern.compile("<([^<]*?)>");
        display("{}: {}", s, Arrays.toString(pattern.split(s)));

        Pattern pattern2 = Pattern.compile("(<[^<]*?>)");
        display("{}: {}", s, Arrays.toString(pattern2.split(s)));
        Matcher m = pattern2.matcher(s);
        m.find();
        display("{}: {}", s, m.groupCount());
        display("{}: {}", s, m.group(1));
        display("{}: {}", s, m.start());
        display("{}: {}", s, m.end());
    }

    @Test
    public void tt() {
        String dbName = "db";

        String s = "db#pippo#jkrepo.er";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#(.*?)#jkrepo\\.[^\\.]+$", s));

        s = "db##jkrepo.er";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#(.*?)#jkrepo\\.[^\\.]+$", s));

        s = "db#pippo#jkrepo.";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#(.*?)#jkrepo\\.[^\\.]+$", s));

        s = "db#pippo#jkrepoooo";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#(.*?)#jkrepo\\.[^\\.]+$", s));

        s = "db#pippo#jkrepo.e.e";
        display("{}: {}", s, s.matches("^"+dbName+"#(.*?)#jkrepo\\.[^\\.]+$"));

        s = "db#pippo #jkrepo.e#";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#[^#]*#jkrepo\\.[^.]+$", s));// correct

        s = "db#pippo #jkrepo.e#";
        display("{}: {}", s, Pattern.matches("^"+dbName+"#[^#]*#jkrepo\\.[^.#]+$", s));// correct
     }
}
