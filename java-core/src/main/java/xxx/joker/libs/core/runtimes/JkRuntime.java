package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JkRuntime {

    public static List<Class<?>> findClasses(String packageName) {
        try {
            File launcherPath = JkFiles.getLauncherPath(JkRuntime.class).toFile();
            if(launcherPath.isFile() && launcherPath.getName().toLowerCase().endsWith(".jar")) {
                return findClassesInJar(launcherPath, packageName);
            } else {
                return getClassesFromClassLoader(packageName);
            }

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

    private static List<Class<?>> findClassesInJar(Path jarFile, String packageName) {
        return findClassesInJar(jarFile.toFile(), packageName);
    }
    private static List<Class<?>> findClassesInJar(File jarFile, String packageName)  {
        try {
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

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    public static long getJvmStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

}
