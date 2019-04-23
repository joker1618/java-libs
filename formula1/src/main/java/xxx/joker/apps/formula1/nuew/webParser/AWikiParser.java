package xxx.joker.apps.formula1.nuew.webParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.nuew.common.F1Const;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;
import xxx.joker.apps.formula1.nuew.model.entities.*;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.scanners.JkHtmlChars;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.service.sharedRepo.JkSharedRepo;
import xxx.joker.service.sharedRepo.JkSharedRepoImpl;
import xxx.joker.service.sharedRepo.entities.JkNation;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public abstract class AWikiParser implements WikiParser {

    public static final Logger LOG = LoggerFactory.getLogger(AWikiParser.class);

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private final JkDownloader dwHtml = new JkDownloader(F1Const.HTML_FOLDER);
    private final JkDownloader dwTemp = new JkDownloader(F1Const.TMP_FOLDER);

    protected final F1Model model = F1ModelImpl.getInstance();
    protected final JkSharedRepo sharedRepo = JkSharedRepoImpl.getInstance();

    protected int year;

    protected AWikiParser(int year) {
        this.year = year;
    }

    /**
     * Add entrants, teams (name and nation) and drivers (name and nation) to repository
     * Add driver web page link
     */
    protected abstract void parseEntrants(String html);
    protected abstract List<String> getGpUrls(String html);
    protected abstract Map<String, Integer> getExpectedDriverPoints(String html);
    protected abstract Map<String, Integer> getExpectedTeamPoints(String html);

    protected abstract void parseGpDetails(String html, F1GranPrix gp);
    protected abstract void parseQualify(String html, F1GranPrix gp);
    protected abstract void parseRace(String html, F1GranPrix gp);

    @Override
    public void parse() {
        parseEntrants(getMainPageHtml());

//        parseDriverPages();
//        if(1==1)    return;

//        Map<String, Integer> expDriverMap = getExpectedDriverPoints(dwHtml.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesDriverMap = JkStreams.sorted(expDriverMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strDrivers = JkStreams.join(entriesDriverMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected driver points ({})\n{}", entriesDriverMap.size(), strDrivers);
//
//        Map<String, Integer> expTeamMap = getExpectedTeamPoints(dwHtml.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesTeamMap = JkStreams.sorted(expTeamMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strTeams  = JkStreams.join(entriesTeamMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected team points ({})\n{}", entriesTeamMap.size(), strTeams);


        List<String> gpUrls = getGpUrls(getMainPageHtml());
        for (int i = 0; i < gpUrls.size(); i++) {
//            display(""+i);
            String html = dwHtml.getHtml(createWikiUrl(gpUrls.get(i)));
            F1GranPrix gp = new F1GranPrix(year, i + 1);
            if(model.add(gp)) {
                parseGpDetails(html, gp);
                display(gp.getCircuit().getNation());
                parseQualify(html, gp);
                parseRace(html, gp);
//                if(i == 0) break;
//                break;
            }
        }
    }

    @Override
    public Map<String, Integer> getExpectedDriverPoints() {
        return getExpectedDriverPoints(getMainPageHtml());

    }

    @Override
    public Map<String, Integer> getExpectedTeamPoints() {
        return getExpectedTeamPoints(getMainPageHtml());
    }

    protected void downloadTrackMap(F1GranPrix gp, JkTag aTag) {
        String url = createWikiUrl(aTag.getAttribute("href"));
        String html = dwHtml.getHtml(url);
        JkTag imgTag = JkScanners.parseHtmlTag(html, "img", "<div class=\"fullImageLink\"", "<a", "<img");
        String imgUrl = createResourceUrl(imgTag);
        Pair<Boolean, Path> dwRes = dwTemp.downloadResource(imgUrl);
        model.saveGpTrackMap(dwRes.getValue(), gp);
    }

    protected F1Circuit retrieveCircuit(String city, String nation, boolean createIfMissing) {
        nation = fixNation(nation);
        city = fixCity(city);
        F1Circuit circuit = model.getCircuit(city, nation);
        if(circuit == null && createIfMissing) {
            circuit = new F1Circuit(city, nation);
            model.add(circuit);
        }
        return circuit;
    }

    private String fixNation(String nation) {
        if(nation.contains("Melbourne"))  return "Melbourne";
        if(nation.contains("Texas"))  return "United States";
        if(nation.contains("China"))  return "China";
        if(nation.contains("Canada"))  return "Canada";
        if(nation.equals("Lombardy"))  return "Italy";
        if(nation.equals("England"))  return "United Kingdom";
        return nation;
    }
    private String fixCity(String city) {
        if(city.contains("Austin"))  return "Austin";
        if(city.contains("Montreal"))  return "Montreal";
        if(city.contains("Suzuka"))  return "Suzuka";
        if(city.contains("Sepang"))  return "Sepang";
        if(city.contains("Mexico City"))  return "Mexico City";
        if(city.contains("Abu Dhabi"))  return "Abu Dhabi";
        if(city.contains("Monte Carlo"))  return "Monte Carlo";
        if(city.contains("Sochi"))  return "Sochi";
        if(StringUtils.containsAny(city, "Montmel", "Valencia"))  return "Barcelona";
        if(city.contains("Stavelot"))  return "Spa";
        if(city.contains("Le Castellet"))  return "Le Castellet";
        if(city.contains("Spielberg"))  return "Spielberg";
        if(StringUtils.containsAny(city, "Nürburg", "Hockenheim"))  return "Hockenheim";
        if(city.contains("Northamptonshire"))  return "Silverstone";
        if(city.contains("South Jeolla"))  return "Yeongam";
        if(city.contains("Uttar Pradesh"))  return "Uttar Pradesh";
        return city;
    }

    protected F1Team retrieveTeam(String teamName, boolean createIfMissing) {
        String tn = fixTeamName(teamName);
        F1Team team = model.getTeam(tn);
        if(team == null && createIfMissing) {
            team = new F1Team(tn);
            if(model.getTeams().add(team));
        }
        return team;
    }
    private String fixTeamName(String teamName) {
        switch (teamName) {
            case "Scuderia Toro Rosso": return "Toro Rosso";
            case "Red Bull Racing":     return "Red Bull";
            default:    return teamName;
        }
    }

    protected F1Driver retrieveDriver(String driverName, boolean createIfMissing) {
        String dname = JkHtmlChars.fixDirtyChars(driverName);
        dname = dname.replace("(racing driver)", "").trim();
        F1Driver driver = model.getDriver(dname);
        if(driver == null && createIfMissing) {
            driver = new F1Driver(dname);
            model.getDrivers().add(driver);
        }
        return driver;
    }

    protected void checkNation(RepoEntity e, String nation) {
        JkNation n = sharedRepo.getNation(nation);
        if(n == null) {
            throw new JkRuntimeException("Nation [{}] not exists. Entity: {}", nation, e);
        }
    }


    protected F1Entrant getEntrant(int year, F1Driver driver, F1Team team) {
        return JkStreams.findUnique(model.getEntrants(year), e -> e.getDriver().equals(driver), e -> e.getTeam().equals(team));
    }


    protected JkDuration parseDuration(String str) {
        String s = str;
        int idx = str.lastIndexOf(".");
        if(idx != -1) {
            String stmp = s.substring(0, idx).replace(".", ":");
            s = stmp + s.substring(idx);
        }
        return JkDuration.of(s);
    }



    /**
     * Parse:
     * - driver, birth day, birth city, download pic
     */
    protected void parseDriverPage(F1Driver driver, JkTag aTag) {
        parseDriverPage(driver, aTag.getAttribute("href"));
    }
    protected void parseDriverPage(F1Driver driver, String pageUrl) {
        String wikiUrl = createWikiUrl(pageUrl);
        String html = dwHtml.getHtml(wikiUrl);

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<table class=\"infobox");
        JkTag tbody = tableEntrants.getChild("tbody");

        JkTag aTag = null;
        List<JkTag> trList = tbody.getChildren("tr");
        int rowNum = 0;
        while(aTag == null && rowNum < trList.size()) {
            aTag = tbody.getChild(rowNum, 0).getChild("a", "class=image");
            rowNum++;
        }

        // Driver cover
        String url = createWikiUrl(aTag.getAttribute("href"));
        String dhtml = dwHtml.getHtml(url);
        JkTag imgTag = JkScanners.parseHtmlTag(dhtml, "img", "<div class=\"fullImageLink\"", "<a", "<img");
        String imgUrl = createResourceUrl(imgTag);
        Pair<Boolean, Path> dwRes = dwTemp.downloadResource(imgUrl);
        model.saveDriverCover(dwRes.getValue(), driver);

        // Driver details
        JkTag rowBorn = null;
        while (rowBorn == null && rowNum < trList.size()) {
            JkTag tr = tbody.getChild(rowNum);
            JkTag ch = tr.getChild(0);
            if (ch.getTagName().equals("th") && "Born".equals(ch.getText())) {
                rowBorn = tr;
            }
            rowNum++;
        }

        String strBirth = rowBorn.getChild(1).findChild("span span").getText();
        driver.setBirthDay(LocalDate.parse(strBirth));

        String[] split = rowBorn.getHtmlTag().split("<br[ ]?/>");
        String strCity = split[split.length - 1].replaceAll("<[^<]*?>", "").replaceAll(",[^,]*$", "").trim();
        driver.setCity(strCity);
    }



    private String createWikiUrl(String wikiSubPath) {
        if(wikiSubPath.startsWith(PREFIX_URL))  {
            return wikiSubPath;
        }
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }

    private String createResourceUrl(JkTag img) {
        String srcset = img.getAttribute("srcset");
        if(srcset == null) {
            return strf("https:{}", img.getAttribute("src").trim());
        }
        return strf("https:{}", srcset.replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }

    private String getMainPageHtml() {
        String mainPageUrl = createWikiUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return dwHtml.getHtml(mainPageUrl);
    }

}
