package runners;

import org.junit.Test;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;
import xxx.joker.apps.formula1.dataCreator.model.F1ResourceManager;
import xxx.joker.apps.formula1.dataCreator.model.F1Resources;
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

        int n = 0;
        int e = 0;
        for (JkTag div : divs) {
            for (JkTag li : div.findFirstTags("li", "class=gallerybox")) {
                String atitle = li.findFirstTag("div", "class=gallerytext").findFirstTag("a").getAttribute("title");
                String nation = atitle.replaceAll("^Flag of the", "").replaceAll("^Flag of", "").replace("_", " ").replaceAll(" +", " ").trim();
                display(nation);
                JkTag aTag = li.findFirstTag("a", "class=image");
                String lastHtml = dhtml.getHtml(createWikiUrl(aTag));
                JkTag img = JkScanners.parseHtmlTag(lastHtml, "div", "<div class=\"fullImageLink\"").findFirstTag("img");
                boolean res = resources.saveFlag(nation, createImageUrl(img));
                if(res) n++; else e++;
            }
        }

        display("Flags recap: {} ({} new, {} old)", n+e, n, e);

        F1ModelImpl.getInstance().commit();
    }

    @Test
    public void getAllIconFlagsAndCountryCodes() {
        F1Resources resources = F1ResourceManager.getInstance();

        JkDownloader dhtml = new JkDownloader(HTML_FOLDER);
        String html = dhtml.getHtml("https://en.wikipedia.org/wiki/List_of_IOC_country_codes");

        JkTag tableTag = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Current_NOCs\">", "<table class=\"wikitable sortable");
        List<JkTag> trList = tableTag.findChildren("tbody tr");

        int n = 0;
        int e = 0;
        for (JkTag tr : trList) {
            int tdNum = tr.getChildren("td").size();
            if(tdNum == 4 && tdNum == tr.getChildren().size()) {
                String countryCode = tr.getChild(0).findFirstTag("span", "class=monospaced").getText();
                String imgUrl = createImageUrl(tr.getChild(1).findChild("img", "span a img"));
                String nation = tr.getChild(1).getChild("a").getText();
                display("{}   {}", countryCode, nation);
                boolean res = resources.saveFlagIcon(nation, imgUrl);
                if(res) n++; else e++;
            }
        }

        display("Flags icons recap: {} ({} new, {} old)", n+e, n, e);

        F1ModelImpl.getInstance().commit();
    }

    @Test
    public void checkFlags() {
        
    }


    protected String createWikiUrl(String wikiSubPath) {
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(JkTag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    private String createImageUrl(JkTag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }
}
