package runner;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.libs.repository.entities.RepoResource;
import xxx.joker.service.commonRepo.entities.JkNation;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.service.commonRepo.config.Configs.*;

/**
 * FIRST RUN: 6m 17s
 * RE-CREATE DB: 58s
 * RE-CREATE ALL: 1m 1s
 */
public class DownloadNationFlags extends AbstractRunner {

    @Test
    public void getAllNationFlags() {
        String html = getHtml("https://en.wikipedia.org/wiki/List_of_sovereign_states");

        JkTag tableTag = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"List_of_states\">", "<table class=\"sortable wikitable");
        List<JkTag> trList = tableTag.findChildren("tbody tr");

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

            String strInfo = strf("{}\t{}", c++, nationName);

            JkNation nation = model.getOrAdd(new JkNation(nationName));
            nation.setCode(code);

            if(nation.getFlagIcon() == null) {
                Pair<Boolean, Path> dwRes = downloadResource(iconUrl);
                RepoResource iconResource = model.addResource(dwRes.getValue(), nationName, "icon flag");
                nation.setFlagIcon(iconResource);
                strInfo += " icon";
            }

            if(nation.getFlagImage() == null) {
                String nationPageUrl = createWikiUrl(a);
                JkTag vcard = JkScanners.parseHtmlTag(getHtml(nationPageUrl), "table", "<table class=\"infobox geography vcard\"");
                List<JkTag> vcardRows = vcard.findChildren("tbody tr");

                for (JkTag row : vcardRows) {
                    List<JkTag> alist = row.findAllTags("a", "class=image");
                    if(alist.size() >= 1) {
                        strInfo += " image";

                        String flagPageUrl = createWikiUrl(alist.get(0));
                        JkTag imgTag = JkScanners.parseHtmlTag(getHtml(flagPageUrl), "img", "<div class=\"fullImageLink\"");
                        String imageUrl = createImageUrl(imgTag);

                        Pair<Boolean, Path> dwRes = downloadResource(imageUrl);
                        RepoResource imageURI = model.addResource(dwRes.getValue(), nationName, "image flag");
                        nation.setFlagImage(imageURI);

                        break;
                    }
                }
            }

            display(strInfo);
//            break;
        }

        model.commit();
    }

    private Map<String,String> getAllCountryCodes() {
        Map<String,String> map = new HashMap<>();

        String html = super.getHtml("https://en.wikipedia.org/wiki/List_of_IOC_country_codes");

        JkTag tableTag = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Current_NOCs\">", "<table class=\"wikitable sortable");
        List<JkTag> trList = tableTag.findChildren("tbody tr");

        for (JkTag tr : trList) {
            int tdNum = tr.getChildren("td").size();
            if(tdNum == 4 && tdNum == tr.getChildren().size()) {
                String countryCode = tr.getChild(0).findFirstTag("span", "class=monospaced").getText();
                String imgUrl = createImageUrl(tr.getChild(1).findChild("img", "span a img"));

                String mapKey = imgUrl.substring(0, imgUrl.lastIndexOf("/"));
                mapKey = mapKey.replaceAll("^.*Flag_of_|\\.svg$", "");
                map.put(mapKey, countryCode);
            }
        }

        return map;
    }

}
