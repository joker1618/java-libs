package xxx.joker.apps.formula1.webParser.years;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.apps.formula1.webParser.AWikiParser;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.scanners.JkScanners;
import xxx.joker.libs.core.scanners.JkTag;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Year2003 extends AWikiParser {


    public Year2003() {
        super(2003);
    }

    @Override
    protected void parseEntrants(String html) {
        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Teams_and_drivers\">", "<table class=\"wikitable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        F1Entrant previous = null;
        for (JkTag tr : tbody.getChildren("tr")) {
            List<JkTag> tdList = tr.getChildren("td");

            if(tdList.size() == tr.getChildren().size() && tdList.size() == 2) {
                JkTag spanTag;
                JkTag aTag;
                JkTag chTag = tr.getChild(0).getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = tr.getChild(0).getChild("a");
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    JkTag img = spanTag.findFirstTag("img");
                    d.setNation(fixNation(img.getAttribute("alt")));
                    checkNation(d, d.getNation());
                    parseDriverPage(d, aTag);
                }

                F1Entrant e = new F1Entrant();
                e.setYear(year);
                e.setTeam(previous.getTeam());
                e.setEngine(previous.getEngine());
                e.setCarNo(previous.getCarNo());
                e.setDriver(d);
                model.add(e);

                previous = e;

            } else if(tdList.size() == tr.getChildren().size() && tdList.size() == 3) {
                int carNum = Integer.valueOf(tr.getChild(0).getText());

                JkTag spanTag;
                JkTag aTag;
                JkTag chTag = tr.getChild(1).getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = tr.getChild(1).getChild("a");
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    JkTag img = spanTag.findFirstTag("img");
                    d.setNation(fixNation(img.getAttribute("alt")));
                    checkNation(d, d.getNation());
                    parseDriverPage(d, aTag);
                }

                F1Entrant e = new F1Entrant();
                e.setYear(year);
                e.setTeam(previous.getTeam());
                e.setEngine(previous.getEngine());
                e.setCarNo(carNum);
                e.setDriver(d);
                model.add(e);

                previous = e;

            } else if(tdList.size() >= 6) {
                JkTag tagTeamName = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam(tagTeamName.getText(), true);
                if(StringUtils.isBlank(team.getNation())) {
                    JkTag img = tr.getChild(0).findFirstTag("img");
                    team.setNation(fixNation(img.getAttribute("alt")));
                    checkNation(team, team.getNation());
                }

                String engine = tr.getChild(3).getText();
                if(StringUtils.isBlank(engine)) {
                    engine = tr.getChild(3).getChild("span").getText();
                }

                int carNum = Integer.valueOf(tr.getChild(5).getText());

                JkTag spanTag;
                JkTag aTag;
                JkTag chTag = tr.getChild(6).getChild("span");
                if(chTag.getAttribute("class").equals("nowrap")) {
                    spanTag = chTag.getChild("span");
                    aTag = chTag.getChild("a");
                } else  {
                    spanTag = chTag;
                    aTag = tr.getChild(6).getChild("a");
                }

                F1Driver d = retrieveDriver(aTag.getAttribute("title"), true);
                if(StringUtils.isBlank(d.getNation())) {
                    JkTag img = spanTag.findFirstTag("img");
                    d.setNation(fixNation(img.getAttribute("alt")));
                    checkNation(d, d.getNation());
                    parseDriverPage(d, aTag);
                }

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

//        List<F1Entrant> elist = model.getEntrants(year);
//        System.out.println(elist.size()+"");
//        System.out.println(RepoUtil.formatEntities(elist));
//        System.exit(1);

    }

    @Override
    protected List<String> getGpUrls(String html) {
        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Grands_Prix\">", "<table class=\"wikitable");
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
    protected Map<String, Double> getExpectedDriverPoints(String html) {
        Map<String, Double> map = new HashMap<>();

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Drivers'_Championship_standings\">"
                , "<table>", "<table class=\"wikitable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren().get(0).getTagName().equals("th")) {
                JkTag dTag = tr.getChild(1).findChild("a", "span a");
                F1Driver driver = retrieveDriver(dTag.getText(), false);
                String spoints = JkStruct.getLastElem(tr.getChildren()).getChild("b").getText();
                double points = Double.parseDouble(spoints);
                map.put(driver.getFullName(), points);
            }
        }

        return map;
    }

    @Override
    protected Map<String, Double> getExpectedTeamPoints(String html) {
        Map<String, Double> map = new HashMap<>();

        JkTag tableEntrants = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"World_Constructors'_Championship_standings\">", "<table class=\"wikitable\"");
        JkTag tbody = tableEntrants.getChild("tbody");

        for (JkTag tr : tbody.getChildren("tr")) {
            if(tr.getChildren("th").size() == 1 && tr.getChildren().get(0).getTagName().equals("th")) {
                JkTag teamTag = tr.getChild(1).findChild("a", "span a");
                F1Team team = retrieveTeam(teamTag.getText(), false);
                String spoints = JkStruct.getLastElem(tr.getChildren()).getChild("b").getText();
                spoints = spoints.replaceAll(".*\\(|\\).*", "");
                map.put(team.getTeamName(), Double.parseDouble(spoints));
            }
        }

        return map;
    }

    @Override
    protected void parseQualify(String html, F1GranPrix gp) {
        if(JkTests.equalsAny(gp.getNum(), 15, 16))   return;

        JkTag tableQualify = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying_2\">", "<table class=\"wikitable");
        if(tableQualify == null) {
            tableQualify = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying\">", "<table class=\"wikitable");
        }

        JkTag tbody = tableQualify.getChild("tbody");

        int pos = 1;

        for (JkTag tr : tbody.getChildren("tr")) {
            int thNum = tr.getChildren("th").size();
            int tdNum = tr.getChildren("td").size();

            if(tdNum >= 5) {
                F1Qualify q = new F1Qualify();
                q.setGpPK(gp.getPrimaryKey());
                q.setPos(pos++);
                gp.getQualifies().add(q);

                int carNo = Integer.parseInt(tr.getChild(1).getText().replace("‡", ""));
                F1Driver d = retrieveDriver(tr.getChild(2).findChild("a", "b a").getText(), false);
                JkTag ttag = tr.getChild(3).findChild("a", "span a", "b a");
                F1Team team = retrieveTeam(ttag.getText().replaceAll("-$", ""), false);
                q.setEntrant(getEntrant(year, d, carNo, team));

                List<JkTag> allChilds = tr.getChildren();
                JkTag chTime = allChilds.get(allChilds.size() - 2);
                q.getTimes().add(getQualTime(chTime));

                q.setFinalGrid(q.getPos());
            }
        }

//        if(gp.getNum()==4) {
//            List<F1Qualify> elist = gp.getQualifies();
//            System.out.println(elist.size()+"");
//            System.out.println(RepoUtil.formatEntities(elist));
//            System.exit(1);
//        }
    }
    private JkDuration getQualTime(JkTag tag) {
        if(tag.getChildren().size() == 1 && tag.getChild("span", "style=display:none;") != null) {
            return null;
        }
        return parseDuration(tag.getTextFlat());
    }

    @Override
    protected void parseRace(String html, F1GranPrix gp) {
        JkTag tableRace = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race_2\">", "<table class=\"wikitable\"");
        if(tableRace == null) {
            tableRace = JkScanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Race\">", "<table class=\"wikitable");
        }
        JkTag tbody = tableRace.getChild("tbody");

//        if(gp.getNum() == 2) {
//            List<F1Qualify> elist = gp.getQualifies();
//            System.out.println(elist.size() + "");
//            System.out.println(RepoUtil.formatEntities(elist));
//            System.exit(1);
//        }

        Map<Integer, F1Qualify> qualifyMap = JkStreams.toMapSingle(gp.getQualifies(), q -> q.getEntrant().getCarNo());
        int pos = 1;

        for (JkTag tr : tbody.getChildren("tr")) {
            int tdNum = tr.getChildren("td").size();
            int thNum = tr.getChildren("th").size();
            if(thNum == 1 && tdNum == 7) {
                F1Race r = new F1Race();
                r.setGpPK(gp.getPrimaryKey());
                r.setPos(pos++);
                gp.getRaces().add(r);

                r.setRetired(JkConvert.toInt(tr.getChild(0).getText()) == null);

                int carNum = Integer.parseInt(tr.getChild(1).getText().replace("‡", ""));
                if(gp.getQualifies().isEmpty()) {
                    F1Driver d = retrieveDriver(tr.getChild(2).findChild("a", "b a").getText(), false);
                    JkTag ttag = tr.getChild(3).findChild("a", "b a");
                    F1Team team = retrieveTeam(ttag.getText().replaceAll("-$", ""), false);
                    r.setEntrant(getEntrant(year, d, carNum, team));
                    r.setStartGrid(Integer.parseInt(tr.getChild(6).getText()));
                } else {
                    F1Qualify q = qualifyMap.get(carNum);
                    r.setEntrant(q.getEntrant());
                    r.setStartGrid(q.getFinalGrid());
                }

                String stmp = tr.getChild(4).getText();
                r.setLaps(stmp.isEmpty() ? 0 : Integer.parseInt(stmp));

                r.setTime(parseDuration(tr.getChild(5).getText().replace("/Accident", "")));
                if(gp.getRaces().size() > 1 && r.getTime() != null) {
                    F1Race firstRace = gp.getRaces().get(0);
                    JkDuration ft = firstRace.getTime().plus(r.getTime());
                    r.setTime(ft);
                }

                JkTag lastChild = tr.getChild(7);
                if(lastChild.getChild("b") == null) {
                    r.setPoints(JkConvert.toDouble(lastChild.getText(), 0d));
                } else {
                    r.setPoints(JkConvert.toDouble(lastChild.getChild("b").getText(), 0d));
                }

            }
        }

    }

}
