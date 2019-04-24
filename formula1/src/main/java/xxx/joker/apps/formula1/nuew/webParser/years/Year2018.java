package xxx.joker.apps.formula1.nuew.webParser.years;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.formula1.nuew.model.entities.*;
import xxx.joker.apps.formula1.nuew.model.fields.F1FastLap;
import xxx.joker.apps.formula1.nuew.webParser.AWikiParser;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2018 extends AWikiParser  {


    public Year2018() {
        super(2018);
    }

    @Override
    protected void parseEntrants(String html) {
        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Entries\">", "<table class=\"wikitable sortable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        for (JkTag tr : tbody.getChildren("tr")) {
            List<JkTag> tdList = tr.getChildren("td");
            if(tdList.size() >= 8) {
                JkTag tagTeamName = tdList.get(1).findChild("b a");
                F1Team team = retrieveTeam2018(tagTeamName, true);
                if(StringUtils.isBlank(team.getNation())) {
                    JkTag img = tdList.get(0).findFirstTag("img");
                    team.setNation(img.getAttribute("alt"));
                    checkNation(team, team.getNation());
                }

                String engine = tdList.get(3).getText();

                String stmp = tdList.get(4).getHtmlTag().replaceAll("^<td(.*?)>", "").replace("</td>", "").replaceAll("<br[ ]?/>", "-");
                List<Integer> carNums = JkStreams.map(JkStrings.splitList(stmp, "-", true), Integer::valueOf);

                List<F1Driver> drivers = new ArrayList<>();
                List<JkTag> spanTags = tdList.get(5).getChildren("span");
                List<JkTag> aTags = tdList.get(5).getChildren("a");
                for(int i = 0; i < aTags.size(); i++) {
                    F1Driver d = retrieveDriver(aTags.get(i).getAttribute("title"), true);
                    if(StringUtils.isBlank(d.getNation())) {
                        JkTag img = spanTags.get(i).findFirstTag("img");
                        d.setNation(img.getAttribute("alt"));
                        checkNation(d, d.getNation());
                        parseDriverPage(d, aTags.get(i));
                    }
                    drivers.add(d);
                }

                for(int c = 0; c < drivers.size(); c++) {
                    F1Entrant e = new F1Entrant();
                    e.setYear(year);
                    e.setTeam(team);
                    e.setEngine(engine);
                    e.setCarNo(carNums.get(c));
                    e.setDriver(drivers.get(c));
                    model.getEntrants().add(e);
                }
            }
        }
    }
    private F1Team retrieveTeam2018(JkTag tag, boolean createIfMissing) {
        String teamName = tag.getText();
        if(teamName.equals("Force India")) {
            teamName = tag.getAttribute("title");
        }
        return super.retrieveTeam(teamName, createIfMissing);
    }

    @Override
    protected List<String> getGpUrls(String html) {
        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Grands_Prix\">", "<table class=\"wikitable sortable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        List<String> urls = new ArrayList<>();
        for (JkTag tr : tbody.getChildren("tr")) {
            List<JkTag> tdList = tr.getChildren("td");
            if(tdList.size() == 6) {
                JkTag a = tdList.get(5).getChild("a");
                urls.add(a.getAttribute("href"));
            }
        }

        return urls;
    }

    @Override
    protected Map<String, Integer> getExpectedDriverPoints(String html) {
        Map<String, Integer> map = new HashMap<>();

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Drivers'_Championship_standings\">", "<table class=\"wikitable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 2) {
                JkTag dTag = tr.getChild(1).findChild("a", "span a");
                F1Driver driver = retrieveDriver(dTag.getText(), false);
                String spoints = tr.getChildren("th").get(1).getText();
                int points = Integer.parseInt(spoints);
                map.put(driver.getFullName(), points);
            }
        }

        return map;
    }

    @Override
    protected Map<String, Integer> getExpectedTeamPoints(String html) {
        Map<String, Integer> map = new HashMap<>();

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Constructors'_Championship_standings\">", "<table class=\"wikitable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 2) {
                JkTag teamTag = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam2018(teamTag, false);
                String spoints = tr.getChildren("th").get(1).getText();
                spoints = spoints.replaceAll(".*\\(|\\).*", "");
                map.put(team.getTeamName(), Integer.parseInt(spoints));
            }
        }

        return map;
    }

    @Override
    protected void parseQualify(String html, F1GranPrix gp) {
        JkTag tableQualify = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying_2\">", "<table class=\"wikitable");
        if(tableQualify == null) {
            tableQualify = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying\">", "<table class=\"wikitable");
        }
        JkTag tbody = tableQualify.getChild("tbody");

        int pos = 1;

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren("td").size() == 7) {
                F1Qualify q = new F1Qualify();
                q.setGpPK(gp.getPrimaryKey());
                q.setPos(pos++);
                gp.getQualifies().add(q);

                JkTag tdDriver = tr.getChild(2);
                F1Driver driver = retrieveDriver(tdDriver.findChild("a", "span a").getText(), false);
                if(driver == null) {
                    throw new JkRuntimeException(tdDriver.getHtmlTag());
                }
                JkTag ttag = tr.getChild(3).findChild("a", "span a");
                F1Team team = retrieveTeam2018(ttag, false);
                q.setEntrant(getEntrant(year, driver, team));

                q.getTimes().add(parseDuration(tr.getChild(4).getTextFlat()));
                q.getTimes().add(parseDuration(tr.getChild(5).getTextFlat()));
                q.getTimes().add(parseDuration(tr.getChild(6).getTextFlat()));

                q.setFinalGrid(JkConvert.toInt(tr.getChild(7).getText(), -1));
            }
        }
    }

    @Override
    protected void parseRace(String html, F1GranPrix gp) {
        JkTag tableRace = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race_2\">", "<table class=\"wikitable\"");
        if(tableRace == null) {
            tableRace = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race\">", "<table class=\"wikitable");
        }
        JkTag tbody = tableRace.getChild("tbody");

        Map<Integer, F1Qualify> qualifyMap = JkStreams.toMapSingle(gp.getQualifies(), q -> q.getEntrant().getCarNo());
        int pos = 1;

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren("td").size() == 7) {
                F1Race r = new F1Race();
                r.setGpPK(gp.getPrimaryKey());
                r.setPos(pos++);
                gp.getRaces().add(r);

                r.setRetired(JkConvert.toInt(tr.getChild(0).getText()) == null);

                int carNum = Integer.parseInt(tr.getChild(1).getText());
                F1Qualify q = qualifyMap.get(carNum);
                r.setStartGrid(q.getFinalGrid());
                r.setEntrant(q.getEntrant());

                r.setLaps(Integer.parseInt(tr.getChild(4).getText()));

                r.setTime(parseDuration(tr.getChild(5).getText()));
                if(gp.getRaces().size() > 1 && r.getTime() != null) {
                    F1Race firstRace = gp.getRaces().get(0);
                    JkDuration ft = firstRace.getTime().plus(r.getTime());
                    r.setTime(ft);
                }

                r.setPoints(JkConvert.toInt(tr.getChild(7).getTextFlat(), 0));
            }
        }
    }

}
