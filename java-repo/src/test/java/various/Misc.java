package various;

import org.junit.Test;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.datalayer.design.EntityPK;
import xxx.joker.libs.datalayer.design.ResourcePath;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.runtimes.JkReflection.isInstanceOf;
import static xxx.joker.libs.core.runtimes.JkReflection.isOfClass;
import static xxx.joker.libs.core.utils.JkConsole.display;

public class Misc {

    @Test
    public void tenum() {
        Class<?> clazz = int.class;
        Class<?> clazz2 = String.class;
        display("{}", clazz.getName());
        display("{}", clazz2.getName());
        display("{}", clazz == int.class);
    }

    @Test
    public void t22t() {
        Class<?> a1 = ResourcePath.class;
        Class<?> a2 = EntityPK.class;
        List<Class<?>> alist = Arrays.asList(a1, a2);
        display("a1  {}  {}  {}  {}", a1 == ResourcePath.class, a1.equals(ResourcePath.class),
                isOfClass(a1, ResourcePath.class), isInstanceOf(a1, ResourcePath.class)
        );
        display("a1  {}  {}  {}  {}  {}", alist.contains(a1), alist.contains(ResourcePath.class),
                JkStreams.count(alist, ac -> isOfClass(ResourcePath.class)),
                JkStreams.count(alist, ac -> ac == ResourcePath.class),
                JkStreams.count(alist, ac -> ac.equals(ResourcePath.class))
        );

        List<Class<?>> alist2 = Arrays.asList(EntityPK.class, ResourcePath.class);
        Class<?> ac = alist2.get(0);
        display("a2  {}", alist2.contains(ac));

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
