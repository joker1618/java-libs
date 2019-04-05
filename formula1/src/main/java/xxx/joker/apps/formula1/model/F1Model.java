package xxx.joker.apps.formula1.model;

import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Link;
import xxx.joker.apps.formula1.model.entities.F1Team;
import xxx.joker.libs.repository.JkRepo;

import java.util.List;
import java.util.Set;

public interface F1Model extends JkRepo {

    F1Team getTeam(String teamName);
    Set<F1Team> getTeams();

    F1Driver getDriver(String driverName);
    Set<F1Driver> getDrivers();

    Set<F1Entrant> getEntrants();
    Set<F1Link> getLinks();

    List<F1Entrant> getEntrants(int year);
}
