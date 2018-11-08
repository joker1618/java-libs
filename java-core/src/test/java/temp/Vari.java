package temp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.html.JkHtmlTag;
import xxx.joker.libs.core.html.JkTextScannerImpl;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Vari {

    @Test
    public void prova() throws IOException {

//        StringBuilder sb = new StringBuilder("  ciao da federico da merda");
////        display("%s", sb.toString().replaceAll("da (.*?) da", "1"));
//        Matcher matcher = Pattern.compile("^[a-zA-Z]").matcher(sb.toString());
//        if(matcher.find()) {
//            display("found");
//        }

        Matcher m = Pattern.compile("^\\s*(.*)$").matcher("  ciao da federicod  ");
        if(m.find()) {
            display("%s*", m.group(1));
        }

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
