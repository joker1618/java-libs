package spikes;

import org.junit.Test;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.html.JkHtmlTag;
import xxx.joker.libs.core.html.JkTextScannerImpl;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

@ToAnalyze
@Deprecated
public class Vari {

    @Test
    public void t() throws IOException {
        URL aa = getClass().getClassLoader().getResource("aa");
        File file = new File(aa.getFile());
        String str = JkStreams.join(Files.readAllLines(file.toPath()), "");
        JkTextScannerImpl scanner = new JkTextScannerImpl(str);
        scanner.startCursorAt("span class=\"mw-headline\" id=\"Race\"");
        JkHtmlTag table = scanner.nextHtmlTag("table");
        List<JkHtmlTag> rowTags = table.findFirsts("tr");
        rowTags.removeIf(tag -> tag.getChildren().size() != 8 || tag.getChildren("th").size() != 1 || tag.getChildren("td").size() != 7);


        for(int i = 0; i < rowTags.size(); i++) {
            JkHtmlTag row = rowTags.get(i);
            List<JkHtmlTag> tdTags = row.getChildren("td");
            List<JkHtmlTag> atags = tdTags.get(1).findAll("a");
            String dname = atags.get(atags.size() - 1).getTextTag();
        }
    }
}
