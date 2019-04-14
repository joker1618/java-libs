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
import java.util.*;

public class F1ResourceManager implements F1Resources {

    private static final Logger LOG = LoggerFactory.getLogger(F1ResourceManager.class);

    private static F1Resources instance;
    private final F1Model model;

    private Map<String, JkImage> iconFlags;
    private Map<F1GranPrix, JkImage> trackMaps;
    private Map<F1Driver, JkImage> drivers;

    private F1ResourceManager() {
        model = F1ModelImpl.getInstance();
        iconFlags = new HashMap<>();
        trackMaps = new HashMap<>();
        drivers = new HashMap<>();
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
        JkImage ficon = iconFlags.get(nation);

        if(ficon == null) {
            F1Resource resource = retrieveResource(nation, "icon", "flag");
            if(resource != null) {
                ficon = JkImage.from(resource);
                iconFlags.put(nation, ficon);
            }
        }

        return ficon;
    }

    @Override
    public JkImage getTrackMap(F1GranPrix gp) {
        JkImage trackMap = trackMaps.get(gp);

        if(trackMap == null) {
            F1Resource resource = retrieveResource(gp.getPrimaryKey(), "image", "trackMap");
            if(resource != null) {
                trackMap = JkImage.from(resource);
                trackMaps.put(gp, trackMap);
            }
        }

        return trackMap;
    }

    @Override
    public JkImage getDriverPicture(F1Driver driver) {
        JkImage imgDriver = drivers.get(driver);

        if(imgDriver == null) {
            F1Resource resource = retrieveResource(driver.getPrimaryKey(), "image", "driver");
            if(resource != null) {
                imgDriver = JkImage.from(resource);
                drivers.put(driver, imgDriver);
            }
        }

        return imgDriver;
    }

    private F1Resource retrieveResource(String key, String... tags) {
        F1Resource toRet = null;
        List<F1Resource> resList = model.getDataList(F1Resource.class, r -> r.getKey().equals(key));
        Arrays.stream(tags).forEach(tag -> resList.removeIf(r -> !r.getTags().contains(tag)));
        if(!resList.isEmpty()) {
            if(resList.size() > 1) {
                resList.sort(Comparator.comparing(r -> r.getTags().size()));
            }
            toRet = resList.get(0);
        }
        return toRet;
    }

    private boolean saveImage(Path folder, String resourceKey, String url, String... tags) {
        JkDownloader dw = new JkDownloader(folder);
        String finalName = fixResourceName(resourceKey, url);
        boolean res = false;
        if(dw.downloadResource(finalName, url)) {
            LOG.info("Downloaded resource: {}", finalName);
            res = true;
        }

        Path resourcePath = folder.resolve(finalName);
        F1Resource resource = new F1Resource(resourcePath);
        if(model.getByPK(resource) == null) {
            resource.setKey(resourceKey);
            resource.setTags(tags);
            JkImage jkImage = JkImage.parse(resourcePath);
            resource.setWidth(jkImage.getWidth());
            resource.setHeight(jkImage.getHeight());
            model.add(resource);
        }

        return res;
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
