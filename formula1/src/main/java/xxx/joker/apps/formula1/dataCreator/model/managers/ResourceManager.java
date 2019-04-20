package xxx.joker.apps.formula1.dataCreator.model.managers;

import javafx.scene.image.Image;
import xxx.joker.apps.formula1.dataCreator.common.F1Const;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Driver;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1GranPrix;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class ResourceManager {

    private static final Map<F1GranPrix, Image> trackMaps = new HashMap<>();
    private static final Map<F1Driver, Image> driverImageMap = new HashMap<>();
    private static final Map<String, Image> flagIconMap = new HashMap<>();

    public static Image getDriverImage(F1Driver driver) {
        synchronized (driverImageMap) {
            Image img = driverImageMap.get(driver);
            if(img == null) {
                img = getImage(F1Const.IMG_DRIVER_PIC_FOLDER, driver.getPrimaryKey());
                if(img != null) {
                    driverImageMap.put(driver, img);
                }
            }
            return img;
        }
    }

    public static Image getTrackMapImage(F1GranPrix gp) {
        synchronized (trackMaps) {
            Image img = trackMaps.get(gp);
            if(img == null) {
                img = getImage(F1Const.IMG_TRACK_MAP_FOLDER, gp.getPrimaryKey());
                if(img != null) {
                    trackMaps.put(gp, img);
                }
            }
            return img;
        }
    }

    public static Image getFlagIconImage(String nation) {
        synchronized (flagIconMap) {
            Image img = flagIconMap.get(nation);
            if(img == null) {
                img = getImage(F1Const.IMG_FLAGS_ICON_FOLDER, nation);
                if(img != null) {
                    flagIconMap.put(nation, img);
                }
            }
            return img;
        }
    }

    private static Image getImage(Path folder, String filename) {
        Image img = null;
        List<Path> paths = JkFiles.find(folder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).equals(filename));
        if(!paths.isEmpty()) {
            String picUrl = JkFiles.toURL(paths.get(0));
            img = new Image(picUrl, true);
        }
        return img;
    }
}
