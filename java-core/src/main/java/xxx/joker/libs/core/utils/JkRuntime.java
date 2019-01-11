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

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkRuntime {

    public static final String ENV_PROP_TEMP_FOLDER = "joker.apps.temp.folder";
    public static final Path FALLBACK_TEMP_FOLDER = Paths.get(System.getProperty("user.home")).resolve(".tempApps");


    public static Path getTempFolder() {
        String val = System.getProperty("joker.apps.temp.folder");
        return val == null ? FALLBACK_TEMP_FOLDER : Paths.get(JkConvert.unixToWinPath(val));
    }

    /**
     * Get classes from:
     * - classpath: if launcher path is a folder (IDE run) or is a JAR inside Maven repository folder (libraries)
     * - launcher JAR: else
     */
    public static List<Class<?>> findClasses(String packageName) {
        try {
            File launcherPath = JkFiles.getLauncherPath(JkReflection.class).toFile();
            boolean isLaunchedFromMavenRepo = JkStrings.matchRegExp(".*[/\\\\]{1}.m2[/\\\\]{1}repository[/\\\\]{1}.*", launcherPath.getPath());
            List<Class<?>> classes;
            if (launcherPath.isDirectory() || isLaunchedFromMavenRepo) {
                classes = getClassesFromClassLoader(packageName);
            } else {
                classes = getClassesFromJar(launcherPath, packageName);
            }
            return classes;

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private static List<Class<?>> getClassesFromClassLoader(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }

    private static List<Class<?>> getClassesFromJar(File jarFile, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        try(JarFile file = new JarFile(jarFile)) {
            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements(); ) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");
                if (name.startsWith(packageName) && name.endsWith(".class"))
                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
            }
        }
        return classes;
    }

}
