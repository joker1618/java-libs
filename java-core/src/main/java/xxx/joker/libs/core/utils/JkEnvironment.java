package xxx.joker.libs.core.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JkEnvironment {

    public static final String APPS_TEMP_FOLDER = "joker.apps.temp.folder";
    public static final String OUTPUT_SIMPLE_CLASS_NAME = "joker.apps.output.format.class.simple.name";
    
    public static final Path FALLBACK_TEMP_FOLDER = Paths.get(System.getProperty("user.home")).resolve(".tempApps");

    private static final Map<String, Object> cacheMap = new HashMap<>();


    public static Path getAppTempFolder() {
        if(!cacheMap.containsKey(APPS_TEMP_FOLDER)) {
            String val = System.getProperty(APPS_TEMP_FOLDER);
            Path p = val == null ? FALLBACK_TEMP_FOLDER : Paths.get(JkConvert.unixToWinPath(val));
            cacheMap.put(APPS_TEMP_FOLDER, p);
        }
        return (Path) cacheMap.get(APPS_TEMP_FOLDER);
    }
    public static void setAppTempFolder(Path tempFolder) {
        Path p = tempFolder.toAbsolutePath().normalize();
        cacheMap.put(APPS_TEMP_FOLDER, p);
        System.setProperty(APPS_TEMP_FOLDER, p.toString());
    }

    public static boolean isShowClassSimpleName() {
        if(!cacheMap.containsKey(OUTPUT_SIMPLE_CLASS_NAME)) {
            String val = System.getProperty(OUTPUT_SIMPLE_CLASS_NAME);
            Boolean b = val == null ? false : Boolean.valueOf(val);
            cacheMap.put(OUTPUT_SIMPLE_CLASS_NAME, b);
        }
        return (Boolean) cacheMap.get(OUTPUT_SIMPLE_CLASS_NAME);
    }
    public static void setShowClassSimpleName(boolean showSimple) {
        cacheMap.put(OUTPUT_SIMPLE_CLASS_NAME, showSimple);
        System.setProperty(OUTPUT_SIMPLE_CLASS_NAME, String.valueOf(showSimple));
    }


}
