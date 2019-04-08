package runners;

import org.junit.Test;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Link;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.web.JkDownloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static xxx.joker.apps.formula1.common.F1Const.*;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class StaticDownload {



    @Test
    public void getMainPage(){
        int ystart = 1980;
        int yend = 2018;

        JkDownloader downloader = new JkDownloader(HTML_FOLDER);
        display("Downloaded web pages:");
        for (int i = yend; i >= ystart; i--) {
            String url = strf("https://en.wikipedia.org/wiki/{}_Formula_One_World_Championship", i);
            downloader.getHtml(url);
            display("  {}  main page", i);
        }
    }

    @Test
    public void getAllFlagIcons(){
        Set<F1Link> links = F1ModelImpl.getInstance().getLinks();
        List<F1Link> flagLinks = JkStreams.filter(links, l -> l.getKey().startsWith("nation.icon"));

        JkDownloader downloader = new JkDownloader(FLAGS_FOLDER);
        display("Downloading flag icons:");
        flagLinks.forEach(l -> {
            String flagName = l.getKey().replaceAll(".*\\.", "") + ".icon";
            downloader.downloadResource(flagName, l.getUrl());
            display("  {}", flagName);
        });
    }
}
