package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class JkReflection {

	public static <T> T createInstanceSafe(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new JkRuntimeException(e);
		}
	}

	public static <T> void setFieldValue(T instance, Field field, Object value) {
		try {
			if(field.isAccessible()) {
				field.set(instance, value);
			} else {
				field.setAccessible(true);
				field.set(instance, value);
				field.setAccessible(false);
			}
		} catch (Exception e) {
			throw new JkRuntimeException("Class {}: error setting field ({}) value ({})", instance.getClass(), field.getName(), value);
		}
	}

	public static List<Field> getFieldsByAnnotation(Class<?> sourceClass, Class<? extends Annotation> annotationClass) {
		List<Field> toRet = new ArrayList<>();
		Field[] declaredFields = sourceClass.getDeclaredFields();
		if(declaredFields != null) {
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
		if(declaredFields != null) {
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

	public static boolean isInstanceOf(Class<?> clazz, Class<?> expected) {
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
