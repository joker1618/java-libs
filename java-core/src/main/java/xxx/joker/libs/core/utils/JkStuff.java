package xxx.joker.libs.core.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.files.JkFiles;

@ToAnalyze
@Deprecated
public class JkStuff {

    public static List<Class<?>> findClasses(String packageName) {
        File launcherPath = JkFiles.getLauncherPath(JkReflection.class).toFile();
        List<Class<?>> classes;
        if(launcherPath.isDirectory() || JkStrings.matchRegExp(".*[/\\\\]{1}.m2[/\\\\]{1}repository[/\\\\]{1}.*", launcherPath.getPath())) {
            classes = getClassesFromClassLoader(packageName);
        } else {
            classes = getClassesFromJar(launcherPath, packageName);
        }
        return classes;
    }

    private static List<Class<?>> getClassesFromClassLoader(String packageName) {
        try {
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

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
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

    private static List<Class<?>> getClassesFromJar(File jarFile, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            JarFile file = new JarFile(jarFile);
            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
                JarEntry jarEntry = entry.nextElement();
                String name = jarEntry.getName().replace("/", ".");
                if(name.startsWith(packageName) && name.endsWith(".class"))
                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
            }
            file.close();
            return classes;

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
