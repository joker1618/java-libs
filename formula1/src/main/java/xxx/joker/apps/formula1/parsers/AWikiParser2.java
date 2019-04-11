package xxx.joker.apps.formula1.parsers;

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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static xxx.joker.apps.formula1.common.F1Const.*;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

abstract class AWikiParser2 implements WikiParser {

    public static final Logger LOG = LoggerFactory.getLogger(AWikiParser2.class);

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private JkDownloader dwHtml;
    protected int year;
    protected F1Model model;

    protected AWikiParser2(int year) {
        this.year = year;
        this.dwHtml = new JkDownloader(HTML_FOLDER);
        this.model = F1ModelImpl.getInstance();
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

        model.getDrivers().forEach(this::parseDriverPage);

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
            String html = dwHtml.getHtml(gpUrls.get(i));
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

    @Override
    public Map<String, Integer> getExpectedDriverPoints() {
        return getExpectedDriverPoints(getMainPageHtml());

    }

    @Override
    public Map<String, Integer> getExpectedTeamPoints() {
        return getExpectedTeamPoints(getMainPageHtml());
    }

    protected void addDriverLink(F1Driver driver, X_Tag aTag) {
        String key = strf("driver.{}", driver.getEntityID());
        String url = createWikiUrl(aTag);
        model.add(new F1Link(key, url));
    }

    protected void downloadFlagIcon(X_Tag img) {
        String url = createResourceUrl(img);
        String fn = createResourceFilename(img.getAttribute("alt"), url);
        if(new JkDownloader(IMG_FLAGS_ICON_FOLDER).downloadResource(fn, url)) {
            LOG.debug("Downloaded flag icon for {}", fn);
        }
    }
    protected void downloadTrackMap(F1GranPrix gp, X_Tag img) {
        String url = createResourceUrl(img);
        String fn = createResourceFilename(gp.getPrimaryKey(), url);
        if(new JkDownloader(IMG_TRACK_MAP_FOLDER).downloadResource(fn, url)) {
            LOG.debug("Downloaded flag icon for {}", fn);
        }
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
        String dname = driverName.replace("(racing driver)", "").trim();
        F1Driver driver = model.getDriver(dname);
        if(driver == null && createIfMissing) {
            driver = new F1Driver(dname);
            model.getDrivers().add(driver);
        }
        return driver;
    }

    protected F1Entrant getEntrant(int year, int carNum, F1Team team) {
        return JkStreams.findUnique(model.getEntrants(year), e -> e.getCarNo() == carNum, e -> e.getTeam().equals(team));
    }

    protected void checkFields(String mex, Object... objs) {
        Arrays.stream(objs).forEach(o -> checkField(o, mex));
    }
    protected void checkField(Object o, String mex, Object... params) {
        if(o == null) {
            throw new JkRuntimeException("Null value found: " + mex, params);
        }
        if(o instanceof String && StringUtils.isBlank((String)o)) {
            throw new JkRuntimeException("Blank string found: " + mex, params);
        }
    }

    protected String createWikiUrl(String wikiSubPath) {
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
    }
    protected String createWikiUrl(X_Tag aTag) {
        return createWikiUrl(aTag.getAttribute("href"));
    }

    private String createResourceUrl(X_Tag img) {
        return strf("https:{}", img.getAttribute("srcset").replaceAll(" [^ ]+$", "").replaceAll(".*,", "").trim());
    }
    private String createResourceFilename(String fn, String url) {
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

    private String getMainPageHtml() {
        String mainPageUrl = createWikiUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return dwHtml.getHtml(mainPageUrl);
    }

    private String fixTeamName(String teamName) {
        if("Scuderia Toro Rosso".equals(teamName)){
            return "Toro Rosso";
        }
        return teamName;
    }

    /**
     * Parse:
     * - driver, birth day, birth city, download pic
     */
    private void parseDriverPage(F1Driver driver) {
        if(driver.getBirthDate() == null) {
//            display(driver.getCity());

            F1Link dlink = JkStreams.findUnique(model.getLinks(), l -> l.getKey().equals("driver." + driver.getEntityID()));

            String html = dwHtml.getHtml(dlink.getUrl());

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
            String imgFilename = createResourceFilename(driver.getPrimaryKey(), picUrl);
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




}
