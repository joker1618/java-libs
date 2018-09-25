package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class TestVarious {

	@Test
	public void testvar() {

//		Reflections reflections = new Reflections("xxx.joker.libs.javalibs.datamodel", new ());
//
//		Set<Class<? extends Object>> allClasses =
//				reflections.getSubTypesOf(Object.class);
//		allClasses.forEach(c -> display(c.getName()));

		Arrays.asList(getClasses("xxx.joker.libs.javalibs.datamodel")).forEach(c -> display(c.toString()));
	}

	private static Class[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			List<Class> classes = new ArrayList<>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			return classes.toArray(new Class[classes.size()]);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
		List classes = new ArrayList();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	@Test
	public void test() {
		Class<?> clazz = ClazzA.class;


		display("%s", Arrays.toString(clazz.getInterfaces()));
		display("%s", Arrays.asList(clazz.getInterfaces()).contains(Interface.class));
		display("END");
	}

	@Test
	public void testA() {
//        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
//        display("%s", resource);

        String[] strarr = new String[0];
        Class<?> aClass = strarr.getClass();
        display("clazz  %s", aClass);
        display("array?  %s", aClass.isArray());
        display("clazz type  %s", aClass.getTypeName());
        display("comp type  %s", aClass.getComponentType());

        List<Integer> list = new ArrayList<>();
        aClass = list.getClass();
        display("clazz  %s", aClass);
        display("array?  %s", aClass.isArray());
        display("clazz type  %s", aClass.getTypeName());
        display("comp type  %s", aClass.getComponentType());

        display("\n\n");

        Object ca = new ClazzA();
        display("isCA  %s", ca instanceof ClazzA);
        display("isInterface  %s", ca instanceof Interface);
        display("isString  %s", ca instanceof String);
    }

	interface Interface {
		void doAction();
	}

	class ClazzA implements Interface {

		@Override
		public void doAction() {

		}
	}
}
