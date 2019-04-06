package xxx.joker.apps.formula1.model;

import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.repository.JkRepo;

import java.util.List;
import java.util.Set;

public interface F1Model extends JkRepo {

    F1Team getTeam(String teamName);
    Set<F1Team> getTeams();

    F1Driver getDriver(String driverName);
    Set<F1Driver> getDrivers();

    Set<F1Entrant> getEntrants();
    List<F1Entrant> getEntrants(int year);

    Set<F1Link> getLinks();

    Set<F1GranPrix> getGranPrixs();
    List<F1GranPrix> getGranPrixs(int year);

}
