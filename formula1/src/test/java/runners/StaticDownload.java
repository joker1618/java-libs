package runners;

import org.junit.Test;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.F1ResourceManager;
import xxx.joker.apps.formula1.model.F1Resources;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.web.JkDownloader;

import java.util.List;

import static xxx.joker.apps.formula1.common.F1Const.*;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class StaticDownload {

    public static final String PREFIX_URL = "https://en.wikipedia.org";


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
    public void getAllSovereignFlags() {
        F1Resources resources = F1ResourceManager.getInstance();

        JkDownloader dhtml = new JkDownloader(HTML_FOLDER);
        String html = dhtml.getHtml("https://en.wikipedia.org/wiki/Gallery_of_sovereign_state_flags");

        JkTag rootDiv = JkScanners.parseHtmlTag(html, "div", "<div class=\"mw-parser-output\"");
        List<JkTag> divs = JkStreams.filter(rootDiv.getChildren("div"), t -> "mod-gallery mod-gallery-default".equals(t.getAttribute("class")));
        for (JkTag div : divs) {
            div.findFirstTags("li", "class=gallerybox").forEach(li -> {
                String atitle = li.findFirstTag("div", "class=gallerytext").findFirstTag("a").getAttribute("title");
                String nation = atitle.replaceAll("^Flag of the", "").replaceAll("^Flag of", "").replace("_", " ").replaceAll(" +", " ").trim();
                display(nation);
                JkTag aTag = li.findFirstTag("a", "class=image");
                String lastHtml = dhtml.getHtml(createWikiUrl(aTag));
                JkTag img = JkScanners.parseHtmlTag(lastHtml, "div", "<div class=\"fullImageLink\"").findFirstTag("img");
                resources.saveFlag(nation, createResourceUrl(img));
            });
        }

        F1ModelImpl.getInstance().commit();
    }


    protected String createWikiUrl(String wikiSubPath) {
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(JkTag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    private String createResourceUrl(JkTag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }
}
