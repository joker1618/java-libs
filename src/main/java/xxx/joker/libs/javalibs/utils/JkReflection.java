package xxx.joker.libs.javalibs.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.barbano on 26/05/2018.
 */
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

	public static Enum getEnumByName(Class<?> enumClass, String enumName) {
		Enum[] enumConstants = (Enum[]) enumClass.getEnumConstants();
		for(Enum elem : enumConstants) {
			if(elem.name().equals(enumName)) {
				return elem;
			}
		}
		return null;
	}

}
