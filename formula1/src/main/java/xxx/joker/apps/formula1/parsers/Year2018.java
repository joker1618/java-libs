package xxx.joker.apps.formula1.parsers;

import xxx.joker.apps.formula1.corelibs.X_Scanners;
import xxx.joker.apps.formula1.corelibs.X_Tag;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Year2018 extends AWikiParser {


    public Year2018() {
        super(2018);
    }

    @Override
    protected void parseEntrants(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Entries\">", "<table class=\"wikitable sortable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            List<X_Tag> tdList = tr.getChildren("td");
            if(tdList.size() >= 8) {
                X_Tag tagTeamName = tdList.get(1).findChild("b a");
                String teamName = tagTeamName.getText();
                F1Team team = retrieveTeam(teamName, true);
                String engine = tdList.get(3).getText();

                String stmp = tdList.get(4).getHtmlTag().replaceAll("^<td(.*?)>", "").replace("</td>", "").replaceAll("<br[ ]?/>", "-");
                List<Integer> carNums = JkStreams.map(JkStrings.splitList(stmp, "-", true), Integer::valueOf);

                List<F1Driver> drivers = new ArrayList<>();
                tdList.get(5).getChildren("a").forEach(t -> {
                    F1Driver d = retrieveDriver(t.getText(), true);
                    drivers.add(d);
                    String key = d.getEntityID()+"";
                    String url = createUrl(t.getAttribute("href"));
                    model.getLinks().add(new F1Link(key, url));
                });

                for(int c = 0; c < drivers.size(); c++) {
                    F1Entrant e = new F1Entrant();
                    e.setYear(year);
                    e.setTeam(team);
                    e.setEngine(engine);
                    e.setCarNum(carNums.get(c));
                    e.setDriver(drivers.get(c));
                    model.getEntrants().add(e);
                }
            }
        }
    }

    @Override
    protected List<String> getGpUrls(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Grands_Prix\">", "<table class=\"wikitable sortable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        List<String> urls = new ArrayList<>();
        for (X_Tag tr : tbody.getChildren("tr")) {
            List<X_Tag> tdList = tr.getChildren("td");
            if(tdList.size() == 6) {
                String href = tdList.get(5).getChild("a").getAttribute("href");
                urls.add(createUrl(href));
            }
        }

        return urls;
    }

    @Override
    protected Map<String, Integer> getExpectedDriverPoints(String html) {
        Map<String, Integer> map = new HashMap<>();

        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Drivers'_Championship_standings\">", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 2) {
                X_Tag dTag = tr.getChild(1).findChild("a", "span a");
                F1Driver driver = model.getDriver(dTag.getText());
                String spoints = tr.getChildren("th").get(1).getText();
                int points = Integer.parseInt(spoints);
                map.put(driver.getDriverName(), points);
            }
        }

        return map;
    }

    @Override
    protected Map<String, Integer> getExpectedTeamPoints(String html) {
        Map<String, Integer> map = new HashMap<>();

        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Constructors'_Championship_standings\">", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 2) {
                X_Tag teamTag = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam(teamTag.getText(), false);
                String spoints = tr.getChildren("th").get(1).getText();
                spoints = spoints.replaceAll(".*\\(", "").replaceAll("\\).*", "");
                int points = Integer.parseInt(spoints);
                map.put(team.getTeamName(), points);
            }
        }

        return map;
    }

    @Override
    protected void parseGpDetails(String html, F1GranPrix gp) {
        X_Tag tableGp = X_Scanners.parseHtmlTag(html, "table", "<table class=\"infobox vevent\"");
        X_Tag tbody = tableGp.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren().size() == 2) {
                if(tr.getChild(0).getText().equals("Location")) {
                    String allText = tr.getChild(1).getHtmlTag().replaceAll("<br[^<]*?>", ",").replaceAll(",[ ]*?,", ",").replaceAll("<[^<]*?>", "");
                    List<String> list = JkStrings.splitList(allText, ",", true);
                    gp.setNation(list.get(list.size() - 1));
                    gp.setCity(JkStreams.join(list.subList(1, list.size() - 1), ", "));
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
            }
        }
    }

    @Override
    protected void parseQualify(String html, F1GranPrix gp) {
        X_Tag tableQualify = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying_2\">", "<table class=\"wikitable");
        if(tableQualify == null) {
            tableQualify = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying\">", "<table class=\"wikitable");
        }
        X_Tag tbody = tableQualify.getChild("tbody");

        int pos = 1;

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren("td").size() == 7) {
                F1Qualify q = new F1Qualify();
                q.setGpPK(gp.getPrimaryKey());
                q.setPos(pos++);
                gp.getQualifies().add(q);

                int carNum = Integer.parseInt(tr.getChild(1).getText());
                X_Tag ttag = tr.getChild(3).findChild("a", "span a");
                String strTeam = ttag.getText();
                F1Team team = retrieveTeam(strTeam, false);
                q.setEntrant(getEntrant(year, carNum, team));

//                display("{} - {} - {}", gp.getPrimaryKey(), carNum, q.getEntrant());

                q.getTimes().add(JkDuration.of(tr.getChild(4).getTextFlat()));
                q.getTimes().add(JkDuration.of(tr.getChild(5).getTextFlat()));
                q.getTimes().add(JkDuration.of(tr.getChild(6).getTextFlat()));

                q.setFinalGrid(JkConvert.toInt(tr.getChild(7).getText(), -1));
            }
        }
    }

    @Override
    protected void parseRace(String html, F1GranPrix gp) {
        X_Tag tableRace = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race_2\">", "<table class=\"wikitable\"");
        if(tableRace == null) {
            tableRace = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race\">", "<table class=\"wikitable");
        }
        X_Tag tbody = tableRace.getChild("tbody");

        Map<Integer, F1Qualify> qualifyMap = JkStreams.toMapSingle(gp.getQualifies(), q -> q.getEntrant().getCarNum());
        int pos = 1;

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren("td").size() == 7) {
                F1Race r = new F1Race();
                r.setGpPK(gp.getPrimaryKey());
                r.setPos(pos++);
                gp.getRaces().add(r);

                r.setRetired(tr.getChild(0).getText().equalsIgnoreCase("Ret"));

                int carNum = Integer.parseInt(tr.getChild(1).getText());
                F1Qualify q = qualifyMap.get(carNum);
                r.setEntrant(q.getEntrant());

                r.setLaps(Integer.parseInt(tr.getChild(4).getText()));

                r.setTime(JkDuration.of(tr.getChild(5).getText()));
                if(gp.getRaces().size() > 1 && r.getTime() != null) {
                    F1Race firstRace = gp.getRaces().get(0);
                    JkDuration ft = firstRace.getTime().plus(r.getTime());
                    r.setTime(ft);
                }

                // Check grid pos against final grid pos in qualify (must be equals)
                int gridPos = JkConvert.toInt(tr.getChild(6).getText(), -1);
                if(q.getFinalGrid() != gridPos) {
                    throw new JkRuntimeException("GP {}, driver {}: mismatch between qualify grid pos ({}) and race grid pos ({})",
                            gp.getPrimaryKey(), q.getEntrant().getDriver().getDriverName(), q.getFinalGrid(), gridPos
                    );
                }

                r.setPoints(JkConvert.toInt(tr.getChild(7).getTextFlat(), 0));
            }
        }
    }



}
