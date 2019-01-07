package xxx.joker.libs.core.reflection;

import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.core.utils.JkTests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class JkReflection {

    public static List<Field> getFieldsByAnnotation(Class<?> sourceClass, Class<? extends Annotation> annotationClass) {
        List<Field> toRet = new ArrayList<>();
        Field[] declaredFields = sourceClass.getDeclaredFields();
        if(!JkTests.isEmpty(declaredFields)) {
            for (Field field : declaredFields) {
                if (field.getAnnotation(annotationClass) != null) {
                    toRet.add(field);
                }
            }
        }
        return toRet;
    }

    public static List<Field> getFieldsByType(Class<?> sourceClass, Class<?> fieldType) {
        List<Field> toRet = new ArrayList<>();
        Field[] declaredFields = sourceClass.getDeclaredFields();
        if(!JkTests.isEmpty(declaredFields)) {
            for (Field field : declaredFields) {
                if (field.getType() == fieldType) {
                    toRet.add(field);
                }
            }
        }
        return toRet;
    }

    public static Field getFieldByName(Class<?> sourceClass, String fieldName) {
        List<Field> declaredFields = Arrays.asList(sourceClass.getDeclaredFields());
        List<Field> fields = JkStreams.filter(declaredFields, f -> f.getName().equals(fieldName));
        return fields.isEmpty() ? null : fields.get(0);
    }

    public static Enum getEnumByName(Class<?> enumClass, String enumName) {
        Enum[] enumConstants = (Enum[]) enumClass.getEnumConstants();
        for(Enum elem : enumConstants) {
            if(elem.name().equals(enumName)) {
                return elem;
            }
        }
        return null;
    }

    public static boolean isOfType(Class<?> clazz, Class<?> expected) {
        Set<Class<?>> types = new HashSet<>();

        Class<?> tmp = clazz;
        while(tmp != null) {
            types.add(tmp);
            types.addAll(findAllInterfaces(tmp));
            tmp = tmp.getSuperclass();
        }

        return types.contains(expected);
    }

    private static Set<Class<?>> findAllInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new HashSet<>();
        interfaces.add(clazz);
        for(Class<?> interf : clazz.getInterfaces()) {
            interfaces.addAll(findAllInterfaces(interf));
        }
        return interfaces;
    }

}
