package temp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.html.JkHtmlTag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class Vari {

    @Test
    public void prova12() throws IOException, ParseException {
        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\music");

        display("\nMAX DEPTH = 0");
        Files.find(root, 0, (p,a)->true).forEach(p -> display("  {}", p));

        display("\nMAX DEPTH = 1");
        Files.find(root, 1, (p,a)->true).forEach(p -> display("  {}", p));

        display("\nMAX DEPTH = 2");
        Files.find(root, 2, (p,a)->true).forEach(p -> display("  {}", p));


        display("%s: %d%n%d", "fed", 3, 22);
    }


    @Test
    public void prova() throws IOException, ParseException {

        Path p1 = Paths.get("").toAbsolutePath();
        Path p2 = p1.resolve("dir/file.txt");
        Path p3 = Paths.get("").toAbsolutePath().getParent().resolve("ciccio");

        display("p1:\t{}", p1);
        display("p2:\t{}", p2);
        display("p1Rp2:\t{}", p1.relativize(p2));
        display("p2Rp1:\t{}", p2.relativize(p1));

        display("\np3:\t{}", p3);
        display("p1Rp3:\t{}", p1.relativize(p3));
        display("p3Rp1:\t{}", p3.relativize(p1));

        display("final:\t{}", p3.resolve(p1.relativize(p2)));


    }

    @Test
    public void prova2() throws IOException {

        String txt = "<input type=\"text\"value=\"fede\"/>";
        String txt2 = "<input type=\"text\"value=\"fede\">";

//        txt = txt.replaceAll("<!--(.*?)-->", "");

        Pattern tagPattern = Pattern.compile("<(.*?) ");
        Matcher matcher = tagPattern.matcher(txt);
        matcher.find();
        display(matcher.group(1)+"*");
        display(matcher.toMatchResult().group(1)+"*");

        tagPattern = Pattern.compile("<(.*)>");
        matcher = tagPattern.matcher(txt);
        matcher.find();
        display(matcher.group(1));
        matcher = tagPattern.matcher(txt2);
        matcher.find();
        display(matcher.group(1));
    }
}
