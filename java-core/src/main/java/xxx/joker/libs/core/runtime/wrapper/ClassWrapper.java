package xxx.joker.libs.core.runtime.wrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.runtime.JkReflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import static xxx.joker.libs.core.lambda.JkStreams.map;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkStrings.strf;

public class ClassWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(ClassWrapper.class);

    protected final Class<?> clazz;
    protected final List<FieldWrapper> fwList;

    public ClassWrapper(Class<?> clazz) {
        this.clazz = clazz;
        this.fwList = map(JkReflection.findAllFields(clazz), FieldWrapper::new);
    }

    public String wrappedClassName() {
        return clazz.getSimpleName();
    }
    public Class<?> wrappedClass() {
        return clazz;
    }

    public List<FieldWrapper> getFields() {
        return fwList;
    }

    @SafeVarargs
    public final boolean containsAnnotation(Class<? extends Annotation>... annotations) {
        return containsAnnotation(toList(annotations));
    }
    public final boolean containsAnnotation(Collection<Class<? extends Annotation>> annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if(clazz.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFinal() {
        return Modifier.isFinal(clazz.getModifiers());
    }
    public boolean isPrivate() {
        return Modifier.isPrivate(clazz.getModifiers());
    }
    public boolean isProtected() {
        return Modifier.isProtected(clazz.getModifiers());
    }
    public boolean isPublic() {
        return Modifier.isPublic(clazz.getModifiers());
    }
    public boolean isStatic() {
        return Modifier.isStatic(clazz.getModifiers());
    }

    @Override
    public String toString() {
        return strf("{}", wrappedClass());
    }

}
