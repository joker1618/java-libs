package xxx.joker.apps.formula1.model.managers;

import javafx.scene.image.Image;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class ResourceManager {

    private static final Map<F1GranPrix, JkImage> trackMaps = new HashMap<>();
    private static final Map<F1Driver, JkImage> driverImageMap = new HashMap<>();
    private static final Map<String, JkImage> flagIconMap = new HashMap<>();

    public static JkImage getDriverImage(F1Driver driver) {
        synchronized (driverImageMap) {
            JkImage img = driverImageMap.get(driver);
            if(img == null) {
                img = getImage(F1Const.IMG_DRIVER_PIC_FOLDER, driver.getPrimaryKey());
                if(img != null) {
                    driverImageMap.put(driver, img);
                }
            }
            return img;
        }
    }

    public static JkImage getTrackMapImage(F1GranPrix gp) {
        synchronized (trackMaps) {
            JkImage img = trackMaps.get(gp);
            if(img == null) {
                img = getImage(F1Const.IMG_TRACK_MAP_FOLDER, gp.getPrimaryKey());
                if(img != null) {
                    trackMaps.put(gp, img);
                }
            }
            return img;
        }
    }

    public static JkImage getFlagIconImage(String nation) {
        synchronized (flagIconMap) {
            JkImage img = flagIconMap.get(nation);
            if(img == null) {
                img = getImage(F1Const.IMG_FLAGS_ICON_FOLDER, nation);
                if(img != null) {
                    flagIconMap.put(nation, img);
                }
            }
            return img;
        }
    }

    private static JkImage getImage(Path folder, String filename) {
        JkImage img = null;
        List<Path> paths = JkFiles.findFiles(folder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).equals(filename));
        if(!paths.isEmpty()) {
            img = new JkImage(paths.get(0));
        }
        return img;
    }
}
