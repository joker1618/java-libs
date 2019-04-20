package temp;

import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Vari {

    @Test
    public void provaas() throws IOException, ParseException {
        String str = "primo.secondo.mp3";
        display(str.replaceAll("\\.[^\\.]*$", ""));
    }

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

        String elapsed = "1:0.234";
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).toStringElapsed(false));

        elapsed = "1:21:50";
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).toStringElapsed(true));


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
