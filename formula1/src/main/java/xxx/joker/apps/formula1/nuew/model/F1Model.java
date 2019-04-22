package xxx.joker.apps.formula1.nuew.model;


import xxx.joker.apps.formula1.nuew.model.entities.F1Driver;
import xxx.joker.apps.formula1.nuew.model.entities.F1GranPrix;
import xxx.joker.libs.repository.JkRepo;
import xxx.joker.libs.repository.entities.RepoResource;

import java.nio.file.Path;

public interface F1Model extends JkRepo {

    RepoResource saveDriverCover(Path imgPath, F1Driver driver);
    RepoResource saveGpTrackMap(Path imgPath, F1GranPrix gp);

//    F1Team getTeam(String teamName);
//    Set<F1Team> getTeams();
//
//    F1Driver getDriver(String driverName);
//    Set<F1Driver> getDrivers();
//
//    Set<F1Entrant> getEntrants();
//    List<F1Entrant> getEntrants(int year);
//
//    Set<F1GranPrix> getGranPrixs();
//    List<F1GranPrix> getGranPrixs(int year);
//
//    Set<F1Circuit> getCircuits();
//    F1Circuit getCircuit(String city, String nation);
//
//    void deleteData(int year);
//
//    F1Season getSeason(int year);
//
//    List<Integer> getAvailableYears();
}
