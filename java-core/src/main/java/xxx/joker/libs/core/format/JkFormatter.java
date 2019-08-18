package xxx.joker.libs.core.format;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkFormatter {

    private static class CsvSep {
        public static final String SEP_FIELD = "|";
        public static final String SEP_LIST = ";";

        public static final String PH_SEP_FIELD = "@_PIPE_@";
        public static final String PH_SEP_LIST = "@_SCL_@";
        public static final String PH_TAB = "@_TAB_@";
        public static final String PH_NEWLINE = "@_LF_@";
        public static final String PH_NULL = "@_NUL_@";
    }

    private DateTimeFormatter DTF_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    private DateTimeFormatter DTF_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private Map<Field, Function<?, String>> fieldFormats= new HashMap<>();
    private Map<Class<?>, Function<?, String>> classFormats = new HashMap<>();
    private Map<Class<?>, Function<?, String>> instanceFormats = new HashMap<>();
    private Map<Field, Function<String, ?>> fieldParses = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> classParses = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> instanceParses = new HashMap<>();

    private JkFormatter() {

    }

    public static JkFormatter get() {
        return new JkFormatter();
    }

    public <T> List<T> parseCsv(Path csvPath, Class<T> clazz) {
        return parseCsv(JkFiles.readLines(csvPath), clazz);
    }
    public <T> List<T> parseCsv(List<String> csvLines, Class<T> clazz) {
        return parseCsv1(csvLines, clazz, CsvSep.SEP_FIELD);
    }
    private <T> List<T> parseCsv1(List<String> csvLines, Class<T> clazz, String fieldSep)  {
        List<T> toRet = new ArrayList<>();

        if(!csvLines.isEmpty()) {
            List<String> fnames = JkStrings.splitList(csvLines.get(0), fieldSep);
            Map<String, Field> fmap = JkStreams.toMapSingle(fnames, fn -> fn, f -> JkReflection.getFieldByName(clazz, f));
            for(int i = 1; i < csvLines.size(); i++) {
                T elem = JkReflection.createInstance(clazz);
                List<String> line = JkStrings.splitList(csvLines.get(i), fieldSep);
                for(int col = 0; col < fnames.size(); col++) {
                    Field f = fmap.get(fnames.get(col));
                    if(f != null) {
                        String strVal = line.get(col);
                        Object value = parseSingleValue(strVal, f);
                        JkReflection.setFieldValue(elem, f, value);
                    }
                }
                toRet.add(elem);
            }
        }
        return toRet;
    }
    public Object parseSingleValue(String value, Field field) {
        Class<?> fclazz = field.getType();
        Object o = isOfClass(fclazz, String.class) ? "" : null;

        Function<String, ?> parseFunc = retrieveCustomParser(field);
        if (StringUtils.isNotBlank(value)) {
            if (parseFunc != null) {
                o = parseFunc.apply(value);
            } else if (!value.equalsIgnoreCase(CsvSep.PH_NULL)) {
                if (isOfClass(fclazz, boolean.class, Boolean.class)) {
                    o = Boolean.valueOf(value);
                } else if (isOfClass(fclazz, int.class, Integer.class)) {
                    o = JkConvert.toInt(value, fclazz.isPrimitive() ? 0 : null);
                } else if (isOfClass(fclazz, long.class, Long.class)) {
                    o = JkConvert.toLong(value, fclazz.isPrimitive() ? 0L : null);
                } else if (isOfClass(fclazz, float.class, Float.class)) {
                    o = JkConvert.toFloat(value, fclazz.isPrimitive() ? 0f : null);
                } else if (isOfClass(fclazz, double.class, Double.class)) {
                    o = JkConvert.toDouble(value, fclazz.isPrimitive() ? 0d : null);
                } else if (isOfClass(fclazz, Path.class)) {
                    o = Paths.get(value);
                } else if (isOfClass(fclazz, File.class)) {
                    o = new File(value);
                } else if (isOfClass(fclazz, LocalTime.class)) {
                    o = LocalTime.parse(value);
                } else if (isOfClass(fclazz, LocalDate.class)) {
                    o = LocalDate.parse(value);
                } else if (isOfClass(fclazz, LocalDateTime.class)) {
                    o = LocalDateTime.parse(value);
                } else if (JkReflection.isInstanceOf(fclazz, JkFormattable.class)) {
                    o = JkReflection.createInstance(fclazz);
                    ((JkFormattable) o).parse(value);
                } else if (JkReflection.isInstanceOf(fclazz, Enum.class)) {
                    o = Enum.valueOf((Class) fclazz, value);
                } else if (isOfClass(fclazz, String.class)) {
                    o = value;
                }
            }
        }

        return o;
    }
    private Function<String, ?> retrieveCustomParser(Field field) {
        // Search in field format
        Map.Entry<Field, Function<String, ?>> found1 = JkStreams.findUnique(fieldParses.entrySet(), field::equals);
        if(found1 != null) {
            return found1.getValue();
        }
        // Search in class format
        Class<?> fclazz = field.getType();
        Map.Entry<Class<?>, Function<String, ?>> found2 = JkStreams.findUnique(classParses.entrySet(), cc -> isOfClass(cc.getKey(), fclazz));
        if(found2 != null) {
            return found2.getValue();
        }
        // Search in instance format
        found2 = JkStreams.findUnique(instanceParses.entrySet(), cc -> JkReflection.isInstanceOf(cc.getKey(), fclazz));
        if(found2 != null) {
            return found2.getValue();
        }
        return null;
    }

    public List<String> formatCsv(Collection<?> list)  {
        return formatCsv1(list, CsvSep.SEP_FIELD);
    }
    public List<String> formatCsvExclude(Collection<?> list, String... fieldsToExclude)  {
        return formatCsv1(list, CsvSep.SEP_FIELD, JkConvert.toList(fieldsToExclude));
    }
    private List<String> formatCsv1(Collection<?> list, String fieldSep)  {
        return formatCsv1(list, fieldSep, Collections.emptyList());
    }
    private List<String> formatCsv1(Collection<?> coll, String fieldSep, List<String> fieldsToExclude)  {
        List<String> toRet = new ArrayList<>();

        if(!coll.isEmpty()) {
            List<?> list = JkConvert.toList(coll);
            List<Field> allFields = JkReflection.findAllFields(list.get(0).getClass());
            allFields.removeIf(f -> fieldsToExclude.contains(f.getName()));

            String header = JkStreams.join(allFields, fieldSep, Field::getName);
            toRet.add(header);

            list.forEach(elem -> {
                String join = JkStreams.join(allFields, CsvSep.SEP_FIELD, f -> formatFieldValue(JkReflection.getFieldValue(elem, f), f));
                toRet.add(join);
            });
        }

        return toRet;
    }
    public <T> String formatFieldValue(T value, Field field) {
        String toRet = "";

        if (value != null) {
            Class<?> fclazz = field.getType();
            Function<?, String> toStringFmt = retrieveCustomFormat(fclazz, field);
            if (toStringFmt != null) {
                Function<T, String> fmtFunc = (Function<T, String>) toStringFmt;
                toRet = fmtFunc.apply(value);
            } else if (isOfClass(fclazz, boolean.class, Boolean.class)) {
                toRet = ((Boolean) value) ? "true" : "false";
            } else if (isOfClass(fclazz, File.class, Path.class)) {
                toRet = value.toString();
            } else if (isOfClass(fclazz, LocalTime.class)) {
                toRet = DTF_TIME.format((LocalTime) value);
            } else if (isOfClass(fclazz, LocalDate.class)) {
                toRet = DTF_DATE.format((LocalDate) value);
            } else if (isOfClass(fclazz, LocalDateTime.class)) {
                toRet = DTF_DATETIME.format((LocalDateTime) value);
            } else if (isOfClass(fclazz, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class)) {
                toRet = String.valueOf(value);
            } else if (JkReflection.isInstanceOf(fclazz, JkFormattable.class)) {
                toRet = ((JkFormattable) value).format();
            } else if (JkReflection.isInstanceOf(fclazz, Enum.class)) {
                toRet = ((Enum) value).name();
            } else if (isOfClass(fclazz, String.class)) {
                toRet = (String) value;
            } else if (JkReflection.isInstanceOf(fclazz, Collection.class)) {
                toRet = strf("({})", ((Collection) value).size());
            } else {
                toRet = value.toString();
            }
        }

        return toRet;
    }
    private static boolean isOfClass(Class<?> clazz, Class<?>... classes) {
        for(Class<?> c : classes) {
            if(c == clazz) {
                return true;
            }
        }
        return false;
    }
    private Function<?, String> retrieveCustomFormat(Class<?> clazz, Field field) {
        // Search in field format
        Map.Entry<Field, Function<?, String>> found1 = JkStreams.findUnique(fieldFormats.entrySet(), field::equals);
        if(found1 != null) {
            return found1.getValue();
        }
        // Search in class format
        Map.Entry<Class<?>, Function<?, String>> found2 = JkStreams.findUnique(classFormats.entrySet(), cc -> isOfClass(cc.getKey(), clazz));
        if(found2 != null) {
            return found2.getValue();
        }
        // Search in instance format
        found2 = JkStreams.findUnique(instanceFormats.entrySet(), cc -> JkReflection.isInstanceOf(clazz, cc.getKey()));
        if(found2 != null) {
            return found2.getValue();
        }
        return null;
    }

    public <T> void setFieldFormat(Class<T> clazz, String fieldName, Function<T, String> formatFunc) {
        fieldFormats.put(JkReflection.getFieldByName(clazz, fieldName), formatFunc);
    }
    public <T> void setClassFormat(Class<T> clazz, Function<T, String> formatFunc) {
        classFormats.put(clazz, formatFunc);
    }
    public <T> void setInstanceFormat(Class<T> clazz, Function<T, String> formatFunc) {
        instanceFormats.put(clazz, formatFunc);
    }
    public <T> void setFieldParse(Class<T> clazz, String fieldName, Function<String, T> parseFunc) {
        fieldParses.put(JkReflection.getFieldByName(clazz, fieldName), parseFunc);
    }
    public <T> void setClassParse(Class<T> clazz, Function<String, T> parseFunc) {
        classParses.put(clazz, parseFunc);
    }
    public <T> void setInstanceParse(Class<T> clazz, Function<String, T> parseFunc) {
        instanceParses.put(clazz, parseFunc);
    }

}
