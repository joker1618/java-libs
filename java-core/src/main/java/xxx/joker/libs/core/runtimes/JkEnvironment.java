package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JkEnvironment {

    private static final String HOME_FOLDER_KEY = "user.home";
    private static final String APPS_FOLDER_KEY = "apps.folder";

    private static final Path APPS_FOLDER_DEFAULT = getHomeFolder().resolve(".appsFolder");

    private static final Map<String, Object> cacheMap = new HashMap<>();


    public static Path getHomeFolder() {
        return Paths.get(System.getProperty(HOME_FOLDER_KEY));
    }

    public static Path getAppsFolder() {
        if(!cacheMap.containsKey(APPS_FOLDER_KEY)) {
            String val = System.getProperty(APPS_FOLDER_KEY);
            Path p = val == null ? APPS_FOLDER_DEFAULT : Paths.get(JkConvert.unixToWinPath(val));
            cacheMap.put(APPS_FOLDER_KEY, p);
        }
        return (Path) cacheMap.get(APPS_FOLDER_KEY);
    }

    public static Path relativizeAppsPath(String sourcePath) {
        return relativizeAppsPath(Paths.get(sourcePath));
    }
    public static Path relativizeAppsPath(Path source) {
        if(source.isAbsolute() && source.startsWith(getAppsFolder())) {
            return getAppsFolder().relativize(source);
        }
        return source;
    }

    public static Path toAbsoluteAppsPath(String sourcePath) {
        return toAbsoluteAppsPath(Paths.get(sourcePath));
    }
    public static Path toAbsoluteAppsPath(Path source) {
        if(!source.isAbsolute()) {
            return getAppsFolder().resolve(source);
        }
        return source;
    }

}
