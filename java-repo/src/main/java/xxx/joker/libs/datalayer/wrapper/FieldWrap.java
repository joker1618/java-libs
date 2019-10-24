package xxx.joker.libs.datalayer.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.datalayer.design.*;
import xxx.joker.libs.datalayer.exceptions.RepoError;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static xxx.joker.libs.core.runtimes.JkReflection.isInstanceOf;
import static xxx.joker.libs.core.runtimes.JkReflection.isOfClass;
import static xxx.joker.libs.datalayer.config.RepoConfig.CsvSep.*;


public class FieldWrap {

    private static final Logger LOG = LoggerFactory.getLogger(FieldWrap.class);

    private static final DateTimeFormatter DTF_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final Field field;
    // The following fields are used to avoid reflection every time
    private Class<?> fieldType;
    private Class<?> collType;
    private Set<Class<?>> directives;

    FieldWrap(Field field) {
        this.field = field;
        this.directives = new HashSet<>();
        initWrapper();
    }
    private void initWrapper() {
        fieldType = field.getType();

        Class<?>[] types = JkReflection.getParametrizedTypes(field);
        if(types.length > 0) {
            collType = types[0];
        }

        boolean allowNullStr = field.getAnnotation(AllowNullString.class) != null ||
                field.getDeclaringClass().getAnnotation(AllowNullString.class) != null;
        if(allowNullStr) {
            directives.add(AllowNullString.class);
        }

        if(field.getAnnotation(ResourcePath.class) != null) {
            directives.add(ResourcePath.class);
        }
        if(field.getAnnotation(EntityPK.class) != null) {
            directives.add(EntityPK.class);
        }
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Set<Class<?>> getDirectives() {
        return directives;
    }

    public Class<?> getFieldType() {
        return fieldType;
    }
    public Class<?> getFieldTypeFlat() {
        return isCollection() ? collType : fieldType;
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isEntity() {
        return isInstanceOf(getFieldType(), RepoEntity.class);
    }
    public boolean isEntityFlat() {
        return isEntity() || isEntityColl();
    }
    public boolean isEntityColl() {
        return collType != null && isInstanceOf(collType, RepoEntity.class);
    }

    public boolean isList() {
        return getFieldType() == List.class;
    }
    public boolean isSet() {
        return getFieldType() == Set.class;
    }
    public boolean isCollection() {
        return isList() || isSet();
    }

    public boolean isComparableFlat() {
        Class<?> toTest = isCollection() ? collType : fieldType;
        return isInstanceOf(toTest, Comparable.class);
    }

    public Class<?> getCollType() {
        return collType;
    }

    public Object getValue(RepoEntity instance) {
        return JkReflection.getFieldValue(instance, field);
    }
    public <T> T getValueCast(RepoEntity instance) {
        return (T) JkReflection.getFieldValue(instance, field);
    }

    public void setValue(RepoEntity instance, Object value) {
        JkReflection.setFieldValue(instance, field, value);
    }
    public void addToColl(RepoEntity instance, Object value) {
        if(isCollection()) {
            Collection coll = getValueCast(instance);
            coll.add(value);
        }
    }

    public boolean isAllowNull() {
        return field.isAnnotationPresent(AllowNullString.class);
    }

    public boolean isResourcePath() {
        return field.isAnnotationPresent(ResourcePath.class);
    }

    public boolean isEntityID() {
        return field.isAnnotationPresent(EntityID.class);
    }
    public boolean isEntityPK() {
        return field.isAnnotationPresent(EntityPK.class);
    }

    protected void fillDefaultValues(RepoEntity instance) {
        if(getValue(instance) == null) {
            if(isOfClass(fieldType, String.class)) {
                if (!isAllowNull()) {
                    setValue(instance, "");
                }
            } else if(isList()) {
                setValue(instance, new ArrayList<>());
            } else if(isSet()){
                Object o = isComparableFlat() ? new TreeSet<>() : new LinkedHashSet<>();
                setValue(instance, o);
            }
        }
    }

    public String formatValue(RepoEntity instance) {
        String strValue;

        if (isEntityColl()) {
            strValue = "";
        } else if (isCollection()) {
            Collection<?> coll = (Collection<?>) getValue(instance);
            strValue = JkStreams.join(coll, SEP_LIST, e -> formatSingleValue(e, getCollType()));
        } else {
            strValue = formatSingleValue(getValue(instance), getFieldType());
        }

        return strValue;
    }
    private String formatSingleValue(Object value, Class<?> fclazz) {
        String toRet;
        boolean isString = isOfClass(fclazz, String.class);

        if (value == null) {
            toRet = isString && !isAllowNull() ? "" : PH_NULL;
        } else if (isInstanceOf(fclazz, RepoEntity.class)) {
            toRet = ((RepoEntity) value).strMini();
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
        } else if (isInstanceOf(fclazz, JkFormattable.class)) {
            toRet = ((JkFormattable)value).format();
        } else if (isInstanceOf(fclazz, Enum.class)) {
            toRet = ((Enum)value).name();
        } else if (isString) {
            toRet = (String) value;
        } else {
            throw new RepoError("Object formatting not implemented for: class = {}, value = {}", fclazz, value);
        }

        return escapeString(toRet, isString);
    }

    public static String escapeString(String value, boolean fullEscape) {
        if(value == null) {
            return PH_NULL;
        }
        String res = value.replace(SEP_LIST, PH_SEP_LIST);
        res = res.replace(SEP_FIELD, PH_SEP_FIELD);
        if(fullEscape) {
            res = res.replaceAll("\t", PH_TAB);
            res = res.replaceAll("\n", PH_NEWLINE);
        }
        return res;
    }

    public static String unescapeString(String value, boolean fullEscape) {
        if(PH_NULL.equals(value)) {
            return null;
        }
        String res = value.replace(PH_SEP_LIST, SEP_LIST);
        res = res.replace(PH_SEP_FIELD, SEP_FIELD);
        if(fullEscape) {
            res = res.replace(PH_TAB, "\t");
            res = res.replace(PH_NEWLINE, "\n");
        }
        return res;
    }

    public void parseAndSetValue(RepoEntity instance, String str) {
        if(!isEntityFlat()) {
            Object pval = parseValue(str);
            JkReflection.setFieldValue(instance, field, pval);
        }
    }

    private Object parseValue(String value) {
        Object retVal;

        if (isCollection()) {
            List<Object> values = new ArrayList<>();
            List<String> strElems = JkStrings.splitList(value, SEP_LIST);
            values.addAll(JkStreams.map(strElems, elem -> parseSingleValue(elem, getCollType())));
            if (isSet()) {
                retVal = isInstanceOf(getCollType(), Comparable.class) ? JkConvert.toTreeSet(values) : JkConvert.toHashSet(values);
            } else {
                retVal = values;
            }

        } else {
            retVal = parseSingleValue(value, getFieldType());
        }

        return retVal;
    }

    private Object parseSingleValue(String value, Class<?> fclazz) {
        Object o;

        boolean isString = isOfClass(fclazz, String.class);
        String unesc = unescapeString(value, isString);

        if (unesc == null) {
            o = isString && !isAllowNull() ? "" : null;
        } else if (isOfClass(fclazz, boolean.class, Boolean.class)) {
            o = Boolean.valueOf(unesc);
        } else if (isOfClass(fclazz, int.class, Integer.class)) {
            o = JkConvert.toInt(unesc, fclazz.isPrimitive() ? 0 : null);
        } else if (isOfClass(fclazz, long.class, Long.class)) {
            o = JkConvert.toLong(unesc, fclazz.isPrimitive() ? 0L : null);
        } else if (isOfClass(fclazz, float.class, Float.class)) {
            o = JkConvert.toFloat(unesc, fclazz.isPrimitive() ? 0f : null);
        } else if (isOfClass(fclazz, double.class, Double.class)) {
            o = JkConvert.toDouble(unesc, fclazz.isPrimitive() ? 0d : null);
        } else if (isOfClass(fclazz, Path.class)) {
            o = Paths.get(unesc);
        } else if (isOfClass(fclazz, File.class)) {
            o = Paths.get(unesc).toFile();
        } else if (isOfClass(fclazz, LocalTime.class)) {
            o = LocalTime.parse(unesc, DTF_TIME);
        } else if (isOfClass(fclazz, LocalDate.class)) {
            o = LocalDate.parse(unesc, DTF_DATE);
        } else if (isOfClass(fclazz, LocalDateTime.class)) {
            o = LocalDateTime.parse(unesc, DTF_DATETIME);
        } else if (isInstanceOf(fclazz, JkFormattable.class)) {
            o = JkReflection.createInstance(fclazz);
            ((JkFormattable) o).parse(unesc);
        } else if (isInstanceOf(fclazz, Enum.class)) {
            o = Enum.valueOf((Class)fclazz, unesc);
        } else if (isString) {
            o = unesc;
        } else {
            throw new RepoError("String parsing not implemented for: class = {}, value = {}", fclazz, value);
        }

        return o;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldWrap that = (FieldWrap) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }


    @Override
    public String toString() {
        return field.getName();
    }

}
