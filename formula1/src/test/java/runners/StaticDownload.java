package runners;

import org.junit.Test;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Link;
import xxx.joker.libs.core.web.JkDownloader;

import java.util.Set;

import static xxx.joker.apps.formula1.common.F1Const.HTML_FOLDER;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class StaticDownload {

    JkDownloader downloader = new JkDownloader(HTML_FOLDER);

    @Test
    public void getMainPage(){
        int ystart = 1980;
        int yend = 2018;
        display("Downloaded web pages:");
        for (int i = yend; i >= ystart; i--) {
            String url = strf("https://en.wikipedia.org/wiki/{}_Formula_One_World_Championship", i);
            downloader.getHtml(url);
            display("  {}  main page", i);
        }
    }

    @Test
    public void getAllLinks(){
        Set<F1Link> links = F1ModelImpl.getInstance().getLinks();
        display("Downloaded web pages:");
        links.forEach(l -> {
            downloader.getHtml(l.getUrl());
            display("  {}", l.getUrl());
        });
    }
}
