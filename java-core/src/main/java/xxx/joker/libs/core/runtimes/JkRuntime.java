package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkConsole.displayColl;

public class JkRuntime {

    public static List<Class<?>> findClasses(String packageName) {
        try {
            File launcherPath = JkFiles.getLauncherPath(JkRuntime.class).toFile();
            List<Class<?>> classes = new ArrayList<>();
            if(launcherPath.isFile() && launcherPath.getName().toLowerCase().endsWith(".jar")) {
                classes.addAll(getClassesFromJar(launcherPath, packageName));
            }
            classes.addAll(getClassesFromClassLoader(packageName));
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
                String prefix = packageName.isEmpty() ? "" : packageName+".";
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, prefix + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(Class.forName(prefix + file.getName().replaceAll("\\.class$", "")));
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
                if (name.endsWith(".class") && (packageName.isEmpty() || name.startsWith(packageName))) {
                    classes.add(Class.forName(name.replaceAll("\\.class$", "")));
                }
            }
        }
        return classes;
    }

    public static long getJvmStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

}
