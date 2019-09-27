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
import java.util.function.Consumer;
import java.util.function.Function;

import static xxx.joker.libs.core.runtimes.JkReflection.*;

public class JkFormatter {

    public static class CsvSep {
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

    private Map<Field, Function<?, String>> fieldFormats = new HashMap<>();
    private Map<Class<?>, Function<?, String>> classFormats = new HashMap<>();
    private Map<Class<?>, Function<?, String>> instanceFormats = new HashMap<>();
    private Map<Field, Function<String, ?>> fieldParses = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> classParses = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> instanceParses = new HashMap<>();

    private Map<String, Function<?, String>> customFormats = new LinkedHashMap<>();
    private List<Consumer<?>> afterParseFunctions = new ArrayList<>();

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
            Map<String, Field> fmap = JkStreams.toMapSingle(fnames, fn -> fn, f -> getFieldByName(clazz, f));
            for(int i = 1; i < csvLines.size(); i++) {
                T elem = JkReflection.createInstance(clazz);
                List<String> line = JkStrings.splitList(csvLines.get(i), fieldSep);
                for(int col = 0; col < fnames.size(); col++) {
                    Field f = fmap.get(fnames.get(col));
                    if(f != null) {
                        String strVal = line.get(col);
                        Object value = parseFieldValue(strVal, f);
                        JkReflection.setFieldValue(elem, f, value);
                    }
                }
                afterParseFunctions.forEach(f -> ((Consumer<Object>)f).accept(elem));
                toRet.add(elem);
            }
        }
        return toRet;
    }
    public Object parseFieldValue(String value, Field field) {
        Class<?> fclazz = field.getType();
        Object o = isOfClass(fclazz, String.class) ? "" : null;

        Function<String, ?> parseFunc = retrieveCustomParser(fclazz, field);
        if (StringUtils.isNotBlank(value)) {
            if (parseFunc != null) {
                o = parseFunc.apply(value);
            } else if(isInstanceOf(fclazz, Collection.class)) {
                List<String> elems = JkStrings.splitList(value, CsvSep.SEP_LIST);
                Class<?> collType = getParametrizedTypes(field)[0];
                List<?> list = JkStreams.map(elems, el -> parseFieldValue(el, collType));
                if(isInstanceOf(fclazz, List.class)) {
                    o = list;
                } else if(isInstanceOf(collType, Comparable.class)) {
                    o = JkConvert.toTreeSet(list);
                } else {
                    o = JkConvert.toHashSet(list);
                }
            } else {
                o = parseFieldValue(value, fclazz);
            }
        }

        return o;
    }
    public Object parseFieldValue(String value, Class<?> valueClazz) {
        Object o = isOfClass(valueClazz, String.class) ? "" : null;

        Function<String, ?> parseFunc = retrieveCustomParser(valueClazz, null);
        if (StringUtils.isNotBlank(value)) {
            if (parseFunc != null) {
                o = parseFunc.apply(value);
            } else if (!value.equalsIgnoreCase(CsvSep.PH_NULL)) {
                if (isOfClass(valueClazz, boolean.class, Boolean.class)) {
                    o = Boolean.valueOf(value);
                } else if (isOfClass(valueClazz, int.class, Integer.class)) {
                    o = JkConvert.toInt(value, valueClazz.isPrimitive() ? 0 : null);
                } else if (isOfClass(valueClazz, long.class, Long.class)) {
                    o = JkConvert.toLong(value, valueClazz.isPrimitive() ? 0L : null);
                } else if (isOfClass(valueClazz, float.class, Float.class)) {
                    o = JkConvert.toFloat(value, valueClazz.isPrimitive() ? 0f : null);
                } else if (isOfClass(valueClazz, double.class, Double.class)) {
                    o = JkConvert.toDouble(value, valueClazz.isPrimitive() ? 0d : null);
                } else if (isOfClass(valueClazz, Path.class)) {
                    o = Paths.get(value);
                } else if (isOfClass(valueClazz, File.class)) {
                    o = new File(value);
                } else if (isOfClass(valueClazz, LocalTime.class)) {
                    o = LocalTime.parse(value);
                } else if (isOfClass(valueClazz, LocalDate.class)) {
                    o = LocalDate.parse(value);
                } else if (isOfClass(valueClazz, LocalDateTime.class)) {
                    o = LocalDateTime.parse(value);
                } else if (isInstanceOf(valueClazz, JkFormattable.class)) {
                    o = JkReflection.createInstance(valueClazz);
                    ((JkFormattable) o).parse(value);
                } else if (isInstanceOf(valueClazz, Enum.class)) {
                    o = Enum.valueOf((Class) valueClazz, value);
                } else if (isOfClass(valueClazz, String.class)) {
                    o = value;
                }
            }
        }

        return o;
    }
    private Function<String, ?> retrieveCustomParser(Class<?> clazz, Field field) {
        // Search in field format
        Function<String, ?> func;
        if(field != null) {
            func = fieldParses.get(field);
            if (func != null) {
                return func;
            }
        }
        // Search in class format
        func = classParses.get(clazz);
        if(func != null) {
            return func;
        }
        // Search in instance format
        List<Function<String, ?>> functions = JkStreams.filterMap(instanceParses.entrySet(), cc -> isInstanceOf(clazz, cc.getKey()), Map.Entry::getValue);
        if(!functions.isEmpty()) {
            return functions.get(0);
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
            if(!customFormats.isEmpty()) {
                header += fieldSep + JkStreams.join(customFormats.keySet(), fieldSep);
            }
            toRet.add(header);

            list.forEach(elem -> {
                String join = JkStreams.join(allFields, fieldSep, f -> formatFieldValue(getFieldValue(elem, f), f));
                if(!customFormats.isEmpty()) {
                    join += fieldSep + JkStreams.join(customFormats.values(), fieldSep, cf -> ((Function<Object, String>) cf).apply(elem));
                }
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
            } else if (isInstanceOf(fclazz, Collection.class)) {
                Collection coll = (Collection) value;
                Class<?> collType = getParametrizedTypes(field)[0];
                toRet = JkStreams.join(coll, CsvSep.SEP_LIST, el -> formatFieldValue(el, collType));
            } else {
                toRet = formatFieldValue(value, fclazz);
            }
        }

        return toRet;
    }
    public String formatFieldValue(Object value, Class<?> valueClazz) {
        String toRet;

        if (value == null) {
            toRet = CsvSep.PH_NULL;
        } else {
            Function<?, String> toStringFmt = retrieveCustomFormat(valueClazz, null);
            if (toStringFmt != null) {
                Function<Object, String> fmtFunc = (Function<Object, String>) toStringFmt;
                toRet = fmtFunc.apply(value);
            } else if (isOfClass(valueClazz, boolean.class, Boolean.class)) {
                toRet = ((Boolean) value) ? "true" : "false";
            } else if (isOfClass(valueClazz, File.class, Path.class)) {
                toRet = value.toString();
            } else if (isOfClass(valueClazz, LocalTime.class)) {
                toRet = DTF_TIME.format((LocalTime) value);
            } else if (isOfClass(valueClazz, LocalDate.class)) {
                toRet = DTF_DATE.format((LocalDate) value);
            } else if (isOfClass(valueClazz, LocalDateTime.class)) {
                toRet = DTF_DATETIME.format((LocalDateTime) value);
            } else if (isOfClass(valueClazz, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class)) {
                toRet = String.valueOf(value);
            } else if (isInstanceOf(valueClazz, JkFormattable.class)) {
                toRet = ((JkFormattable) value).format();
            } else if (isInstanceOf(valueClazz, Enum.class)) {
                toRet = ((Enum) value).name();
            } else if (isOfClass(valueClazz, String.class)) {
                toRet = (String) value;
            } else {
                toRet = value.toString();
            }
        }

        return toRet;
    }
    private Function<?, String> retrieveCustomFormat(Class<?> clazz, Field field) {
        // Search in field format
        Function<?, String> func;
        if(field != null) {
            func = fieldFormats.get(field);
            if (func != null) {
                return func;
            }
        }
        // Search in class format
        func = classFormats.get(clazz);
        if(func != null) {
            return func;
        }
        // Search in instance format
        List<Function<?, String>> functions = JkStreams.filterMap(instanceFormats.entrySet(), cc -> isInstanceOf(clazz, cc.getKey()), Map.Entry::getValue);
        if(!functions.isEmpty()) {
            return functions.get(0);
        }
        return null;
    }

    public <T> void setFieldFormat(Class<T> clazz, String fieldName, Function<T, String> formatFunc) {
        fieldFormats.put(getFieldByName(clazz, fieldName), formatFunc);
    }
    public <T> void setClassFormat(Class<T> clazz, Function<T, String> formatFunc) {
        classFormats.put(clazz, formatFunc);
    }
    public <T> void setInstanceFormat(Class<T> clazz, Function<T, String> formatFunc) {
        instanceFormats.put(clazz, formatFunc);
    }
    public <T> void setFieldParse(Class<T> clazz, String fieldName, Function<String, T> parseFunc) {
        fieldParses.put(getFieldByName(clazz, fieldName), parseFunc);
    }
    public <T> void setClassParse(Class<T> clazz, Function<String, T> parseFunc) {
        classParses.put(clazz, parseFunc);
    }
    public <T> void setInstanceParse(Class<T> clazz, Function<String, T> parseFunc) {
        instanceParses.put(clazz, parseFunc);
    }

    public <T> void setCustomFormat(String headerName, Function<T, String> formatFunc) {
        customFormats.put(headerName, formatFunc);
    }
    public <T> void setAfterParseFunction(Consumer<T> afterParseFunc) {
        afterParseFunctions.add(afterParseFunc);
    }

}
