package runner;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.core.media.JkMedia;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.service.commonRepo.JkCommonRepo;
import xxx.joker.service.commonRepo.JkCommonRepoImpl;
import xxx.joker.service.commonRepo.entities.JkFlag;
import xxx.joker.service.commonRepo.entities.JkNation;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.service.commonRepo.config.Configs.*;

public class GetNationFlags {

    private JkCommonRepo model = JkCommonRepoImpl.getInstance();

    @Test
    public void getAllNationFlags() {
        JkDownloader dhtml = new JkDownloader(HTML_FOLDER);
        String html = dhtml.getHtml("https://en.wikipedia.org/wiki/List_of_sovereign_states");

        JkTag tableTag = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"List_of_states\">", "<table class=\"sortable wikitable");
        List<JkTag> trList = tableTag.findChildren("tbody tr");

        JkDownloader dicon = new JkDownloader(FLAGS_FOLDER_ICON);
        JkDownloader dimg = new JkDownloader(FLAGS_FOLDER_IMAGE);

        Map<String, String> codes = getAllCountryCodes();

        int c = 0;
        for (JkTag tr : trList) {
            JkTag chTag = tr.getChild(0);
            if(!chTag.getTagName().equals("td")) continue;

            JkTag b = chTag.getChild("b");
            if(b == null) continue;

            JkTag span = b.getChild("span", "class=flagicon");
            if(span == null)    continue;

            String iconUrl = createImageUrl(span.getChild("img"));
            String str = iconUrl.substring(0, iconUrl.lastIndexOf("/")).replaceAll(".*Flag_of_", "");
            String key = JkStreams.findUnique(codes.keySet(), str::startsWith);
            String code = codes.remove(key);

            JkTag a = b.getChild("a");
            String nationName = a.getText();
            if(nationName.contains(",")) {
                String[] split = JkStrings.splitArr(nationName, ",", true);
                nationName = split[1] + " " + split[0];
            }

            if(code == null)  {
                display("Country code not found for {}", nationName);
                continue;
            }

            JkNation nation = model.getOrAdd(new JkNation(nationName));
            nation.setCode(code);
            if(nation.getFlag() == null) {
                String nationPageUrl = createWikiUrl(a);

                JkFlag flag = new JkFlag();
                nation.setFlag(flag);

                String iconName = fixResourceName(nationName, iconUrl);
                Pair<Boolean, Path> resDw = dicon.downloadResource(iconName, iconUrl);
                flag.setIcon(JkMedia.parseImage(resDw.getValue()));

                JkTag vcard = JkScanners.parseHtmlTag(dhtml.getHtml(nationPageUrl), "table", "<table class=\"infobox geography vcard\"");
                List<JkTag> vcardRows = vcard.findChildren("tbody tr");

                for (JkTag row : vcardRows) {
                    List<JkTag> alist = row.findAllTags("a", "class=image");
                    if(alist.size() >= 1) {
                        display("{}  {}", c++, nationName);
//                        c++;

                        String flagPageUrl = createWikiUrl(alist.get(0));
                        JkTag imgTag = JkScanners.parseHtmlTag(dhtml.getHtml(flagPageUrl), "img", "<div class=\"fullImageLink\"");
                        String imageUrl = createImageUrl(imgTag);

                        String imageName = fixResourceName(nationName, imageUrl);
                        resDw = dimg.downloadResource(imageName, imageUrl);
                        flag.setImage(JkMedia.parseImage(resDw.getValue()));

                        break;
                    }
                }
            }
        }

        if(!codes.isEmpty()) display("Codes unused: {}", codes.values());

        display("Nation flags downloaded {}", c);

        model.commit();
    }

    private Map<String,String> getAllCountryCodes() {
        Map<String,String> map = new HashMap<>();

        JkDownloader dhtml = new JkDownloader(HTML_FOLDER);
        String html = dhtml.getHtml("https://en.wikipedia.org/wiki/List_of_IOC_country_codes");

        JkTag tableTag = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Current_NOCs\">", "<table class=\"wikitable sortable");
        List<JkTag> trList = tableTag.findChildren("tbody tr");

        for (JkTag tr : trList) {
            int tdNum = tr.getChildren("td").size();
            if(tdNum == 4 && tdNum == tr.getChildren().size()) {
                String countryCode = tr.getChild(0).findFirstTag("span", "class=monospaced").getText();
                String imgUrl = createImageUrl(tr.getChild(1).findChild("img", "span a img"));

                String mapKey = imgUrl.substring(0, imgUrl.lastIndexOf("/"));
                mapKey = mapKey.replaceAll(".*Flag_of_", "").replaceAll("\\.svg$", "");
                map.put(mapKey, countryCode);
            }
        }

        return map;
    }

    private String fixResourceName(String fn, String url) {
        String finalFname = fn;
        int dotIdx = url.lastIndexOf(".");
        int slashIdx = url.lastIndexOf("/");
        if(dotIdx != -1 && (slashIdx == -1 || dotIdx > slashIdx)) {
            String fext = url.substring(dotIdx);
            if (!finalFname.endsWith(fext)) {
                finalFname += fext;
            }
        }
        return finalFname;
    }

    protected String createWikiUrl(String wikiSubPath) {
        return strf("https://en.wikipedia.org/{}", wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(JkTag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    private String createImageUrl(JkTag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }
}
