package xxx.joker.apps.formula1.model;

import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.JkRepoFile;
import xxx.joker.libs.repository.design.RepoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class F1ModelImpl extends JkRepoFile implements F1Model {

    private static F1Model instance;

    private F1ModelImpl() {
        super(F1Const.DB_FOLDER, F1Const.DB_NAME, "xxx.joker.apps.formula1.model.entities");
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
        return JkStreams.findUnique(getDrivers(), d -> d.getDriverName().equals(driverName));
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
    public Set<F1Link> getLinks() {
        return getDataSet(F1Link.class);
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
    public void deleteData(int year) {
        List<RepoEntity> list = new ArrayList<>();
        list.addAll(getEntrants(year));
        for (F1GranPrix gp : getGranPrixs(year)) {
            list.addAll(gp.getQualifies());
            list.addAll(gp.getRaces());
            list.add(gp);
        }

        list.forEach(super::remove);
    }

    @Override
    public List<F1Entrant> getEntrants(int year) {
        return JkStreams.filter(getEntrants(), e -> e.getYear() == year);
    }

}
