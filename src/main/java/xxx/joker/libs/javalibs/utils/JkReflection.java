package xxx.joker.libs.javalibs.utils;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by f.barbano on 26/05/2018.
 */
public class JkReflection {

	public static List<Field> getFieldsByAnnotation(Class<?> sourceClass, Class<? extends Annotation> annotationClass) {
		List<Field> toRet = new ArrayList<>();
		Field[] declaredFields = sourceClass.getDeclaredFields();
		if(!JkTests.isEmpty(declaredFields)) {
			for (Field field : declaredFields) {
				if (field.getAnnotation(annotationClass) != null) {
					toRet.add(field);
				}
			}
		}
		return toRet;
	}

	public static List<Field> getFieldsByType(Class<?> sourceClass, Class<?> fieldType) {
		List<Field> toRet = new ArrayList<>();
		Field[] declaredFields = sourceClass.getDeclaredFields();
		if(!JkTests.isEmpty(declaredFields)) {
			for (Field field : declaredFields) {
				if (field.getType() == fieldType) {
					toRet.add(field);
				}
			}
		}
		return toRet;
	}

	public static Field getFieldByName(Class<?> sourceClass, String fieldName) {
		List<Field> declaredFields = Arrays.asList(sourceClass.getDeclaredFields());
		List<Field> fields = JkStreams.filter(declaredFields, f -> f.getName().equals(fieldName));
		return fields.isEmpty() ? null : fields.get(0);
	}

	public static Enum getEnumByName(Class<?> enumClass, String enumName) {
		Enum[] enumConstants = (Enum[]) enumClass.getEnumConstants();
		for(Enum elem : enumConstants) {
			if(elem.name().equals(enumName)) {
				return elem;
			}
		}
		return null;
	}

	public static boolean isOfType(Class<?> clazz, Class<?> expected) {
		Set<Class<?>> types = new HashSet<>();

		Class<?> tmp = clazz;
		while(tmp != null) {
			types.add(tmp);
			types.addAll(findAllInterfaces(tmp));
			tmp = tmp.getSuperclass();
		}

		return types.contains(expected);
	}

	private static Set<Class<?>> findAllInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaces = new HashSet<>();
		interfaces.add(clazz);
		for(Class<?> interf : clazz.getInterfaces()) {
			interfaces.addAll(findAllInterfaces(interf));
		}
		return interfaces;
	}

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
