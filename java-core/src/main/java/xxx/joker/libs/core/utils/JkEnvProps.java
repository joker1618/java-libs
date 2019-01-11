package xxx.joker.libs.core.utils;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JkEnvProps {

    public static final String PROP_TEMP_FOLDER = "joker.apps.temp.folder";
    public static final String PROP_OUTPUT_SIMPLE_CLASS_NAME = "joker.apps.output.format.class.simple.name";
    
    public static final Path FALLBACK_TEMP_FOLDER = Paths.get(System.getProperty("user.home")).resolve(".tempApps");


    public static Path getTempFolder() {
        String val = System.getProperty(PROP_TEMP_FOLDER);
        return val == null ? FALLBACK_TEMP_FOLDER : Paths.get(JkConvert.unixToWinPath(val));
    }
    public static void setTempFolder(Path tempFolder) {
        System.setProperty(PROP_TEMP_FOLDER, tempFolder.toAbsolutePath().normalize().toString());
    }

    public static boolean isShowClassSimpleName() {
        String val = System.getProperty(PROP_OUTPUT_SIMPLE_CLASS_NAME);
        return val != null && Boolean.valueOf(val);
    }
    public static void setShowClassSimpleName(boolean showSimple) {
        System.setProperty(PROP_OUTPUT_SIMPLE_CLASS_NAME, String.valueOf(showSimple));
    }


}
