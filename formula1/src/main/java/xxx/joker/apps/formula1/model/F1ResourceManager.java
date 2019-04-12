package xxx.joker.apps.formula1.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Resource;
import xxx.joker.libs.core.web.JkDownloader;

import java.nio.file.Path;

public class F1ResourceManager implements F1Resources {

    private static final Logger LOG = LoggerFactory.getLogger(F1ResourceManager.class);

    private static F1Resources instance;
    private final F1Model model;

    private F1ResourceManager() {
        model = F1ModelImpl.getInstance();
    }

    public static synchronized F1Resources getInstance() {
        if(instance == null) {
            instance = new F1ResourceManager();
        }
        return instance;
    }

    @Override
    public boolean saveFlagIcon(String nation, String url) {
        return saveImage(F1Const.IMG_FLAGS_ICON_FOLDER, nation, url, "icon", "flag");
    }

    @Override
    public boolean saveTrackMap(F1GranPrix gp, String url) {
        return saveImage(F1Const.IMG_TRACK_MAP_FOLDER, gp.getPrimaryKey(), url, "image", "trackMap");
    }

    @Override
    public boolean saveDriverPicture(F1Driver driver, String url) {
        return saveImage(F1Const.IMG_DRIVER_PIC_FOLDER, driver.getPrimaryKey(), url, "image", "driver");
    }

    @Override
    public JkImage getFlagIcon(String nation) {

        return null;
    }


    private boolean saveImage(Path folder, String resourceKey, String url, String... tags) {
        JkDownloader dw = new JkDownloader(folder);
        String finalName = fixResourceName(resourceKey, url);
        Path resourcePath = folder.resolve(finalName);

        F1Resource resource = new F1Resource(resourcePath);
        resource.setKey(resourceKey);
        if(model.retrieveByPK(resource) == null) {
            resource.setTags(tags);
            JkImage jkImage = JkImage.parse(resourcePath);
            resource.setWidth(jkImage.getWidth());
            resource.setHeight(jkImage.getHeight());
            model.add(resource);
        }
        if(dw.downloadResource(finalName, url)) {
            LOG.debug("Downloaded resource: {}", finalName);
            return true;
        }
        return false;
    }
    private String fixResourceName(String fn, String url) {
        String finalFname = fn;
        int dotIdx = url.lastIndexOf(".");
        int slashIdx = url.lastIndexOf("/");
        if(dotIdx != -1 && (slashIdx == -1 || dotIdx > slashIdx)) {
            String fext = url.substring(dotIdx);
            if (!finalFname.endsWith(fext)) {
                finalFname += fext;
            }
        }
        return finalFname;
    }


}
