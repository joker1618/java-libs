package xxx.joker.apps.formula1.model.providers;

import javafx.scene.image.Image;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceProvider {

    private final Map<F1GranPrix, Image> trackMaps = new HashMap<>();
    private final Map<F1Driver, Image> driverImageMap = new HashMap<>();
    private final Map<String, Image> flagIconMap = new HashMap<>();

    public Image getDriverImage(F1Driver driver) {
        synchronized (driverImageMap) {
            Image img = driverImageMap.get(driver);
            if(img == null) {
                Path folder = F1Const.IMG_DRIVER_PIC_FOLDER;
                List<Path> paths = JkFiles.findFiles(folder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).equals(driver.getFullName()));
                if(!paths.isEmpty()) {
                    String picUrl = JkFiles.toURL(paths.get(0));
                    img = new Image(picUrl, true);
                    driverImageMap.put(driver, img);
                }
            }
            return img;
        }
    }

    public Image getTrackMapImage(F1GranPrix gp) {
        synchronized (trackMaps) {
            Image img = trackMaps.get(gp);
            if(img == null) {
                Path folder = F1Const.IMG_TRACK_MAP_FOLDER;
                List<Path> paths = JkFiles.findFiles(folder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).equals(gp.getPrimaryKey()));
                if(!paths.isEmpty()) {
                    String picUrl = JkFiles.toURL(paths.get(0));
                    img = new Image(picUrl, true);
                    trackMaps.put(gp, img);
                }
            }
            return img;
        }
    }

    public Image getFlagIconImage(String nation) {
        synchronized (flagIconMap) {
            Image img = flagIconMap.get(nation);
            if(img == null) {
                Path folder = F1Const.IMG_FLAGS_ICON_FOLDER;
                List<Path> paths = JkFiles.findFiles(folder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).equals(nation));
                if(!paths.isEmpty()) {
                    String picUrl = JkFiles.toURL(paths.get(0));
                    img = new Image(picUrl, true);
                    flagIconMap.put(nation, img);
                }
            }
            return img;
        }
    }
}
