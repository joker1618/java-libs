package xxx.joker.libs.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.config.RepoCtx;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.engine.RepoManager;
import xxx.joker.libs.repository.entities.RepoProperty;
import xxx.joker.libs.repository.entities.RepoResource;
import xxx.joker.libs.repository.entities.RepoTags;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class JkRepoFile implements JkRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkRepoFile.class);

    private RepoManager repoManager;
    private RepoCtx ctx;

    protected JkRepoFile() {}

    protected JkRepoFile(Path repoFolder, String dbName, String... pkgsToScan) {
        this(null, repoFolder, dbName, pkgsToScan);
    }

    protected JkRepoFile(Path repoFolder, String dbName, Collection<Class<?>> classes) {
        this(null, repoFolder, dbName, classes);
    }

    protected JkRepoFile(String encryptionPwd, Path repoFolder, String dbName, String... pkgsToScan) {
        initRepo(encryptionPwd, repoFolder, dbName, scanPackages(getClass(), pkgsToScan));
    }

    protected JkRepoFile(String encryptionPwd, Path repoFolder, String dbName, Collection<Class<?>> classes) {
        initRepo(encryptionPwd, repoFolder, dbName, classes);
    }

    protected void initRepo(String encryptionPwd, Path repoFolder, String dbName, Collection<Class<?>> classes) {
        this.ctx = new RepoCtx(repoFolder, dbName, classes, encryptionPwd);
        this.repoManager = new RepoManager(ctx);
    }


    @Override
    public Set<RepoProperty> getProperties() {
        return getDataSet(RepoProperty.class);
    }

    @Override
    public <T extends RepoEntity> Map<Class<T>, Set<T>> getDataSets() {
        return repoManager.getDataSets();
    }

    @Override
    public <T extends RepoEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return repoManager.getDataSet(entityClazz);
    }

    @Override
    @SafeVarargs
    public final <T extends RepoEntity> List<T> getList(Class<T> entityClazz, Predicate<T>... filters) {
        return JkStreams.filter(getDataSet(entityClazz), filters);
    }

    @Override
    @SafeVarargs
    public final <K, T extends RepoEntity> Map<K, T> getMap(Class<T> entityClazz, Function<T, K> keyMapper, Predicate<T>... filters) {
        return JkStreams.toMapSingle(getDataSet(entityClazz), keyMapper, e -> e, filters);
    }

    @Override
    public <T extends RepoEntity> T getById(long id) {
        return (T) repoManager.getDataByID().get(id);
    }

    @Override
    public <T extends RepoEntity> T get(Class<T> entityClazz, Predicate<T>... filters) {
        List<T> dataList = getList(entityClazz, filters);
        return dataList.size() == 1 ? dataList.get(0) : null;
    }

    @Override
    public <T extends RepoEntity> T getByPk(T entity) {
        Set<T> dataSet = (Set<T>) getDataSet(entity.getClass());
        return JkStreams.findUnique(dataSet, entity::equals);
    }

    @Override
    public <T extends RepoEntity> T getByPkOrAdd(T entity) {
        T found = getByPk(entity);
        if (found == null) {
            add(entity);
            found = entity;
        }
        return found;
    }

    @Override
    public <T extends RepoEntity> boolean add(T toAdd) {
        Set<T> dataSet = (Set<T>) getDataSet(toAdd.getClass());
        return dataSet.add(toAdd);
    }

    @Override
    public <T extends RepoEntity> boolean removeId(long eid) {
        return remove(getById(eid));
    }

    @Override
    public <T extends RepoEntity> boolean remove(T toRemove) {
        return getDataSet(toRemove.getClass()).remove(toRemove);
    }

    @Override
    public void clearAll() {
        repoManager.clearDataSets();
    }

    @Override
    public void rollback() {
        repoManager.rollback();
    }

    @Override
    public void commit() {
        repoManager.commit();
    }

    @Override
    public String getProperty(String propKey) {
        RepoProperty prop = retrieveProperty(propKey);
        return prop == null ? null : prop.getValue();
    }

    @Override
    public String getProperty(String propKey, String _default) {
        String val = getProperty(propKey);
        return val == null ? _default : val;
    }

    @Override
    public String setProperty(String propKey, String propValue) {
        RepoProperty prop = retrieveProperty(propKey);
        String oldValue = prop == null ? null : prop.getValue();
        if (prop != null) {
            prop.setValue(propValue);
        } else {
            prop = new RepoProperty(propKey, propValue);
            getDataSet(RepoProperty.class).add(prop);
        }
        return oldValue;
    }

    @Override
    public String delProperty(String propKey) {
        RepoProperty prop = retrieveProperty(propKey);
        if (prop != null) {
            getDataSet(RepoProperty.class).remove(prop);
        }
        return prop == null ? null : prop.getValue();
    }

    private RepoProperty retrieveProperty(String propKey) {
        return get((RepoProperty.class), rp -> rp.getKey().equalsIgnoreCase(propKey));
    }

    @Override
    public RepoResource getResource(String resName, String... tags) {
        return repoManager.getResource(resName, RepoTags.of(tags));
    }

    @Override
    public RepoResource addResource(Path sourcePath, String resName, String... tags) {
        return repoManager.addResource(sourcePath, resName, RepoTags.of(tags));
    }

    public static List<Class<?>> scanPackages(Class<?> launcherClazz, String... pkgsArr) {
        return scanPackages1(pkgsArr, p -> JkRuntime.findClasses(launcherClazz, p));
    }
    public static List<Class<?>> scanPackages1(String[] pkgsArr, Function<String, List<Class<?>>> finder) {
        Set<Class<?>> classes = new HashSet<>();

        List<String> pkgsToScan = JkConvert.toList(pkgsArr);
        pkgsToScan.forEach(pkg -> classes.addAll(finder.apply(pkg)));
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, RepoEntity.class));
        classes.removeIf(c -> Modifier.isAbstract(c.getModifiers()));
        classes.removeIf(c -> Modifier.isInterface(c.getModifiers()));

        return JkConvert.toList(classes);
    }

//    private static List<Class<?>> findClasses(String packageName) {
//        try {
//            File launcherPath = JkFiles.getLauncherPath(JkReflection.class).toFile();
//            List<Class<?>> classes = getClassesFromClassLoader(packageName);
//            classes.addAll(getClassesFromJar(launcherPath, packageName));
//            return classes;
//
//        } catch (Exception ex) {
//            throw new JkRuntimeException(ex);
//        }
//    }
//
//    private static List<Class<?>> getClassesFromClassLoader(String packageName) throws IOException, ClassNotFoundException {
//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        String path = packageName.replace('.', '/');
//        Enumeration<URL> resources = classLoader.getResources(path);
//        List<File> dirs = new ArrayList<>();
//        while (resources.hasMoreElements()) {
//            URL resource = resources.nextElement();
//            dirs.add(new File(resource.getFile()));
//        }
//        List<Class<?>> classes = new ArrayList<>();
//        for (File directory : dirs) {
//            classes.addAll(findClasses(directory, packageName));
//        }
//        return classes;
//    }
//    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
//            List<Class<?>> classes = new ArrayList<>();
//        if (!directory.exists()) {
//            return classes;
//        }
//
//        File[] files = directory.listFiles();
//        if(files != null) {
//            for (File file : files) {
//                if (file.isDirectory()) {
//                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
//                } else if (file.getName().endsWith(".class")) {
//                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
//                }
//            }
//        }
//
//        return classes;
//    }
//
//    private static List<Class<?>> getClassesFromJar(File jarFile, String packageName) throws IOException, ClassNotFoundException {
//        List<Class<?>> classes = new ArrayList<>();
//        try(JarFile file = new JarFile(jarFile)) {
//            for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements(); ) {
//                JarEntry jarEntry = entry.nextElement();
//                String name = jarEntry.getName().replace("/", ".");
//                if (name.startsWith(packageName) && name.endsWith(".class"))
//                    classes.add(Class.forName(name.substring(0, name.length() - 6)));
//            }
//        }
//        return classes;
//    }
//
    public static long getJvmStartTime() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

}