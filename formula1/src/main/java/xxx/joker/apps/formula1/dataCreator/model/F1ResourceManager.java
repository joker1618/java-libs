package xxx.joker.apps.formula1.dataCreator.model;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.fxlibs.JxImage;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Driver;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Resource;
import xxx.joker.libs.core.web.JkDownloader;

import java.nio.file.Path;
import java.util.*;

public class F1ResourceManager implements F1Resources {

    private static final Logger LOG = LoggerFactory.getLogger(F1ResourceManager.class);

    private static F1Resources instance;
    private final F1Model model;

    private Map<String, JxImage> iconFlags;
    private Map<F1GranPrix, JxImage> trackMaps;
    private Map<F1Driver, JxImage> drivers;

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
    public boolean saveFlag(String nation, String url) {
        return saveImage(F1Const.IMG_FLAGS_FOLDER, nation, url, "image", "flag");
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
    public JxImage getFlagIcon(String nation) {
        JxImage ficon = iconFlags.get(nation);

        if(ficon == null) {
            F1Resource resource = retrieveResource(nation, "icon", "flag");
            if(resource != null) {
                ficon = resource.convertToImage();
                iconFlags.put(nation, ficon);
            }
        }

        return ficon;
    }

    @Override
    public JxImage getTrackMap(F1GranPrix gp) {
        JxImage trackMap = trackMaps.get(gp);

        if(trackMap == null) {
            F1Resource resource = retrieveResource(gp.getPrimaryKey(), "image", "trackMap");
            if(resource != null) {
                trackMap = resource.convertToImage();
                trackMaps.put(gp, trackMap);
            }
        }

        return trackMap;
    }

    @Override
    public JxImage getDriverPicture(F1Driver driver) {
        JxImage imgDriver = drivers.get(driver);

        if(imgDriver == null) {
            F1Resource resource = retrieveResource(driver.getPrimaryKey(), "image", "driver");
            if(resource != null) {
                imgDriver = resource.convertToImage();
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
        Pair<Boolean, Path> resDw = dw.downloadResource(finalName, url);
        if(resDw.getKey()) {
            LOG.info("Downloaded resource: {}", finalName);
            res = true;
        }

        Path resourcePath = folder.resolve(finalName);
        F1Resource resource = new F1Resource(resourcePath);
        if(model.get(resource) == null) {
            resource.setKey(resourceKey);
            resource.setTags(tags);
            JxImage jxImage = JxImage.parse(resourcePath);
            resource.setWidth(jxImage.getWidth());
            resource.setHeight(jxImage.getHeight());
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
