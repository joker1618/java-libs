package xxx.joker.libs.core.runtimes;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class JkReflection {

	public static <T> T createInstance(String clazzName) {
		try {
			return (T) createInstance(Class.forName(clazzName));
		} catch (Exception e) {
			throw new JkRuntimeException(e);
		}
	}
	public static <T> T createInstance(Class<T> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new JkRuntimeException(e);
		}
	}

	public static void setFieldValue(Object instance, String fieldName, Object value) {
		try {
			setFieldValue(instance, getFieldByName(instance.getClass(), fieldName), value);
		} catch (Exception e) {
			throw new JkRuntimeException("Class {}: error setting field ({}) value ({})", instance.getClass(), fieldName, value);
		}
	}
	public static void setFieldValue(Object instance, Field field, Object value) {
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

	public static Object getFieldValue(Object instance, String fieldName) {
		try {
			return getFieldValue(instance, getFieldByName(instance.getClass(), fieldName));
		} catch (Exception ex) {
			throw new JkRuntimeException("Class {}: error getting field value from ({})", instance.getClass().getName(), fieldName);
		}
	}
	public static Object getFieldValue(Object instance, Field field) {
		try {
			Object obj;
			if (field.isAccessible()) {
				obj = field.get(instance);
			} else {
				field.setAccessible(true);
				obj = field.get(instance);
				field.setAccessible(false);
			}
			return obj;

		} catch (Exception ex) {
			throw new JkRuntimeException("Class {}: error getting field value from ({})", instance.getClass().getName(), field.getName());
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
		while(sourceClass != null) {
			List<Field> declaredFields = Arrays.asList(sourceClass.getDeclaredFields());
			List<Field> fields = JkStreams.filter(declaredFields, f -> f.getName().equals(fieldName));
			if(!fields.isEmpty()) {
				return fields.get(0);
			}
			sourceClass = sourceClass.getSuperclass();
		}
		return null;
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

	public static boolean isInstanceOf(Class<?> clazz, Collection<Class<?>> expected) {
		List<Class<?>> types = findAllClassTypes(clazz);
		for(Class<?> cexp : expected) {
			if(types.contains(cexp)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isInstanceOf(Class<?> clazz, Class<?>... expected) {
		return isInstanceOf(clazz, Arrays.asList(expected));
	}

	public static List<Class<?>> findAllClassTypes(Class<?> clazz) {
		Set<Class<?>> types = new HashSet<>();

		Class<?> tmp = clazz;
		while(tmp != null) {
			types.add(tmp);
			types.addAll(findAllInterfaces(tmp));
			tmp = tmp.getSuperclass();
		}

		return new ArrayList<>(types);
	}

	private static Set<Class<?>> findAllInterfaces(Class<?> clazz) {
		Set<Class<?>> interfaces = new HashSet<>();
		interfaces.add(clazz);
		for(Class<?> interf : clazz.getInterfaces()) {
			interfaces.addAll(findAllInterfaces(interf));
		}
		return interfaces;
	}

	private static List<Field> findAllFields(Class<?> clazz) {
		Class<?> sourceClazz = clazz;
		List<Field> fields = new ArrayList<>();
		while(sourceClazz != null) {
			fields.addAll(JkConvert.toList(sourceClazz.getDeclaredFields()));
			sourceClazz = sourceClazz.getSuperclass();
		}
		return fields;
	}

	public static Class<?>[] getParametrizedTypes(Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			return getParametrizedTypes(field);
		} catch (NoSuchFieldException e) {
			return new Class<?>[0];
		}
	}
	public static Class<?>[] getParametrizedTypes(Field field) {
		if(!isInstanceOf(field.getGenericType().getClass(), ParameterizedType.class)) {
			return new Class<?>[0];
		}

		Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
		return JkStreams.map(Arrays.asList(types), t -> (Class<?>) t).toArray(new Class<?>[0]);
	}

	public static Class<?> classForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new JkRuntimeException(e, "Class not found for name: {}", className);
		}
	}

	public static <T> T copyFields(Object source, Class<T> targetClass, String... fieldsToCopy) {
		Map<String, Field> sourceFieldMap = JkStreams.toMapSingle(findAllFields(source.getClass()), Field::getName);
		List<Field> targetFields = findAllFields(targetClass);

		if(fieldsToCopy.length > 0) {
			targetFields.removeIf(tf -> !JkTests.containsIgnoreCase(fieldsToCopy, tf.getName()));
		}

		T target = createInstance(targetClass);
		for (Field tf : targetFields) {
			Field f = sourceFieldMap.get(tf.getName());
			if(f != null && f.getType() == tf.getType()) {
				Object sval = getFieldValue(source, f);
				setFieldValue(target, tf, sval);
			}
		}

		return target;
	}
}
