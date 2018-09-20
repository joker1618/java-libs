package xxx.joker.libs.javalibs.repository;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.javalibs.utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

public class JkRepository {

    private static final String LINE_SEP = StringUtils.LF;
    //	private static final CsvSep FIELD_SEP = new CsvSep(";", ";");
//	private static final CsvSep LIST_SEP = new CsvSep(",", ",");
    private static final CsvSep FIELD_SEP = new CsvSep("###FIELD_SEP###", ";");
    private static final CsvSep LIST_SEP = new CsvSep("###LIST_SEP###", ",");

    private static final String PLACEHOLDER_TAB = "##TAB##";
    private static final String PLACEHOLDER_NEWLINE = "##NEWLINE##";

    private static final Map<Class<?>, Map<Integer,AnnField>> fieldsMap = new HashMap<>();

    private JkRepository() {}

    public static <T extends JkRepoTable> List<T> load(Path repoPath) throws IOException, ClassNotFoundException {
        if(!Files.exists(repoPath)) {
            return Collections.emptyList();
        }

        List<String> lines = JkStreams.filter(Files.readAllLines(repoPath), StringUtils::isNotBlank);
        if(lines.isEmpty()) {
            return Collections.emptyList();
        }

        String clazzName = lines.remove(0);
        Class<?> repoClazz = Class.forName(clazzName);

        Path depsPath = createDependenciesPath(repoPath);
        List<String> depsLines = JkStreams.filter(Files.readAllLines(depsPath), StringUtils::isNotBlank);
        Map<String, String> depsMap = new HashMap<>();
        if(!depsLines.isEmpty()) {
            depsMap.putAll(JkStreams.toMapSingle(depsLines, l -> l.split(":")[0], l -> l.split(":")[1]));
        }

        List<Object> loadList = JkStreams.map(lines, line -> parseElem(repoClazz, line, depsMap));
        return JkStreams.map(loadList, l -> (T)l);
    }

    public static <T extends JkRepoTable> void save(Path repoPath, Collection<T> elems) throws IOException {
        Path depsPath = createDependenciesPath(repoPath);

        if(elems.isEmpty()) {
            Files.deleteIfExists(repoPath);
            Files.deleteIfExists(depsPath);

        } else {
            List<StringCSV> csvList = elems.stream()
                    .distinct()
                    .sorted()
                    .map(JkRepository::formatElem)
                    .collect(Collectors.toList());

            List<String> mainLines = JkStreams.map(csvList, StringCSV::getMainValue);
            mainLines.add(0, elems.toArray()[0].getClass().getName());

            List<String> depLines = csvList.stream().flatMap(csv -> csv.getDependencies().stream())
                    .sorted().distinct().collect(Collectors.toList());

            JkFiles.writeFile(repoPath, mainLines, true);
            JkFiles.writeFile(depsPath, depLines, true);
        }
    }

    public static <T extends JkRepoTable> void update(Path repoPath, Collection<T> elems) throws IOException, ClassNotFoundException {
        Set<T> elemSet = new TreeSet<>(elems);

        List<T> existings = load(repoPath);
        elemSet.addAll(existings);

        save(repoPath, elemSet);
    }


    private static Path createDependenciesPath(Path repoPath) {
        String fname = strf("%s.dependencies", JkFiles.getFileName(repoPath));

        String ext = JkFiles.getExtension(repoPath);
        if(!ext.isEmpty()) {
            fname = strf("%s.%s", fname, ext);
        }

        return JkFiles.getParent(repoPath).resolve(fname);
    }

    private static Object parseElem(Class<?> elemClazz, String line, Map<String, String> depsMap) {
        try {
            parseClassFields(elemClazz);
            Object instance  = elemClazz.newInstance();
            List<String> row = JkStrings.splitFieldsList(line, FIELD_SEP.safeSep);
            for(Map.Entry<Integer,AnnField> entry : fieldsMap.get(elemClazz).entrySet()) {
                if(entry.getKey() < row.size()) {
                    Object o = fromStringValue(row.get(entry.getKey()), entry.getValue(), depsMap);
                    entry.getValue().setValue(instance, o);
                }
            }
            return instance;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseClassFields(Class<?> clazz) {
        if(fieldsMap.containsKey(clazz)) {
            return;
        }

        Map<Integer, AnnField> annMap = new HashMap<>();
        fieldsMap.put(clazz, annMap);

        List<Field> fields = JkReflection.getFieldsByAnnotation(clazz, JkRepoField.class);

        if(!fields.isEmpty()) {
            List<AnnField> annFields = JkStreams.map(fields, AnnField::new);

            for (AnnField annField : annFields) {
                if (annField.ann.index() < 0) {
                    throw new IllegalArgumentException(strf("Negative index not allowed. Field %s", annField.field.getName()));
                }
                if (annMap.containsKey(annField.ann.index())) {
                    throw new IllegalArgumentException(strf("Duplicated index %d", annField.ann.index()));
                }
                if(isImplOf(annField.field.getType(), JkRepoTable.class)) {
                    parseClassFields(annField.field.getType());
                }
                if(isImplOf(annField.ann.collectionType(), JkRepoTable.class)) {
                    parseClassFields(annField.ann.collectionType());
                }
                annMap.put(annField.ann.index(), annField);
            }
        }
    }


    private static StringCSV formatElem(JkRepoTable elem) {
        parseClassFields(elem.getClass());

        StringCSV toRet = new StringCSV();

        try {
            // Parse instance
            List<String> deps = new ArrayList<>();
            int numFields = getNumFields(elem);
            List<String> row = Stream.generate(() -> "").limit(numFields).collect(Collectors.toList());
            Map<Integer, AnnField> annMap = fieldsMap.get(elem.getClass());
            for (Integer index : annMap.keySet()) {
                AnnField annField = annMap.get(index);
                Object value = annField.getValue(elem);
                StringCSV str = toStringValue(value, annField);
                row.set(index, str.mainValue);
                deps.addAll(str.dependencies);
            }

            toRet.mainValue = JkStreams.join(row, FIELD_SEP.safeSep);
            toRet.dependencies = deps;

            return toRet;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getNumFields(JkRepoTable elem) {
        return fieldsMap.get(elem.getClass()).keySet().stream().mapToInt(i->i).max().orElse(-1) + 1;
    }

    private static StringCSV toStringValue(Object value, AnnField annField) {
        StringCSV retVal = new StringCSV();

        Class<?> fclazz = annField.field.getType();

        if(fclazz == List.class || fclazz == Set.class || fclazz.isArray()) {
            Class<?> elemClazz = annField.ann.collectionType();
            if(value != null) {
                List list;
                if(fclazz.isArray()) 		 list = Arrays.asList((Object[])value);
                else if(fclazz == Set.class) list = JkConverter.toArrayList((Set)value);
                else 						 list = (List)value;

                if(!list.isEmpty()) {
                    List<StringCSV> csvList = JkStreams.map(list, e -> toStringSingleValue(e, elemClazz));
                    retVal.mainValue = JkStreams.join(csvList, LIST_SEP.safeSep, StringCSV::getMainValue);
                    retVal.dependencies = csvList.stream().flatMap(c -> c.getDependencies().stream()).collect(Collectors.toList());
                }
            }

        } else {
            retVal = toStringSingleValue(value, fclazz);
        }

        return retVal;
    }

    private static StringCSV toStringSingleValue(Object value, Class<?> fclazz) {
        try {
            StringCSV str = new StringCSV();

            if (value == null) {
                str.mainValue = "";
            } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
                str.mainValue = ((Boolean) value) ? "true" : "false";
            } else if (Arrays.asList(File.class, Path.class).contains(fclazz)) {
                str.mainValue = value.toString();
            } else if (fclazz == LocalTime.class) {
                str.mainValue = DateTimeFormatter.ISO_TIME.format((LocalTime) value);
            } else if (fclazz == LocalDate.class) {
                str.mainValue = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
            } else if (fclazz == LocalDateTime.class) {
                str.mainValue = DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) value);
            } else if (isImplOf(fclazz, JkRepoTable.class)) {
                JkRepoTable cel = (JkRepoTable) value;
                str.mainValue = cel.getPrimaryKey();
                StringCSV csv = formatElem(cel);
                String ml = strf("%s:%s", getCsvElementsPrefix(cel), csv.mainValue);
                str.dependencies.add(ml);
                str.dependencies.addAll(csv.dependencies);
            } else {
                str.mainValue = String.valueOf(value).replaceAll("\t", PLACEHOLDER_TAB).replaceAll("\n", PLACEHOLDER_NEWLINE);
            }

            return str;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    private static Object fromStringValue(String value, AnnField annField, Map<String, String> depMap) {
        Object retVal;

        Class<?> fclazz = annField.field.getType();
        if(fclazz.isArray() || fclazz == Set.class || fclazz == List.class) {
            List<String> strElems = JkStrings.splitFieldsList(value, LIST_SEP.safeSep);
            Class<?> elemClazz = annField.ann.collectionType();
            List<Object> list = JkStreams.map(strElems, elem -> fromStringSingleValue(elem, elemClazz, depMap));
            if(fclazz.isArray()) {
                retVal = toArray(list,fclazz);
            } else if(fclazz == Set.class) {
                retVal = isImplOf(elemClazz, Comparable.class) ? JkConverter.toTreeSet(list) : JkConverter.toHashSet(list);
            } else {
                retVal = list;
            }

        } else {
            retVal = fromStringSingleValue(value, fclazz, depMap);
        }

        return retVal;
    }

    private static Object toArray(List list, Class<?> fclazz) {
        Object[] typeArray = null;

        if(fclazz == Boolean[].class)		typeArray = new Boolean[0];
        if(fclazz == Integer[].class)		typeArray = new Integer[0];
        if(fclazz == Long[].class)			typeArray = new Long[0];
        if(fclazz == Float[].class)			typeArray = new Float[0];
        if(fclazz == Double[].class)		typeArray = new Double[0];
        if(fclazz == File[].class)			typeArray = new File[0];
        if(fclazz == Path[].class)			typeArray = new Path[0];
        if(fclazz == LocalTime[].class)		typeArray = new LocalTime[0];
        if(fclazz == LocalDate[].class)		typeArray = new LocalDate[0];
        if(fclazz == LocalDateTime[].class)	typeArray = new LocalDateTime[0];
        if(fclazz == String[].class)		typeArray = new String[0];

        return list.toArray(typeArray);
    }

    private static Object fromStringSingleValue(String value, Class<?> fclazz, Map<String, String> depMap) {
        Object o;

        try {
            if (StringUtils.isEmpty(value)) {
                o = fclazz == String.class ? "" : null;
            } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
                o = Boolean.valueOf(value);
            } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
                o = Boolean.valueOf(value);
            } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
                o = JkConverter.stringToInteger(value);
            } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
                o = JkConverter.stringToInteger(value);
            } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
                o = JkConverter.stringToLong(value);
            } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
                o = JkConverter.stringToLong(value);
            } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
                o = JkConverter.stringToDouble(value);
            } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
                o = JkConverter.stringToDouble(value);
            } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
                o = JkConverter.stringToFloat(value);
            } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
                o = JkConverter.stringToFloat(value);
            } else if (fclazz == Path.class) {
                o = Paths.get(value);
            } else if (fclazz == File.class) {
                o = new File(value);
            } else if (fclazz == LocalTime.class) {
                o = LocalTime.parse(value, DateTimeFormatter.ISO_TIME);
            } else if (fclazz == LocalDate.class) {
                o = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
            } else if (fclazz == LocalDateTime.class) {
                o = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            } else if (isImplOf(fclazz, JkRepoTable.class)) {
                String depLine = depMap.get(getCsvElementsPrefix(fclazz.getName(), value));
                o = parseElem(fclazz, depLine, depMap);
            } else {
                o = value.replaceAll(PLACEHOLDER_TAB, "\t").replaceAll(PLACEHOLDER_NEWLINE, "\n");
            }

            return o;

        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getCsvElementsPrefix(JkRepoTable elem) {
        return getCsvElementsPrefix(elem.getClass().getName(), elem.getPrimaryKey());
    }
    private static String getCsvElementsPrefix(String className, String elemID) {
        return strf("%s_%s", className, elemID);
    }

    private static boolean isImplOf(Class<?> clazz, Class<?> expected) {
        List<Class<?>> interfaces = new ArrayList<>();
        interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
        if(clazz.getSuperclass() != null) {
            interfaces.addAll(Arrays.asList(clazz.getSuperclass().getInterfaces()));
        }
        return interfaces.contains(expected);
    }


    private static class AnnField {
        JkRepoField ann;
        Field field;

        AnnField(Field field) {
            this.ann = field.getAnnotation(JkRepoField.class);
            this.field = field;
        }

        Object getValue(Object elem) throws IllegalAccessException {
            boolean facc = field.isAccessible();
            field.setAccessible(true);
            Object obj = field.get(elem);
            field.setAccessible(facc);
            return obj;
        }

        void setValue(Object elem, Object value) throws IllegalAccessException {
            boolean facc = field.isAccessible();
            field.setAccessible(true);
            field.set(elem, value);
            field.setAccessible(facc);
        }
    }

    private static class CsvSep {
        String safeSep;
        String simpleSep;

        CsvSep(String safeSep, String simpleSep) {
            this.safeSep = safeSep;
            this.simpleSep = simpleSep;
        }
    }

    private static class StringCSV {
        String mainValue = "";
        List<String> dependencies = new ArrayList<>();

        public String getMainValue() {
            return mainValue;
        }

        public void setMainValue(String mainValue) {
            this.mainValue = mainValue;
        }

        public List<String> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<String> dependencies) {
            this.dependencies = dependencies;
        }
    }

}
