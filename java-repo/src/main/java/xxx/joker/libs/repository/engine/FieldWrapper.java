package xxx.joker.libs.repository.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.AllowNullString;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoFieldCustom;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static xxx.joker.libs.repository.common.RepoCommon.Separator.*;


public class FieldWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FieldWrapper.class);

    private static final DateTimeFormatter DTF_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    private final Field field;
    // The following fields are used to avoid reflection every time
    private Class<?> fieldType;
    private Class<?> elemType;
    private Set<Class<?>> directives;

    FieldWrapper(Field field) {
        this.field = field;
        this.directives = new HashSet<>();
        init();
    }

    private void init() {
        fieldType = field.getType();

        Class<?>[] types = JkReflection.getParametrizedTypes(field);
        if(types.length > 0) {
            elemType = types[0];
        }

        boolean allowNullStr = field.getAnnotation(AllowNullString.class) != null ||
                field.getDeclaringClass().getAnnotation(AllowNullString.class) != null;
        if(allowNullStr) {
            directives.add(AllowNullString.class);
        }
    }

    public Field getField() {
        return field;
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getFieldType() {
        return fieldType;
    }

    public boolean isRepoEntity() {
        return getFieldType().getSuperclass() == RepoEntity.class;
    }
    public boolean isRepoEntityFlatField() {
        return isRepoEntity() || isRepoEntityCollection();
    }
    public boolean isRepoEntityCollection() {
        return elemType != null && elemType.getSuperclass() == RepoEntity.class;
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

    public boolean isComparableFlatField() {
        Class<?> toTest = isCollection() ? elemType : fieldType;
        return JkReflection.isInstanceOf(toTest, Comparable.class);
    }

    public Class<?> getElemType() {
        return elemType;
    }

    public Object getValue(RepoEntity instance) {
        return JkReflection.getFieldValue(instance, field);
    }

    public void setValue(RepoEntity instance, Object value) {
        JkReflection.setFieldValue(instance, field, value);
    }

    private boolean isAllowNullString() {
        return directives.contains(AllowNullString.class);
    }

    public void applyDirectives(RepoEntity instance) {
        if(typeOf(String.class)) {
            if(!isAllowNullString()) {
                if(getValue(instance) == null) {
                    setValue(instance, "");
                }
            }
        }
    }

    public String formatValue(RepoEntity instance) {
        String strValue;

        if(isRepoEntityFlatField()) {
            strValue = "";
        } else if (isCollection()) {
            Collection<?> coll = (Collection<?>) getValue(instance);
            strValue = JkStreams.join(coll, SEP_LIST, e -> formatSingleValue(e, getElemType()));
        } else {
            strValue = formatSingleValue(getValue(instance), getFieldType());
        }

        return strValue;
    }
    private String formatSingleValue(Object value, Class<?> fclazz) {
        String toRet;

        if (value == null) {
            toRet = isOfType(fclazz, String.class) && !isAllowNullString() ? "" : PH_NULL;
        } else if (isOfType(fclazz, boolean.class, Boolean.class)) {
            toRet = ((Boolean) value) ? "true" : "false";
        } else if (isOfType(fclazz, File.class, Path.class)) {
            toRet = value.toString();
        } else if (isOfType(fclazz, JkDuration.class)) {
            toRet = String.valueOf(((JkDuration) value).toMillis());
        } else if (isOfType(fclazz, LocalTime.class)) {
            toRet = DateTimeFormatter.ISO_TIME.format((LocalTime) value);
        } else if (isOfType(fclazz, LocalDate.class)) {
            toRet = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
        } else if (isOfType(fclazz, LocalDateTime.class)) {
            toRet = DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) value);
        } else if (isOfType(fclazz, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class)) {
            toRet = String.valueOf(value);
        } else if (isOfType(fclazz, String.class)) {
            toRet = (String) value;
        } else if (JkReflection.isInstanceOf(fclazz, RepoFieldCustom.class)) {
            toRet = ((RepoFieldCustom)value).format();
        } else {
            throw new RepoError("Object formatting not implemented for: class = {}, value = {}", fclazz, value);
        }

        boolean isString = isOfType(fclazz, String.class);
        return escapeString(toRet, isString);
    }

    public String escapeString(String value, boolean fullEscape) {
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

    public String unescapeString(String value, boolean fullEscape) {
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

    public boolean typeOf(Class<?>... classes) {
        return isOfType(fieldType, classes);
    }
    public boolean typeOfFlat(Class<?>... classes) {
        return isOfType(isCollection() ? elemType : fieldType, classes);
    }

    public void parseAndSetValue(RepoEntity instance, String str) {
        if(!isRepoEntityFlatField()) {
            Object pval = parseValue(str);
            JkReflection.setFieldValue(instance, field, pval);
        }
    }

    private Object parseValue(String value) {
        Object retVal;

        if (isCollection()) {
            List<Object> values = new ArrayList<>();
            List<String> strElems = JkStrings.splitList(value, SEP_LIST);
            values.addAll(JkStreams.map(strElems, elem -> parseSingleValue(elem, getElemType())));
            if (isSet()) {
                retVal = JkReflection.isInstanceOf(getElemType(), Comparable.class) ? JkConvert.toTreeSet(values) : JkConvert.toHashSet(values);
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

        boolean isString = isOfType(fclazz, String.class);
        String unesc = unescapeString(value, isString);

        if (unesc == null) {
            o = isString && !isAllowNullString() ? "" : null;
        } else if (isOfType(fclazz, boolean.class, Boolean.class)) {
            o = Boolean.valueOf(unesc);
        } else if (isOfType(fclazz, int.class, Integer.class)) {
            o = JkConvert.toInt(unesc);
        } else if (isOfType(fclazz, long.class, Long.class)) {
            o = JkConvert.toLong(unesc);
        } else if (isOfType(fclazz, float.class, Float.class)) {
            o = JkConvert.toFloat(unesc);
        } else if (isOfType(fclazz, double.class, Double.class)) {
            o = JkConvert.toDouble(unesc);
        } else if (isOfType(fclazz, Path.class)) {
            o = Paths.get(unesc);
        } else if (isOfType(fclazz, File.class)) {
            o = new File(unesc);
        } else if (isOfType(fclazz, JkDuration.class)) {
            o = JkDuration.of(Long.valueOf(unesc));
        } else if (isOfType(fclazz, LocalTime.class)) {
            o = LocalTime.parse(unesc, DTF_TIME);
        } else if (isOfType(fclazz, LocalDate.class)) {
            o = LocalDate.parse(unesc, DTF_DATE);
        } else if (isOfType(fclazz, LocalDateTime.class)) {
            o = LocalDateTime.parse(unesc, DTF_DATETIME);
        } else if (JkReflection.isInstanceOf(fclazz, RepoFieldCustom.class)) {
            o = JkReflection.createInstanceSafe(fclazz);
            ((RepoFieldCustom) o).parse(unesc);
        } else if (isString) {
            o = unesc;
        } else {
            throw new RepoError("String parsing not implemented for: class = {}, value = {}", fclazz, value);
        }

        return o;
    }

    private boolean isOfType(Class<?> toFind, Class<?>... elems) {
        for(Class<?> c : elems) {
            if(c == toFind) return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldWrapper that = (FieldWrapper) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
