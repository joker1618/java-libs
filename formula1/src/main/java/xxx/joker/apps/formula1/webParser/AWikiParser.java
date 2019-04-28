package xxx.joker.apps.formula1.webParser;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.apps.formula1.model.fields.F1FastLap;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.scanners.JkHtmlChars;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.service.sharedRepo.JkSharedRepo;
import xxx.joker.service.sharedRepo.JkSharedRepoImpl;
import xxx.joker.service.sharedRepo.entities.JkNation;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    protected abstract Map<String, Double> getExpectedDriverPoints(String html);
    protected abstract Map<String, Double> getExpectedTeamPoints(String html);

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
//        System.exit(1);

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
    public Map<String, Double> getExpectedDriverPoints() {
        return getExpectedDriverPoints(getMainPageHtml());

    }

    @Override
    public Map<String, Double> getExpectedTeamPoints() {
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
        if(nation.contains("Monte Carlo"))  return "Monaco";
        if(nation.contains("Melbourne"))  return "Melbourne";
        if(nation.contains("Indiana"))  return "United States";
        if(nation.contains("Texas"))  return "United States";
        if(nation.contains("China"))  return "China";
        if(nation.contains("Canada"))  return "Canada";
        if(nation.equals("Lombardy"))  return "Italy";
        if(nation.equals("England"))  return "United Kingdom";
        return nation;
    }
    private String fixCity(String city) {
        if(StringUtils.containsAny(city, "Mogyoród", "Budapest"))  return "Mogyoród";
        if(city.contains("Austin"))  return "Austin";
        if(city.contains("Montreal"))  return "Montreal";
        if(city.contains("Oyama"))  return "Oyama";
        if(city.contains("Suzuka"))  return "Suzuka";
        if(city.contains("Sepang"))  return "Sepang";
        if(city.contains("Mexico City"))  return "Mexico City";
        if(city.contains("Abu Dhabi"))  return "Abu Dhabi";
        if(StringUtils.containsAny(city, "Circuit de Monaco", "Monte Carlo"))  return "Monte Carlo";
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
            case "Scuderia Toro Rosso":     return "Toro Rosso";
            case "Red Bull Racing":         return "Red Bull";
            case "McLaren-Mercedes":        return "McLaren";
            case "Spyker MF1":
            case "MF1":
                return "Midland F1";
            default:    return teamName;
        }
    }

    protected F1Driver retrieveDriver(String driverName, boolean createIfMissing) {
        String dname = fixDriverName(driverName);
        F1Driver driver = model.getDriver(dname);
        if(driver == null && createIfMissing) {
            driver = new F1Driver(dname);
            model.getDrivers().add(driver);
        }
        return driver;
    }
    private String fixDriverName(String driverName) {
        String dn = JkHtmlChars.fixDirtyChars(driverName);
        dn = dn.replace("(racing driver)", "").trim();
        switch (dn) {
            case "Nelson Piquet, Jr.":  return "Nelson Piquet Jr.";
            default:    return dn;
        }
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
    protected F1Entrant getEntrant(int year, F1Driver driver, int carNo, F1Team team) {
        return JkStreams.findUnique(model.getEntrants(year), e -> e.getDriver().equals(driver), e -> e.getCarNo() == carNo, e -> e.getTeam().equals(team));
    }

    protected JkDuration parseDuration(String str) {
        String s;
        int idx = str.lastIndexOf(".");
        if(idx != -1) {
            s = str.substring(0, idx).replace(".", ":");
            s += str.substring(idx);
        } else {
            int idx2 = str.lastIndexOf(":");
            if(idx2 != -1) {
                s = str.substring(0, idx2) + "." + str.substring(idx2);
            } else {
                s = str;
            }
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

    protected void parseGpDetails(String html, F1GranPrix gp) {
        JkTag tableGp = JkScanners.parseHtmlTag(html, "table", "<table class=\"infobox vevent\"");
        JkTag tbody = tableGp.getChild("tbody");

        F1FastLap fastLap = new F1FastLap();
        int seeker = 0;

        for (JkTag tr : tbody.getChildren("tr")) {
            if(seeker == 0) {
                // find track map image
                if(tr.getChildren().size() == 1) {
                    JkTag aTag = tr.getChild(0).getChild("a");
                    if(aTag != null && aTag.getChild("img") != null) {
                        downloadTrackMap(gp, aTag);
                        seeker++;
                    }
                }

            } else if(seeker == 1) {
                if(tr.getChildren().size() == 2) {
                    if(tr.getChild(0).getText().equals("Location")) {
                        String allText = tr.getChild(1).getHtmlTag().replaceAll("<br[^<]*?>", ",").replaceAll(",[ ]*?,", ",").replaceAll("<[^<]*?>", "");
                        List<String> list = JkStrings.splitList(allText, ",", true);
                        String nation = null;
                        String city = null;
                        if(list.size() > 2) {
                            nation = list.get(list.size() - 1);
                            city = JkStreams.join(list.subList(1, list.size() - 1), ", ");
                        } else if(list.size() == 2) {
                            nation = list.get(1);
                            city = list.get(0);
                        } else if(list.get(0).equals("Circuit de Monaco")) {
                            nation = "Monaco";
                            city = "Monte Carlo";
                        } else if(list.get(0).equals("Silverstone Circuit")) {
                            nation = "United Kingdom";
                            city = "Silverstone";
                        }
                        F1Circuit f1Circuit = retrieveCircuit(city, nation, true);
                        gp.setCircuit(f1Circuit);
                        checkNation(f1Circuit, f1Circuit.getNation());
                    } else if(tr.getChild(0).getText().equals("Course length")) {
                        String lenStr = tr.getChild(1).getText().replaceAll("[ ]*km.*", "").trim();
                        gp.setLapLength(Double.parseDouble(lenStr));
                    } else if(tr.getChild(0).getText().equals("Distance")) {
                        String numStr = tr.getChild(1).getText().replaceAll("[ ]*laps.*", "").trim();
                        gp.setNumLapsRace(Integer.parseInt(numStr));
                    } else if(tr.getChild(0).getText().equals("Date")) {
                        String attrValue = tr.getChild(1).getChild("span").getAttribute("title");
                        if(attrValue == null) {
                            attrValue = tr.getChild(1).findChild("span span").getText();
                        }
                        LocalDate date = LocalDate.parse(attrValue, DateTimeFormatter.ISO_LOCAL_DATE);
                        gp.setDate(date);
                    }
                } else if(tr.getChildren().size() == 1) {
                    if(tr.getChild(0).getTextFlat().equalsIgnoreCase("Fastest lap")) {
                        seeker++;
                    }
                }

            } else if(seeker == 2) {
                if(tr.getChild(0).getText().equals("Driver")) {
                    F1Driver d = retrieveDriver(tr.findChild("td span a").getAttribute("title"), false);
                    if(d == null && tr.findChild("td a") != null) {
                        d = retrieveDriver(tr.findChild("td a").getAttribute("title"), false);
                    }
                    if(d == null) {
                        d = retrieveDriver(tr.findChild("td").getText(), false);
                    }
                    fastLap.setDriverPK(d.getPrimaryKey());

                } else if(tr.getChild(0).getText().equals("Time")) {
                    String txt = tr.findChild("td").getText().replaceAll(" .*", "");
                    fastLap.setLapTime(parseDuration(txt));
                    gp.setFastLap(fastLap);
                    break;
                }
            }
        }
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
