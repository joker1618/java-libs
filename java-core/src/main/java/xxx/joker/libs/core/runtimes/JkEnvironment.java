package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.cache.JkCache;
import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JkEnvironment {

    private static final String HOME_FOLDER_KEY = "user.home";
    public static final String APPS_FOLDER_KEY = "apps.folder";

    private static final Path APPS_FOLDER_DEFAULT = getHomeFolder().resolve(".appsFolder");

    public static Path getHomeFolder() {
        return Paths.get(System.getProperty(HOME_FOLDER_KEY));
    }

    public static Path getAppsFolder() {
        String val = System.getProperty(APPS_FOLDER_KEY);
        Path p = val == null ? APPS_FOLDER_DEFAULT : Paths.get(JkConvert.unixToWinPath(val));
        return p;
    }

    public static void setAppsFolder(Path folder) {
        System.setProperty(APPS_FOLDER_KEY, folder.toString());
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
