package xxx.joker.apps.formula1.old.dataCreator.model;

import xxx.joker.apps.formula1.old.dataCreator.model.beans.F1Season;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.*;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.*;
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

    Set<F1GranPrix> getGranPrixs();
    List<F1GranPrix> getGranPrixs(int year);

    Set<F1Circuit> getCircuits();
    F1Circuit getCircuit(String city, String nation);

    void deleteData(int year);

    F1Season getSeason(int year);

    List<Integer> getAvailableYears();
}
