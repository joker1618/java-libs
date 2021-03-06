package xxx.joker.libs.core.runtime.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.runtime.JkReflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkStrings.strf;

public class FieldWrapper extends TypeWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(FieldWrapper.class);

    protected final Field field;

    public FieldWrapper(Field field) {
        super(field.getGenericType());
        this.field = field;
    }

    public Field getField() {
        return field;
    }
    public String getFieldName() {
        return field.getName();
    }
    public Class<?> getFieldType() {
        return getTypeClass();
    }

    public <T> T getValue(Object instance) {
        return JkReflection.getFieldValue(instance, field);
    }

    public void setValue(Object instance, Object value) {
        JkReflection.setFieldValue(instance, field, value);
    }

    @SafeVarargs
    public final boolean containsAnnotation(Class<? extends Annotation>... annotations) {
        return containsAnnotation(toList(annotations));
    }
    public final boolean containsAnnotation(Collection<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if(field.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }
    public boolean isPrivate() {
        return Modifier.isPrivate(field.getModifiers());
    }
    public boolean isProtected() {
        return Modifier.isProtected(field.getModifiers());
    }
    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }
    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public String toString() {
        return toString(true);
    }
    public String toString(boolean simpleClassName) {
        return strf("{} {}", super.toString(simpleClassName), field.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldWrapper that = (FieldWrapper) o;
        return Objects.equals(toString(false), that.toString(false));
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString(false));
    }
}
