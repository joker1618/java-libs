package xxx.joker.libs.datalayer.export;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkFormattable;
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

public class TmpFmt {

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

    private Map<Class<?>, Function<?, String>> customClassFormats = new HashMap<>();
    private Map<Class<?>, Function<?, String>> customInstanceFormats = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> customClassParses = new HashMap<>();
    private Map<Class<?>, Function<String, ?>> customInstanceParses = new HashMap<>();

    private TmpFmt() {

    }

    public static TmpFmt get() {
        return new TmpFmt();
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
            List<String> fnames = JkStrings.splitList(csvLines.get(0), CsvSep.SEP_FIELD);
            Map<String, Field> fmap = JkStreams.toMapSingle(fnames, fn -> fn, f -> JkReflection.getFieldByName(clazz, f));
            for(int i = 1; i < csvLines.size(); i++) {
                T elem = JkReflection.createInstance(clazz);
                List<String> line = JkStrings.splitList(csvLines.get(i), CsvSep.SEP_FIELD);
                for(int col = 0; col < fnames.size(); col++) {
                    Field f = fmap.get(fnames.get(col));
                    if(f != null) {
                        String strVal = line.get(col);
                        Object value = parseSingleValue(strVal, f.getType());
                        JkReflection.setFieldValue(elem, f, value);
                    }
                }
                toRet.add(elem);
            }
        }
        return toRet;
    }
    private Object parseSingleValue(String value, Class<?> fclazz) {
        Object o = isOfClass(fclazz, String.class) ? "" : null;

        if (StringUtils.isNotBlank(value) && !value.equalsIgnoreCase(CsvSep.PH_NULL)) {
            Function<String, ?> parseFunc = retrieveCustomParse(fclazz);
            if(parseFunc != null) {
                o = parseFunc.apply(value);
            } else if (isOfClass(fclazz, boolean.class, Boolean.class)) {
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
                o = Enum.valueOf((Class)fclazz, value);
            } else if (isOfClass(fclazz, String.class)) {
                o = value;
            }
        }

        return o;
    }
    private Function<String, ?> retrieveCustomParse(Class<?> clazz) {
        // Search first in class format, if not found search in instance format
        Map.Entry<Class<?>, Function<String, ?>> found = JkStreams.findUnique(customClassParses.entrySet(), cc -> isOfClass(cc.getKey(), clazz));
        if(found != null) {
            return found.getValue();
        }
        found = JkStreams.findUnique(customInstanceParses.entrySet(), cc -> JkReflection.isInstanceOf(cc.getKey(), clazz));
        if(found != null) {
            return found.getValue();
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
        return formatCsv1(list, CsvSep.SEP_FIELD, Collections.emptyList());
    }
    private List<String> formatCsv1(Collection<?> coll, String fieldSep, List<String> fieldsToExclude)  {
        List<String> toRet = new ArrayList<>();

        if(!coll.isEmpty()) {
            List<?> list = JkConvert.toList(coll);
            List<Field> allFields = JkReflection.findAllFields(list.get(0).getClass());
            allFields.removeIf(f -> fieldsToExclude.contains(f.getName()));

            String header = JkStreams.join(allFields, CsvSep.SEP_FIELD, Field::getName);
            toRet.add(header);

            list.forEach(elem -> {
                String join = JkStreams.join(allFields, CsvSep.SEP_FIELD, f -> formatFieldValue(JkReflection.getFieldValue(elem, f), f.getType()));
                toRet.add(join);
            });
        }

        return toRet;
    }
    private <T> String formatFieldValue(T value, Class<?> fclazz) {
        if (value == null) {
            return "";
        }

        String toRet = "";
        Function<?, String> toStringFmt = retrieveCustomFormat(fclazz);
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
            toRet = ((JkFormattable)value).format();
        } else if (JkReflection.isInstanceOf(fclazz, Enum.class)) {
            toRet = ((Enum)value).name();
        } else if (isOfClass(fclazz, String.class)) {
            toRet = (String) value;
        } else if (JkReflection.isInstanceOf(fclazz, Collection.class)) {
            toRet = strf("({})", ((Collection)value).size());
        } else {
            toRet = value.toString();
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
    private Function<?, String> retrieveCustomFormat(Class<?> clazz) {
        // Search first in class format, if not found search in instance format
        Map.Entry<Class<?>, Function<?, String>> found = JkStreams.findUnique(customClassFormats.entrySet(), cc -> isOfClass(cc.getKey(), clazz));
        if(found != null) {
            return found.getValue();
        }
        found = JkStreams.findUnique(customInstanceFormats.entrySet(), cc -> JkReflection.isInstanceOf(clazz, cc.getKey()));
        if(found != null) {
            return found.getValue();
        }
        return null;
    }

    public <T> void addCustomClassFormat(Class<T> clazz, Function<T, String> formatFunc) {
        customClassFormats.put(clazz, formatFunc);
    }
    public <T> void addCustomInstanceFormat(Class<T> clazz, Function<T, String> formatFunc) {
        customInstanceFormats.put(clazz, formatFunc);
    }
    public <T> void addCustomClassParse(Class<T> clazz, Function<String, T> parseFunc) {
        customClassParses.put(clazz, parseFunc);
    }
    public <T> void addCustomInstanceParse(Class<T> clazz, Function<String, T> parseFunc) {
        customInstanceParses.put(clazz, parseFunc);
    }

}
