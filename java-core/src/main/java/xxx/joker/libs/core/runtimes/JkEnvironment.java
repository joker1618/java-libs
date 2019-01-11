package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JkEnvironment {

    public static final String HOME_FOLDER_KEY = "user.home";
    public static final String APPS_TEMP_FOLDER_KEY = "joker.apps.temp.folder";
    public static final String OUTPUT_SIMPLE_CLASS_NAME_KEY = "joker.apps.output.simpleClassName";
    
    public static final Path FALLBACK_TEMP_FOLDER = Paths.get(System.getProperty("user.home")).resolve(".tempApps");

    private static final Map<String, Object> cacheMap = new HashMap<>();


    public static Path getHomeFolder() {
        return Paths.get(System.getProperty(HOME_FOLDER_KEY));
    }

    public static Path getAppTempFolder() {
        if(!cacheMap.containsKey(APPS_TEMP_FOLDER_KEY)) {
            String val = System.getProperty(APPS_TEMP_FOLDER_KEY);
            Path p = val == null ? FALLBACK_TEMP_FOLDER : Paths.get(JkConvert.unixToWinPath(val));
            cacheMap.put(APPS_TEMP_FOLDER_KEY, p);
        }
        return (Path) cacheMap.get(APPS_TEMP_FOLDER_KEY);
    }
    public static void setAppTempFolder(Path tempFolder) {
        Path p = tempFolder.toAbsolutePath().normalize();
        cacheMap.put(APPS_TEMP_FOLDER_KEY, p);
        System.setProperty(APPS_TEMP_FOLDER_KEY, p.toString());
    }

    public static boolean isShowClassSimpleName() {
        if(!cacheMap.containsKey(OUTPUT_SIMPLE_CLASS_NAME_KEY)) {
            String val = System.getProperty(OUTPUT_SIMPLE_CLASS_NAME_KEY);
            Boolean b = val == null ? false : Boolean.valueOf(val);
            cacheMap.put(OUTPUT_SIMPLE_CLASS_NAME_KEY, b);
        }
        return (Boolean) cacheMap.get(OUTPUT_SIMPLE_CLASS_NAME_KEY);
    }
    public static void setShowClassSimpleName(boolean showSimple) {
        cacheMap.put(OUTPUT_SIMPLE_CLASS_NAME_KEY, showSimple);
        System.setProperty(OUTPUT_SIMPLE_CLASS_NAME_KEY, String.valueOf(showSimple));
    }


}
