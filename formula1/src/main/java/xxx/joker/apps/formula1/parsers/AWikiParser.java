package xxx.joker.apps.formula1.parsers;

import static xxx.joker.apps.formula1.common.F1Const.*;
import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConsole;
import xxx.joker.libs.core.web.JkDownloader;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class AWikiParser implements IWikiParser {

    public static final Logger LOG = LoggerFactory.getLogger(AWikiParser.class);

    public static final String PREFIX_URL = "https://en.wikipedia.org";

    private JkDownloader downloader;
    protected int year;
    protected F1Model model;

    protected AWikiParser(int year) {
        this.year = year;
        this.downloader = new JkDownloader(HTML_FOLDER);
        this.model = F1ModelImpl.getInstance();
    }

    @Override
    public void parse() {
        model.getDataSets().values().forEach(Set::clear);

        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        parseEntrants(downloader.getHtml(mainPageUrl));
//
//        Map<String, Integer> expDriverMap = getExpectedDriverPoints(downloader.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesDriverMap = JkStreams.sorted(expDriverMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strDrivers = JkStreams.join(entriesDriverMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected driver points ({})\n{}", entriesDriverMap.size(), strDrivers);
//
//        Map<String, Integer> expTeamMap = getExpectedTeamPoints(downloader.getHtml(mainPageUrl));
//        List<Map.Entry<String, Integer>> entriesTeamMap = JkStreams.sorted(expTeamMap.entrySet(), Comparator.comparing(Map.Entry::getValue));
//        String strTeams  = JkStreams.join(entriesTeamMap, "\n", w -> strf("  %-5d%s", w.getValue(), w.getKey()));
//        display("*** Expected team points ({})\n{}", entriesTeamMap.size(), strTeams);

        // Clear all gran prix for year
//        model.getDataSet(F1Qualify.class).clear();
//        model.getDataSet(F1Race.class).clear();
//        model.getDataSet(F1GranPrix.class).clear();

        List<String> gpUrls = getGpUrls(downloader.getHtml(mainPageUrl));
        for (int i = 0; i < gpUrls.size(); i++) {
            String html = downloader.getHtml(gpUrls.get(i));
            F1GranPrix gp = new F1GranPrix(year, i + 1);
            if(model.getGranPrixs().add(gp)) {
                parseGpDetails(html, gp);
                display(gp.getNation());
                parseQualify(html, gp);
                parseRace(html, gp);
//                if(i == 16) break;
            }
        }
    }

    @Override
    public Map<String, Integer> getExpectedDriverPoints() {
        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return getExpectedDriverPoints(downloader.getHtml(mainPageUrl));

    }

    @Override
    public Map<String, Integer> getExpectedTeamPoints() {
        String mainPageUrl = createUrl(strf("/wiki/{}_Formula_One_World_Championship", year));
        return getExpectedTeamPoints(downloader.getHtml(mainPageUrl));
    }

    protected abstract void parseEntrants(String html);
    protected abstract List<String> getGpUrls(String html);
    protected abstract Map<String, Integer> getExpectedDriverPoints(String html);
    protected abstract Map<String, Integer> getExpectedTeamPoints(String html);

    protected abstract void parseGpDetails(String html, F1GranPrix gp);
    protected abstract void parseQualify(String html, F1GranPrix gp);
    protected abstract void parseRace(String html, F1GranPrix gp);

    protected String createUrl(String wikiSubPath) {
        return strf("{}/{}", PREFIX_URL, wikiSubPath.replaceFirst("^/", ""));
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
        F1Driver driver = model.getDriver(driverName);
        if(driver == null && createIfMissing) {
            driver = new F1Driver(driverName);
            model.getDrivers().add(driver);
        }
        return driver;
    }

    protected F1Entrant getEntrant(int year, int carNum, F1Team team) {
        return JkStreams.findUnique(model.getEntrants(year), e -> e.getCarNum() == carNum, e -> e.getTeam().equals(team));
    }

    public String fixTeamName(String teamName) {
        if("Scuderia Toro Rosso".equals(teamName)){
            return "Toro Rosso";
        }
        return teamName;
    }
}
