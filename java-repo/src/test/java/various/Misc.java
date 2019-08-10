package various;

import org.junit.Test;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Misc {

//    @Test
//    public void tenum() {
//        Class<?> clazz = RepoUriType.class;
//        Object ruto = Enum.valueOf((Class)clazz, "IMAGE");
//        display("{}",ruto);
//        RepoUriType rut = (RepoUriType) Enum.valueOf((Class)clazz, "IMAGE");
//        display(rut.name());
//        display("{}", JkReflection.isInstanceOf(clazz, Enum.class));
//
//        String s = "fe%%de/r/ico,l  a;ma:don na";
//        display("{}\n{}", s, s.replaceAll("[/%,;:\\s]", "_"));
////
////        Collection<Integer> coll = Collections.emptyList();
////        display(coll.getClass().getName());
////        display(coll.getClass().getTypeName());
////        display(((ParameterizedType) coll.getClass()
////                .getGenericSuperclass()).toString());
////        display(((ParameterizedType) coll.getClass()
////                .getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
////        Class genclazz = (Class) ((ParameterizedType) coll.getClass()
////                .getGenericSuperclass()).getActualTypeArguments()[0];
////        display(genclazz.getName());
//    }

    @Test
    public void t22t() {
        Path p1 = Paths.get("pippo");
        Path p2 = p1.toAbsolutePath();
        display("1 {}", p1.relativize(p1.resolve("file")));
        display("2 {}", p2.relativize(p2.resolve("file")));
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
