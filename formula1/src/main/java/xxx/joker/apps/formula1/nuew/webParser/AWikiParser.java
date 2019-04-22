package xxx.joker.apps.formula1.nuew.webParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.nuew.common.F1Const;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;
import xxx.joker.apps.formula1.nuew.model.entities.*;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.web.JkDownloader;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public abstract class AWikiParser implements WikiParser {

    public static final Logger LOG = LoggerFactory.getLogger(AWikiParser.class);

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private final JkDownloader dwHtml = new JkDownloader(F1Const.HTML_FOLDER);
    private final JkDownloader dwTemp = new JkDownloader(F1Const.TMP_FOLDER);

    protected final F1Model model = F1ModelImpl.getInstance();

    protected int year;

    protected AWikiParser(int year) {
        this.year = year;
    }

    /**
     * Add entrants, teams (name and nation) and drivers (name and nation) to repository
     * Add driver web page link
     */
    protected abstract void parseEntrants(String html);
//    protected abstract List<String> getGpUrls(String html);
//    protected abstract Map<String, Integer> getExpectedDriverPoints(String html);
//    protected abstract Map<String, Integer> getExpectedTeamPoints(String html);

//    protected abstract void parseGpDetails(String html, F1GranPrix gp);
//    protected abstract void parseQualify(String html, F1GranPrix gp);
//    protected abstract void parseRace(String html, F1GranPrix gp);

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


//        List<String> gpUrls = getGpUrls(getMainPageHtml());
//        for (int i = 0; i < gpUrls.size(); i++) {
////            display(""+i);
//            String html = dwHtml.getHtml(gpUrls.get(i));
//            F1GranPrix gp = new F1GranPrix(year, i + 1);
//            if(model.getGranPrixs().add(gp)) {
//                parseGpDetails(html, gp);
//                display(gp.getCircuit().getNation());
//                parseQualify(html, gp);
//                parseRace(html, gp);
////                if(i == 0) break;
////                break;
//            }
//        }
    }

//    @Override
//    public Map<String, Integer> getExpectedDriverPoints() {
//        return getExpectedDriverPoints(getMainPageHtml());
//
//    }

//    @Override
//    public Map<String, Integer> getExpectedTeamPoints() {
//        return getExpectedTeamPoints(getMainPageHtml());
//    }

//    protected boolean downloadTrackMap(F1GranPrix gp, JkTag img) {
//        String url = createResourceUrl(img);
//        return resources.saveTrackMap(gp, url);
//    }
//
//    protected F1Circuit retrieveCircuit(String city, String nation, boolean createIfMissing) {
//        nation = fixNation(nation);
//        city = fixCity(city);
//        F1Circuit circuit = model.getCircuit(city, nation);
//        if(circuit == null && createIfMissing) {
//            circuit = new F1Circuit(city, nation);
//            model.add(circuit);
//        }
//        return circuit;
//    }
//
//    private String fixNation(String nation) {
//        if(nation.contains("Melbourne"))  return "Melbourne";
//        if(nation.contains("Texas"))  return "United States";
//        if(nation.contains("China"))  return "China";
//        if(nation.contains("Canada"))  return "Canada";
//        if(nation.equals("Lombardy"))  return "Italy";
//        if(nation.equals("England"))  return "United Kingdom";
//        return nation;
//    }
//    private String fixCity(String city) {
//        if(city.contains("Austin"))  return "Austin";
//        if(city.contains("Montreal"))  return "Montreal";
//        if(city.contains("Suzuka"))  return "Suzuka";
//        if(city.contains("Sepang"))  return "Sepang";
//        if(city.contains("Mexico City"))  return "Mexico City";
//        if(city.contains("Abu Dhabi"))  return "Abu Dhabi";
//        if(city.contains("Monte Carlo"))  return "Monte Carlo";
//        if(city.contains("Sochi"))  return "Sochi";
//        if(StringUtils.containsAny(city, "Montmel", "Valencia"))  return "Barcelona";
//        if(city.contains("Stavelot"))  return "Spa";
//        if(city.contains("Le Castellet"))  return "Le Castellet";
//        if(city.contains("Spielberg"))  return "Spielberg";
//        if(StringUtils.containsAny(city, "Nürburg", "Hockenheim"))  return "Hockenheim";
//        if(city.contains("Northamptonshire"))  return "Silverstone";
//        if(city.contains("South Jeolla"))  return "Yeongam";
//        if(city.contains("Uttar Pradesh"))  return "Uttar Pradesh";
//        return city;
//    }
//
//    protected F1Team retrieveTeam(String teamName, boolean createIfMissing) {
//        String tn = fixTeamName(teamName);
//        F1Team team = model.getTeam(tn);
//        if(team == null && createIfMissing) {
//            team = new F1Team(tn);
//            if(model.getTeams().add(team));
//        }
//        return team;
//    }
//
//    protected F1Driver retrieveDriver(String driverName, boolean createIfMissing) {
//        String dname = JkHtmlChars.fixDirtyChars(driverName);
//        dname = dname.replace("(racing driver)", "").trim();
//        F1Driver driver = model.getDriver(dname);
//        if(driver == null && createIfMissing) {
//            driver = new F1Driver(dname);
//            model.getDrivers().add(driver);
//        }
//        return driver;
//    }
//
//    protected F1Entrant getEntrant(int year, int carNum, F1Team team) {
//        return JkStreams.findUnique(model.getEntrants(year), e -> e.getCarNo() == carNum, e -> e.getTeam().equals(team));
//    }
//
//
//    protected JkDuration parseDuration(String str) {
//        String s = str;
//        int idx = str.lastIndexOf(".");
//        if(idx != -1) {
//            String stmp = s.substring(0, idx).replace(".", ":");
//            s = stmp + s.substring(idx);
//        }
//        return JkDuration.of(s);
//    }
//
//
//    private String fixTeamName(String teamName) {
//        if("Scuderia Toro Rosso".equals(teamName)){
//            return "Toro Rosso";
//        }
//        if("Red Bull Racing".equals(teamName)){
//            return "Red Bull";
//        }
//        return teamName;
//    }
//
    /**
     * Parse:
     * - driver, birth day, birth city, download pic
     */
    private void parseDriverPages(F1Driver driver, String pageUrl) {
        String wikiUrl = createWikiUrl(pageUrl);
        String html = dwHtml.getHtml(wikiUrl);

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<table class=\"infobox");
        JkTag tbody = tableEntrants.getChild("tbody");

        JkTag img = null;
        List<JkTag> trList = tbody.getChildren("tr");
        int rowNum = 0;
        while(img == null && rowNum < trList.size()) {
            img = tbody.getChild(rowNum).findFirstTag("img");
            rowNum++;
        }

        String picUrl = createResourceUrl(img);
        Pair<Boolean, Path> dwRes = dwTemp.downloadResource(pageUrl);
        model.saveDriverCover(dwRes.getValue(), driver);
        resources.saveDriverPicture(driver, picUrl);

        if(driver.getBirthDay() == null || StringUtils.isBlank(driver.getCity())) {
            JkTag rowBorn = null;
            while (rowBorn == null && rowNum < trList.size()) {
                JkTag tr = tbody.getChild(rowNum);
                JkTag ch = tr.getChild(0);
                if (ch.getTagName().equals("th") && "Born".equals(ch.getText())) {
                    rowBorn = tr;
                }
                rowNum++;
            }

            if (driver.getBirthDay() == null) {
                String strBirth = rowBorn.getChild(1).findChild("span span").getText();
                driver.setBirthDay(LocalDate.parse(strBirth));
                checkField(driver.getBirthDay(), "No birth date for {}", driver);
            }

            if (StringUtils.isBlank(driver.getCity())) {
                String[] split = rowBorn.getHtmlTag().split("<br[ ]?/>");
                String strCity = split[split.length - 1].replaceAll("<[^<]*?>", "").replaceAll(",[^,]*$", "").trim();
                driver.setCity(strCity);
                checkField(driver.getCity(), "No birth city for {}", driver);
            }
        }
    }


    protected String createWikiUrl(String wikiSubPath) {
        if(wikiSubPath.startsWith(PREFIX_URL))  {
            return wikiSubPath;
        }
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(JkTag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    private String createResourceUrl(JkTag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }

    private String getMainPageHtml() {
        String mainPageUrl = createWikiUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return dwHtml.getHtml(mainPageUrl);
    }

}
