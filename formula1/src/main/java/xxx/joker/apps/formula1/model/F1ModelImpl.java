package xxx.joker.apps.formula1.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.beans.F1SeasonResult;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.web.JkDownloader;
import xxx.joker.libs.repository.JkRepoFile;
import xxx.joker.libs.repository.design.RepoEntity;

import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class F1ModelImpl extends JkRepoFile implements F1Model {

    private static final Logger LOG = LoggerFactory.getLogger(F1ModelImpl.class);
    private static F1Model instance;

    private Map<Integer, F1Season> seasonMap = new HashMap<>();

    private F1ModelImpl() {
        super(F1Const.DB_FOLDER, F1Const.DB_NAME, "xxx.joker.apps.formula1.model.entities");
//        super(F1Const.DB_FOLDER, F1Const.DB_NAME, "xxx.joker.apps.formula1.model.entities", "fede");
    }

    public static synchronized F1Model getInstance() {
        if(instance == null) {
            instance = new F1ModelImpl();
        }
        return instance;
    }

    @Override
    public F1Team getTeam(String teamName) {
        return JkStreams.findUnique(getTeams(), t -> t.getTeamName().equals(teamName));
    }

    @Override
    public Set<F1Team> getTeams() {
        return getDataSet(F1Team.class);
    }

    @Override
    public F1Driver getDriver(String driverName) {
        return JkStreams.findUnique(getDrivers(), d -> d.getFullName().equals(driverName));
    }

    @Override
    public Set<F1Driver> getDrivers() {
        return getDataSet(F1Driver.class);
    }

    @Override
    public Set<F1Entrant> getEntrants() {
        return getDataSet(F1Entrant.class);
    }
    @Override
    public List<F1Entrant> getEntrants(int year) {
        return JkStreams.filter(getEntrants(), e -> e.getYear() == year);
    }

    @Override
    public Set<F1GranPrix> getGranPrixs() {
        return getDataSet(F1GranPrix.class);
    }

    @Override
    public List<F1GranPrix> getGranPrixs(int year) {
        return JkStreams.filter(getGranPrixs(), gp -> gp.getYear() == year);
    }

    @Override
    public Set<F1Circuit> getCircuits() {
        return getDataSet(F1Circuit.class);
    }

    @Override
    public F1Circuit getCircuit(String city, String nation) {
        return JkStreams.findUnique(getCircuits(), c -> city.equals(c.getCity()) && nation.equals(c.getNation()));
    }

    @Override
    public void deleteData(int year) {
        List<RepoEntity> list = new ArrayList<>();
        list.addAll(getEntrants(year));
        for (F1GranPrix gp : getGranPrixs(year)) {
            list.addAll(gp.getQualifies());
            list.addAll(gp.getRaces());
            list.add(gp);
        }
        for (RepoEntity entity : list) {
            LOG.debug("Start deleting {}", entity);
            super.remove(entity);
        }
    }

    @Override
    public F1Season getSeason(int year) {
        F1Season season = seasonMap.get(year);

        if(season == null) {
            List<F1GranPrix> gpList = getGranPrixs(year);
            List<F1Entrant> entrants = getEntrants(year);

            List<F1SeasonResult> results = new ArrayList<>();
            entrants.stream().map(F1Entrant::getDriver).distinct().forEach(d -> {
                F1SeasonResult res = new F1SeasonResult();
                res.setDriver(d);
                gpList.forEach(gp -> {
                    F1Race race = JkStreams.findUnique(gp.getRaces(), r -> r.getEntrant().getDriver().equals(d));
                    if(race != null) {
                        res.getPoints().put(gp, race);
                    }
                });
                res.setTotPoints(JkStreams.sumInt(res.getPoints().values(), F1Race::getPoints));
                results.add(res);
            });
            Collections.sort(results);

            season = new F1Season(year);
            season.setEntrants(entrants);
            season.setGpList(gpList);
            season.setResults(results);
            seasonMap.put(year, season);
        }

        return season;
    }

    @Override
    public List<Integer> getAvailableYears() {
        return JkStreams.mapFilterSortUniq(
                getEntrants(),
                F1Entrant::getYear,
                f -> true,
                Comparator.reverseOrder()
        );
    }


}
