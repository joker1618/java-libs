package xxx.joker.libs.repository.managers;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityField;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DesignField implements Comparable<DesignField> {

    private JkEntityField annot;
    private Field field;

    public DesignField(Field field) {
        this.annot = field.getAnnotation(JkEntityField.class);
        this.field = field;
    }

    public int getIdx() {
        return annot.idx();
    }

    public Field getField() {
        return field;
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

    // fixme
//    Class<?> getParentClass() {
//        return field.getDeclaringClass();
//    }

    Class<?> getCollectionType() {
        return annot.collectionType();
    }


    // fixme
//    boolean isEntityImpl() {
//        Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
//        return JkReflection.isInstanceOf(fclazz, JkEntity.class);
//    }
//
//    Class<?> getEntityClass() {
//        Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
//        return !isEntityImpl() ? null : fclazz;
//    }

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
        return getFieldName().compareTo(o.getFieldName());
    }
}
