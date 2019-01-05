package temp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.html.JkHtmlTag;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class Vari {

    @Test
    public void prova() throws IOException, ParseException {
        String s = "d^*fede";
        String quoted = Pattern.quote("^*");
        display("{}", s.replaceAll("^"+quoted, "__"));
        display("{}", s.replaceAll(quoted, "__"));
        display("{}={}%n", s, 5, 555);
        display("{}={}%n".replace("%n", "\n"), s, 5);
//        StringBuilder sb = new StringBuilder("  ciao da federico da merda");
////        display("%s", sb.toString().replaceAll("da (.*?) da", "1"));
//        Matcher matcher = Pattern.compile("^[a-zA-Z]").matcher(sb.toString());
//        if(matcher.find()) {
//            display("found");
//        }

        NumberFormat nfeng = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nfeng.setGroupingUsed(false);
        display("%s", nfeng.format(2212.23));
        display("%s", nfeng.format(nfeng.parse("4545.63").doubleValue()));

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
