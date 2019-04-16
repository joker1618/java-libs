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

public class Year2011 extends AWikiParser {


    public Year2011() {
        super(2011);
    }

    /**
     * Parse:
     * - entrant, all field
     * - team, name, nation, download flag icon
     * - driver, name, nation, download flag icon, web page link
     */
    @Override
    protected void parseEntrants(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Teams_and_drivers\">", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        F1Entrant previous = null;
        for (X_Tag tr : tbody.getChildren("tr")) {
            List<X_Tag> tdList = tr.getChildren("td");

            if(tdList.size() == tr.getChildren().size() && tdList.size() == 2) {
                X_Tag spanTag;
                X_Tag aTag;
                X_Tag firstTd = tr.getChild(0);
                X_Tag chTag = firstTd.getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = firstTd.getChild("a");
                    if(aTag == null) {
                        aTag = firstTd.getChild("span", "class=nowrap").getChild("a");
                    }
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    X_Tag img = spanTag.findFirstTag("img");
                    d.setNation(img.getAttribute("alt"));
                    super.checkField(d.getNation(), "Driver nation {}", d.getFullName());
                    super.addDriverLink(d, aTag);
                    super.downloadFlagIcon(img);
                }
                checkField(d.getNation(), "Nation for driver {}, year {}", d.getFullName(), year);

                F1Entrant e = new F1Entrant();
                e.setYear(year);
                e.setTeam(previous.getTeam());
                e.setEngine(previous.getEngine());
                e.setCarNo(previous.getCarNo());
                e.setDriver(d);
                model.add(e);

            } else if(tdList.size() == tr.getChildren().size() && tdList.size() == 3) {
                int carNum = Integer.valueOf(tr.getChild(0).getText());

                X_Tag spanTag;
                X_Tag aTag;
                X_Tag chTag = tr.getChild(1).getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = tr.getChild(1).getChild("a");
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    X_Tag img = spanTag.findFirstTag("img");
                    d.setNation(img.getAttribute("alt"));
                    super.checkField(d.getNation(), "Driver nation {}", d.getFullName());
                    super.addDriverLink(d, aTag);
                    super.downloadFlagIcon(img);
                }
                checkField(d.getNation(), "Nation for driver {}, year {}", d.getFullName(), year);

                F1Entrant e = new F1Entrant();
                e.setYear(year);
                e.setTeam(previous.getTeam());
                e.setEngine(previous.getEngine());
                e.setCarNo(carNum);
                e.setDriver(d);
                model.add(e);

            } else if(tdList.size() >= 7) {
                X_Tag tagTeamName = tr.getChild(1).findChild("a");
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
                if(engine.equals("Renault EnergyF1-2014")) {
                    engine = "Renault Energy F1-2014";
                }

                int carNum = Integer.valueOf(tr.getChild(4).getText());

                X_Tag spanTag;
                X_Tag aTag;
                X_Tag chTag = tr.getChild(5).getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = tr.getChild(5).getChild("a");
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    X_Tag img = spanTag.findFirstTag("img");
                    d.setNation(img.getAttribute("alt"));
                    super.checkField(d.getNation(), "Driver nation {}", d.getFullName());
                    super.addDriverLink(d, aTag);
                    super.downloadFlagIcon(img);
                }
                checkField(d.getNation(), "Nation for driver {}, year {}", d.getFullName(), year);

                F1Entrant e = new F1Entrant();
                e.setYear(year);
                e.setTeam(team);
                e.setEngine(engine);
                e.setCarNo(carNum);
                e.setDriver(d);
                model.add(e);

                previous = e;
            }
        }
    }

    @Override
    protected List<String> getGpUrls(String html) {
        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Grands_Prix\">", "<table class=\"wikitable");
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

        X_Tag tableEntrants = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Drivers'_Championship_standings\">"
                , "<table class=\"wikitable\"", "<table>", "<table class=\"wikitable\"");
        X_Tag tbody = tableEntrants.getChild("tbody");

        for (X_Tag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 2) {
                X_Tag dTag = tr.getChild(1).findChild("a", "span a");
                F1Driver driver = retrieveDriver(dTag.getText(), false);
                String spoints = JkStreams.getLastElem(tr.getChildren()).getText();
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
            if(tr.getChildren("th").size() <= 2 && tr.getChild(0).getTagName().equals("th")) {
                X_Tag teamTag = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam(teamTag.getText(), false);
                X_Tag last = JkStreams.getLastElem(tr.getChildren());
                String spoints = last.getTagName().equals("th") ? last.getText() : last.getChild("b").getText();
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
                fastLap.setLapTime(parseDuration(txt));
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
                    if(tr.getChild(1).getChildren().size() == 1) {
                        String tdText = tr.findChild("td a").getText();
                        if(tdText.equals("Circuit de Monaco")) {
                            F1Circuit f1Circuit = super.retrieveCircuit("Monte Carlo", "Monaco", true);
                            gp.setCircuit(f1Circuit);
                        } else if(tdText.equals("Autódromo José Carlos Pace")) {
                            F1Circuit f1Circuit = super.retrieveCircuit("São Paulo", "Brazil", true);
                            gp.setCircuit(f1Circuit);
                        }
                    } else {
                        String allText = tr.getChild(1).getHtmlTag().replaceAll("<br[^<]*?>", ",").replaceAll(",[ ]*?,", ",").replaceAll("<[^<]*?>", "");
                        List<String> list = JkStrings.splitList(allText, ",", true);
                        String nation = list.get(list.size() - 1);
                        String city = JkStreams.join(list.subList(1, list.size() - 1), ", ");
                        F1Circuit f1Circuit = super.retrieveCircuit(city, nation, true);
                        gp.setCircuit(f1Circuit);
                    }
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
            int tdNum = tr.getChildren("td").size();
            if(tr.getChildren("th").size() == 1 && (tdNum >= 6 && tdNum <= 8)) {
                F1Qualify q = new F1Qualify();
                q.setGpPK(gp.getPrimaryKey());
                q.setPos(pos++);
                gp.getQualifies().add(q);

                int counter = 4;

                F1Driver d = super.retrieveDriver(tr.getChild(2).findChild("a").getText(), false);
                F1Entrant entrant = JkStreams.findUnique(model.getEntrants(year), e -> e.getDriver().equals(d));
                q.setEntrant(entrant);

                q.getTimes().add(parseDuration(tr.getChild(counter++).getTextFlat()));
                q.getTimes().add(parseDuration(tr.getChild(counter++).getTextFlat()));
                q.getTimes().add(parseDuration(tr.getChild(counter++).getTextFlat()));

                q.setFinalGrid(JkConvert.toInt(tr.getChild(counter).getText(), -1));
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

        Map<String, F1Qualify> qualifyMap = JkStreams.toMapSingle(gp.getQualifies(), q -> q.getEntrant().getDriver().getFullName());
        int pos = 1;

        for (X_Tag tr : tbody.getChildren("tr")) {
            int tdNum = tr.getChildren("td").size();
            if(tr.getChildren("th").size() == 1 && (tdNum == 7 || tdNum == 8)) {
                F1Race r = new F1Race();
                r.setGpPK(gp.getPrimaryKey());
                r.setPos(pos++);
                gp.getRaces().add(r);

                r.setRetired(JkConvert.toInt(tr.getChild(0).getText()) == null);

                String driverName = tr.getChild(2).findChild("a", "b a").getText();
                F1Qualify q = qualifyMap.get(driverName);
                r.setEntrant(q.getEntrant());

                int counter = tdNum == 7 ? 4 : 5;

                r.setLaps(Integer.parseInt(tr.getChild(counter++).getText()));

                r.setTime(parseDuration(tr.getChild(counter++).getText()));
                if(gp.getRaces().size() > 1 && r.getTime() != null) {
                    F1Race firstRace = gp.getRaces().get(0);
                    JkDuration ft = firstRace.getTime().plus(r.getTime());
                    r.setTime(ft);
                }

                counter++;
                r.setPoints(JkConvert.toInt(tr.getChild(counter).getTextFlat(), 0));
            }
        }
    }


}
