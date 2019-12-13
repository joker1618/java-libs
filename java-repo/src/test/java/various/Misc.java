package various;

import javafx.beans.property.SimpleBooleanProperty;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.runtime.JkReflection.isInstanceOf;
import static xxx.joker.libs.core.runtime.JkReflection.isOfClass;
import static xxx.joker.libs.core.util.JkConsole.display;

public class Misc {

    @Test
    public void tenum() {
        Class<?> clazz = int.class;
        Class<?> clazz2 = String.class;
        display("{}", clazz.getName());
        display("{}", clazz2.getName());
        display("{}", clazz == int.class);
        display(StringUtils.equals(null, null));
        Path p1 = Paths.get("").toAbsolutePath().resolve("pippo");
        Path p2 = Paths.get("").toAbsolutePath().resolve("pluto");
        display(p1.resolve(p2));

        SimpleBooleanProperty sop = new SimpleBooleanProperty(true);
        sop.addListener((obs,o,n) -> display("fede"));
        sop.set(false);
        sop.set(false);
        sop.set(true);

    }

    @Test
    public void tte() throws ClassNotFoundException {

        display("{}", Class.forName("java.lang.String"));
//        String s = "fe<zio<mimmo>>as>";
//        Pattern pattern = Pattern.compile("<([^<]*?)>");
//        display("{}: {}", s, Arrays.toString(pattern.split(s)));
//
//        Pattern pattern2 = Pattern.compile("(<[^<]*?>)");
//        display("{}: {}", s, Arrays.toString(pattern2.split(s)));
//        Matcher m = pattern2.matcher(s);
//        m.find();
//        display("{}: {}", s, m.groupCount());
//        display("{}: {}", s, m.group(1));
//        display("{}: {}", s, m.start());
//        display("{}: {}", s, m.end());
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
