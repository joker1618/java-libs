package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JkRuntime {

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
        if(files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
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

    public static long getJvmStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }
}
