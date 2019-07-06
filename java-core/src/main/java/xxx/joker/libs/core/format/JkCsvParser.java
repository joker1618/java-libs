package xxx.joker.libs.core.format;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JkCsvParser {

    private List<String> excludes = new ArrayList<>();
    private List<String> toShow = new ArrayList<>();

    public JkCsvParser() {

    }
    public static JkCsvParser get() {
        return new JkCsvParser();
    }

    public <T> List<T> parseCsv(Path csvPath, Class<T> clazz) {
        return parseCsv(JkFiles.readLines(csvPath), clazz);
    }
    public <T> List<T> parseCsv(List<String> csvLines, Class<T> clazz) {
        List<T> toRet = new ArrayList<>();

        if(!csvLines.isEmpty()) {
            List<String> fnames = JkStrings.splitList(csvLines.get(0), JkOutput.DEF_SEP);
            Map<String, Field> fmap = JkStreams.toMapSingle(fnames, fn -> fn, f -> JkReflection.getFieldByName(clazz, f));
            for(int i = 1; i < csvLines.size(); i++) {
                T elem = JkReflection.createInstance(clazz);
                List<String> line = JkStrings.splitList(csvLines.get(i), JkOutput.DEF_SEP);
                for(int col = 0; col < fnames.size(); col++) {
                    Field f = fmap.get(fnames.get(col));
                    String strVal = line.get(col);
                    Object value = parseSingleValue(strVal, f.getType());
                    JkReflection.setFieldValue(elem, f, value);
                }
                toRet.add(elem);
            }
        }
        return toRet;
    }
    private boolean isOfType(Field f, Class<?>... classes) {
        return isOfType(f.getType(), classes);
    }
    private boolean isOfType(Class<?> clazz, Class<?>... classes) {
        for(Class<?> c : classes) {
            if(c == clazz) {
                return true;
            }
        }
        return false;
    }
    private Object parseSingleValue(String value, Class<?> fclazz) {
        Object o = null;

        if (value != null && !value.equalsIgnoreCase(JkOutput.PH_NULL)) {
            if (isOfType(fclazz, boolean.class, Boolean.class)) {
                o = Boolean.valueOf(value);
            } else if (isOfType(fclazz, int.class, Integer.class)) {
                o = JkConvert.toInt(value, fclazz.isPrimitive() ? 0 : null);
            } else if (isOfType(fclazz, long.class, Long.class)) {
                o = JkConvert.toLong(value, fclazz.isPrimitive() ? 0L : null);
            } else if (isOfType(fclazz, float.class, Float.class)) {
                o = JkConvert.toFloat(value, fclazz.isPrimitive() ? 0f : null);
            } else if (isOfType(fclazz, double.class, Double.class)) {
                o = JkConvert.toDouble(value, fclazz.isPrimitive() ? 0d : null);
            } else if (isOfType(fclazz, Path.class)) {
                o = Paths.get(value);
            } else if (isOfType(fclazz, File.class)) {
                o = new File(value);
            } else if (isOfType(fclazz, LocalTime.class)) {
                o = LocalTime.parse(value);
            } else if (isOfType(fclazz, LocalDate.class)) {
                o = LocalDate.parse(value);
            } else if (isOfType(fclazz, LocalDateTime.class)) {
                o = LocalDateTime.parse(value);
            } else if (JkReflection.isInstanceOf(fclazz, JkFormattable.class)) {
                o = JkReflection.createInstance(fclazz);
                ((JkFormattable) o).parse(value);
            } else if (JkReflection.isInstanceOf(fclazz, Enum.class)) {
                o = Enum.valueOf((Class)fclazz, value);
            } else if (isOfType(fclazz, String.class)) {
                o = value;
            }
        }

        return o;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public List<String> getToShow() {
        return toShow;
    }
}
