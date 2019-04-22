package xxx.joker.apps.formula1.old.dataCreator.model;

import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1Driver;
import xxx.joker.apps.formula1.old.dataCreator.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.old.fxlibs.JxImage;

public interface F1Resources {

    boolean saveFlagIcon(String nation, String url);
    boolean saveFlag(String nation, String url);
    boolean saveTrackMap(F1GranPrix gp, String url);
    boolean saveDriverPicture(F1Driver driver, String url);

    JxImage getFlagIcon(String nation);
    JxImage getTrackMap(F1GranPrix gp);
    JxImage getDriverPicture(F1Driver driver);

}
