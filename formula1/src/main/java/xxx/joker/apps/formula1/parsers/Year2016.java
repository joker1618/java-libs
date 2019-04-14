package xxx.joker.apps.formula1.parsers;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.formula1.corelibs.X_Scanners;
import xxx.joker.apps.formula1.corelibs.X_Tag;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2016 extends AWikiParser {


    public Year2016() {
        super(2016);
    }

    /**
     * Parse:
     * - entrant, all field
     * - team, name, nation, download flag icon
     * - driver, name, nation, download flag icon, web page link
     */
    @Override
    protected void parseEntrants(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Teams_and_drivers\">", "<table class=\"wikitable sortable");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            List<X_Tag> tdList = tr.getChildren("td");
            if(tdList.size() >= 7) {
                X_Tag tagTeamName = tr.getChild(1).findChild("span a", "a");
                F1Team team = retrieveTeam(tagTeamName.getText(), true);
                if(StringUtils.isBlank(team.getNation())) {
                    X_Tag img = tr.getChild(0).findFirstTag("img");
                    team.setNation(img.getAttribute("alt"));
                    super.checkField(team.getNation(), "Team nation {}", team.getTeamName());
                    super.downloadFlagIcon(img);
                }
                checkField(team.getNation(), "Nation for team {}, year {}", tagTeamName, year);

                String engine = tr.getChild(3).getText();
                if(StringUtils.isBlank(engine)) {
                    engine = tr.getChild(3).getChild("span").getText();
                }

                String stmp = tr.getChild(4).getHtmlTag().replaceAll("^<td(.*?)>", "").replace("</td>", "").replaceAll("<br[ ]?/>", "-");
                List<Integer> carNums = JkStreams.map(JkStrings.splitList(stmp, "-", true), Integer::valueOf);

                List<X_Tag> spanTags = new ArrayList<>();
                List<X_Tag> aTags = new ArrayList<>();
                List<F1Driver> drivers = new ArrayList<>();
                for (X_Tag child : tr.getChild(5).getChildren()) {
                    if(child.getTagName().equals("span") && child.getAttribute("class").equals("nowrap")) {
                        spanTags.add(child.getChild("span"));
                        aTags.add(child.getChild("a"));
                    } else if(child.getTagName().equals("span")) {
                        spanTags.add(child);
                    } else if(child.getTagName().equals("a")) {
                        aTags.add(child);
                    }
                }

                for(int i = 0; i < aTags.size(); i++) {
                    F1Driver d = retrieveDriver(aTags.get(i).getAttribute("title"), true);
                    if(StringUtils.isBlank(d.getNation())) {
                        X_Tag img = spanTags.get(i).findFirstTag("img");
                        d.setNation(img.getAttribute("alt"));
                        super.checkField(d.getNation(), "Driver nation {}", d.getFullName());
                        super.addDriverLink(d, aTags.get(i));
                        super.downloadFlagIcon(img);
                    }
                    checkField(d.getNation(), "Nation for driver {}, year {}", d.getFullName(), year);
                    drivers.add(d);
                }

                for(int c = 0; c < drivers.size(); c++) {
                    F1Entrant e = new F1Entrant();
                    e.setYear(year);
                    e.setTeam(team);
                    e.setEngine(engine);
                    e.setCarNo(carNums.get(c));
                    e.setDriver(drivers.get(c));
                    model.add(e);
                }
            }
        }
    }

    @Override
    protected List<String> getGpUrls(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Grands_Prix\">", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        List<String> urls = new ArrayList<>();
        for (X_Tag tr : tbody.getChildren("tr")) {
            List<X_Tag> tdList = tr.getChildren("td");
            if(tdList.size() == 6) {
                X_Tag a = tdList.get(5).getChild("a");
                urls.add(super.createWikiUrl(a));

                X_Tag img = tdList.get(0).findFirstTag("img");
                super.downloadFlagIcon(img);
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

        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Constructors'_Championship_standings\">", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1) {
                X_Tag teamTag = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam(teamTag.getText(), false);
                String spoints = JkStreams.getLastElem(tr.getChildren("td")).getChild("b").getText();
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

        int counterFastLast = -1;
        F1FastLap fastLap = new F1FastLap();
        gp.setFastLap(fastLap);

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(counterFastLast == 2) {
                F1Driver d = retrieveDriver(tr.findChild("td span a").getAttribute("title"), false);
                if(d == null) {
                    d = retrieveDriver(tr.findChild("td a").getAttribute("title"), false);
                }
                fastLap.setDriverPK(d.getPrimaryKey());
                counterFastLast--;

            } else if(counterFastLast == 1) {
                String txt = tr.findChild("td").getText().replaceAll(" .*", "");
                fastLap.setLapTime(JkDuration.of(txt));
                counterFastLast--;

            } else if(tr.findChild("td a img") != null) {
                X_Tag img = tr.findChild("td a img");
                downloadTrackMap(gp, img);

            } else if(counterFastLast == -1 && tr.getChildren().size() == 1 && tr.findChild("th a") != null) {
                X_Tag tag = tr.findChild("th a");
                if (tag.getText().equals("Fastest lap")) {
                    counterFastLast = 2;
                }

            } else if(tr.getChildren().size() == 2) {
                if(tr.getChild(0).getText().equals("Location")) {
                    String allText = tr.getChild(1).getHtmlTag().replaceAll("<br[^<]*?>", ",").replaceAll(",[ ]*?,", ",").replaceAll("<[^<]*?>", "");
                    List<String> list = JkStrings.splitList(allText, ",", true);
                    String nation = list.get(list.size() - 1);
                    String city = JkStreams.join(list.subList(1, list.size() - 1), ", ");
                    F1Circuit f1Circuit = super.retrieveCircuit(city, nation, true);
                    gp.setCircuit(f1Circuit);
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
                F1Team team = retrieveTeam(ttag.getText(), false);
                q.setEntrant(getEntrant(year, carNum, team));

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

        Map<Integer, F1Qualify> qualifyMap = JkStreams.toMapSingle(gp.getQualifies(), q -> q.getEntrant().getCarNo());
        int pos = 1;

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren("td").size() == 7) {
                F1Race r = new F1Race();
                r.setGpPK(gp.getPrimaryKey());
                r.setPos(pos++);
                gp.getRaces().add(r);

                r.setRetired(JkConvert.toInt(tr.getChild(0).getText()) == null);

                int carNum = Integer.parseInt(tr.getChild(1).getText());
                F1Qualify q = qualifyMap.get(carNum);
                r.setEntrant(q.getEntrant());

                r.setLaps(Integer.parseInt(tr.getChild(4).getText()));

                r.setTime(JkDuration.of(tr.getChild(5).getText().replace("&#160;", "")));
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
