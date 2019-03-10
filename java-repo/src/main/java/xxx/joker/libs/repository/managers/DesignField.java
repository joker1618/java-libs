package xxx.joker.libs.repository.managers;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityField;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

class DesignField implements Comparable<DesignField> {

    private int annotIdx;
    private Field field;
    private Class<?> collType;

    public DesignField(Field field) {
        JkEntityField annot = field.getAnnotation(JkEntityField.class);
        this.annotIdx = annot.idx();
        this.field = field;
        if(isCollection()) {
            ParameterizedType ct = (ParameterizedType) field.getGenericType();
            collType = (Class<?>) ct.getActualTypeArguments()[0];
        }
    }

    public int getAnnotIdx() {
        return annotIdx;
    }

    Class<?> getFieldType() {
        return field.getType();
    }

    Class<?> getFlatFieldType() {
        return isCollection() ? getCollectionType() : getFieldType();
    }

    boolean isFlatJkEntity() {
        return JkReflection.isInstanceOf(getFlatFieldType(), JkEntity.class);
    }

    Class<?> getCollectionType() {
        return collType;
    }

    boolean isList() {
        return getFieldType() == List.class;
    }
    boolean isSet() {
        return getFieldType() == Set.class;
    }

    boolean isCollection() {
        return isList() || isSet();
    }

    boolean isFlatFieldComparable() {
        Class<?> c;
        if(isCollection()) {
            c = getCollectionType();
        } else {
            c = getFieldType();
        }
        return JkReflection.isInstanceOf(c, Comparable.class);
    }

    public Object getValue(Object elem) {
        try {
            Object obj;
            if (field.isAccessible()) {
                obj = field.get(elem);
            } else {
                field.setAccessible(true);
                obj = field.get(elem);
                field.setAccessible(false);
            }
            return obj;

        } catch (IllegalAccessException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public void setValue(Object elem, Object value) {
        try {
            if (field.isAccessible()) {
                field.set(elem, value);
            } else {
                field.setAccessible(true);
                field.set(elem, value);
                field.setAccessible(false);
            }

        } catch (IllegalAccessException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public String getFieldName() {
        return field.getName();
    }

    @Override
    public int compareTo(DesignField o) {
        return getAnnotIdx() - o.getAnnotIdx();
    }
}
