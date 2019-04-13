package xxx.joker.apps.formula1.model;

import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;

public interface F1Resources {

    boolean saveFlagIcon(String nation, String url);
    boolean saveTrackMap(F1GranPrix gp, String url);
    boolean saveDriverPicture(F1Driver driver, String url);

    JkImage getFlagIcon(String nation);
    JkImage getTrackMap(F1GranPrix gp);
    JkImage getDriverPicture(F1Driver driver);

}
