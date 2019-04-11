package xxx.joker.apps.formula1.parsers;

import static xxx.joker.apps.formula1.common.F1Const.*;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.corelibs.X_Scanners;
import xxx.joker.apps.formula1.corelibs.X_Tag;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.web.JkDownloader;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

abstract class AWikiParser implements WikiParser {

    public static final Logger LOG = LoggerFactory.getLogger(AWikiParser.class);

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private JkDownloader htmlDownloader;
    protected int year;
    protected F1Model model;

    protected AWikiParser(int year) {
        this.year = year;
        this.htmlDownloader = new JkDownloader(HTML_FOLDER);
        this.model = F1ModelImpl.getInstance();
    }

    protected abstract void parseEntrants(String html);
    protected abstract List<String> getGpUrls(String html);
    protected abstract Map<String, Integer> getExpectedDriverPoints(String html);
    protected abstract Map<String, Integer> getExpectedTeamPoints(String html);

    protected abstract void parseGpDetails(String html, F1GranPrix gp);
    protected abstract void parseQualify(String html, F1GranPrix gp);
    protected abstract void parseRace(String html, F1GranPrix gp);


    @Override
    public void parse() {
        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        parseEntrants(htmlDownloader.getHtml(mainPageUrl));

        model.getDrivers().forEach(this::parseDriverPage);

//        Map<String, Integer> expDriverMap = getExpectedDriverPoints(htmlDownloader.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesDriverMap = JkStreams.sorted(expDriverMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strDrivers = JkStreams.join(entriesDriverMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected driver points ({})\n{}", entriesDriverMap.size(), strDrivers);
//
//        Map<String, Integer> expTeamMap = getExpectedTeamPoints(htmlDownloader.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesTeamMap = JkStreams.sorted(expTeamMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strTeams  = JkStreams.join(entriesTeamMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected team points ({})\n{}", entriesTeamMap.size(), strTeams);

        List<String> gpUrls = getGpUrls(htmlDownloader.getHtml(mainPageUrl));
        for (int i = 0; i < gpUrls.size(); i++) {
            String html = htmlDownloader.getHtml(gpUrls.get(i));
            F1GranPrix gp = new F1GranPrix(year, i + 1);
            if(model.getGranPrixs().add(gp)) {
                parseGpDetails(html, gp);
                display(gp.getNation());
                parseQualify(html, gp);
                parseRace(html, gp);
//                if(i == 0) break;
//                break;
            }
        }
    }

    private void parseDriverPage(F1Driver driver) {
        if(driver.getBirthDate() == null) {
            display(driver.getFullName());

            F1Link dlink = JkStreams.findUnique(model.getLinks(), l -> l.getKey().equals("driver." + driver.getEntityID()));

            String html = htmlDownloader.getHtml(dlink.getUrl());

            X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<table class=\"infobox");
            X_Tag tbody = tableEntrants.getChild("tbody");

            JkDownloader dw = new JkDownloader(IMG_DRIVER_PIC_FOLDER);

            X_Tag img = null;
            List<X_Tag> trList = tbody.getChildren("tr");
            int rowNum = 0;
            while(img == null && rowNum < trList.size()) {
                img = tbody.getChild(rowNum).findFirstTag("img");
                rowNum++;
            }
            String picUrl = createResourceUrl(img);
            String imgFilename = driver.getPrimaryKey();
            dw.downloadResource(imgFilename, picUrl);

            X_Tag rowBorn = null;
            while(rowBorn == null && rowNum < trList.size()) {
                X_Tag tr = tbody.getChild(rowNum);
                X_Tag ch = tr.getChild(0);
                if(ch.getTagName().equals("th") && "Born".equals(ch.getText())) {
                    rowBorn = tr;
                }
                rowNum++;
            }

            String strBirth = rowBorn.getChild(1).findChild("span span").getText();
            driver.setBirthDate(LocalDate.parse(strBirth));
            checkField(driver.getBirthDate(), "No birth date for {}", driver);

            String[] split = rowBorn.getHtmlTag().split("<br[ ]?/>");
            String strCity = split[split.length - 1].replaceAll("<[^<]*?>", "").replaceAll(",[^,]*$", "").trim();
            driver.setBirthCity(strCity);
            checkField(driver.getBirthCity(), "No birth city for {}", driver);
        }
    }

    @Override
    public Map<String, Integer> getExpectedDriverPoints() {
        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return getExpectedDriverPoints(htmlDownloader.getHtml(mainPageUrl));

    }

    @Override
    public Map<String, Integer> getExpectedTeamPoints() {
        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return getExpectedTeamPoints(htmlDownloader.getHtml(mainPageUrl));
    }

    protected String createUrl(String wikiSubPath) {
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createUrl(X_Tag aTag) {
        return createUrl(aTag.getAttribute("href"));
    }

    protected void addDriverLink(F1Driver driver, X_Tag aTag) {
        String key = strf("driver.{}", driver.getEntityID());
        String url = createUrl(aTag.getAttribute("href"));
        model.add(new F1Link(key, url));
    }
    protected void addFlagIconLink(X_Tag img) {
        String key = strf("nation.icon.{}", img.getAttribute("alt"));
        String url = createResourceUrl(img);
        model.add(new F1Link(key, url));
    }
    private String createResourceUrl(X_Tag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }

    protected void persistGpTrackMap(F1GranPrix gp, X_Tag img) {
        JkDownloader dw = new JkDownloader(IMG_TRACK_MAP_FOLDER);
        String url = createResourceUrl(img);
        dw.downloadResource(gp.getPrimaryKey(), url);
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
    protected F1Driver retrieveDriver(String driverName, boolean createIfMissing) {
        F1Driver driver = model.getDriver(driverName.replace("(racing driver)", "").trim());
        if(driver == null && createIfMissing) {
            driver = new F1Driver(driverName);
            model.getDrivers().add(driver);
        }
        return driver;
    }

    protected F1Entrant getEntrant(int year, int carNum, F1Team team) {
        return JkStreams.findUnique(model.getEntrants(year), e -> e.getCarNo() == carNum, e -> e.getTeam().equals(team));
    }

    protected String fixTeamName(String teamName) {
        if("Scuderia Toro Rosso".equals(teamName)){
            return "Toro Rosso";
        }
        return teamName;
    }

    protected void checkField(Object o, String mex, Object... params) {
        if(o == null) {
            throw new JkRuntimeException("Null value found: " + mex, params);
        }
        if(o instanceof String && StringUtils.isBlank((String)o)) {
            throw new JkRuntimeException("Blank string found: " + mex, params);
        }
    }

}
