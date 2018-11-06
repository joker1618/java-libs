package temp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.html.JkHtmlTag;
import xxx.joker.libs.core.html.JkTextScannerImpl;
import xxx.joker.libs.core.utils.JkFiles;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Vari {

    @Test
    public void prova() throws IOException {

        Path path = Paths.get("C:\\tmp\\testo.txt");
        List<String> lines = Files.readAllLines(path);
        String joined = JkStreams.join(lines, "\n");
////        display("%s", joined);
//        JkTextScannerImpl scanner = new JkTextScannerImpl(joined);
//        JkHtmlTag tag = scanner.nextHtmlTag("ol");
//        printTag(tag, 0);

        JkTextScannerImpl scanner = new JkTextScannerImpl(joined);
        scanner.startCursorAfter("<span class=\"mw-headline\" id=\"Qualifying\">");
        JkHtmlTag tableTag = scanner.nextHtmlTag("table");
        display(tableTag.getChildrenTags("tr").size()+"");

    }
    private void printTag(JkHtmlTag tag, int level) {
        String prefix = StringUtils.repeat("  ", level);
        int chsize = tag.getChildren().size();
        display("%s%s (%d)", prefix, tag.getTagName(), chsize);
//        display("%s%s (%d)  %s", prefix, tag.getTagName(), chsize, tag.getAttributes());
//        display("%s%s (%d)  %s  %s", prefix, tag.getTagName(), chsize, tag.getAttributes(), chsize == 0 ? "*"+tag.getTextInside()+"*" : "");
        tag.getChildren().forEach(c -> printTag(c, level+1));
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
