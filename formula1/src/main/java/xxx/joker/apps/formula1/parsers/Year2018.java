package xxx.joker.apps.formula1.parsers;

import xxx.joker.apps.formula1.corelibs.X_Scanners;
import xxx.joker.apps.formula1.corelibs.X_Tag;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                String teamName = tdList.get(1).findChild("b", "a").getText();
                F1Team team = retrieveTeam(teamName);
                String engine = tdList.get(3).getText();

                String stmp = tdList.get(4).getHtmlTag().replaceAll("^<td(.*?)>", "").replace("</td>", "").replaceAll("<br[ ]?/>", "-");
                List<Integer> carNums = JkStreams.map(JkStrings.splitList(stmp, "-", true), Integer::valueOf);

                List<F1Driver> drivers = new ArrayList<>();
                tdList.get(5).getChildren("a").forEach(t -> {
                    F1Driver d = retrieveDriver(t.getText());
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
                X_Tag dTag = tr.getChild(1).getChild("a");
                if(dTag == null) {
                    dTag = tr.getChild(1).findChild("span", "a");
                }
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
                X_Tag teamTag = tr.getChild(1).getChild("a");
                if(teamTag == null) {
                    teamTag = tr.getChild(1).findChild("span", "a");
                }
                F1Team team = model.getTeam(teamTag.getText());
                String spoints = tr.getChildren("th").get(1).getText();
                spoints = spoints.replaceAll(".*\\(", "").replaceAll("\\).*", "");
                int points = Integer.parseInt(spoints);
                map.put(team.getTeamName(), points);
            }
        }

        return map;
    }

    @Override
    protected void parseQualify(String html, F1GranPrix gp) {
        X_Tag tableQualify = X_Scanners.parseHtmlTag(html, "table", "<span class=\"mw-headline\" id=\"Qualifying_2\">", "<table class=\"wikitable sortable\"");
//        X_Tag tbody = tableEntrants.getChild("tbody");
//
//        for (X_Tag tr : tbody.getChildren("tr")) {
//            if(tr.getChildren("th").size() == 2) {
//                X_Tag teamTag = tr.getChild(1).getChild("a");
//                if(teamTag == null) {
//                    teamTag = tr.getChild(1).findChild("span", "a");
//                }
//                F1Team team = model.getTeam(teamTag.getText());
//                String spoints = tr.getChildren("th").get(1).getText();
//                spoints = spoints.replaceAll(".*\\(", "").replaceAll("\\).*", "");
//                int points = Integer.parseInt(spoints);
//                map.put(team.getTeamName(), points);
//            }
//        }
//
//        return map;
    }

    @Override
    protected void parseRace(String html, F1GranPrix gp) {

    }

}
