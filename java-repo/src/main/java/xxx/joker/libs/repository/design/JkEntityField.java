package xxx.joker.libs.repository.design;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})

/**
 * ALLOWED FIELD TYPES:
 *
 * 	boolean.class 	Boolean.class
 * 	int.class     	Integer.class
 * 	long.class    	Long.class
 * 	float.class   	Float.class
 * 	double.class  	Double.class
 *
 * 	File.class
 * 	Path.class
 * 	LocalDate.class
 * 	LocalTime.class
 * 	LocalDateTime.class
 * 	String.class
 *
 * 	Pair.class
 *
 * 	? extends JkEntityFieldCustom
 * 	? extends JkEntity
 *
 * ALLOWED COLLECTION TYPES:
 * 	- List
 * 	- Set
 * 	- Map
 *
 * PARAMETERS:
 * - collectionClass: must be specified for 'List' and 'Set' fields
 * - keyClass: must be specified for 'Map' fields only
 * - valueClass: must be specified for 'Map' fields only
 *
 *
 * DETAILS:
 * - String  -->  null not permitted: used ""
 * - List    -->  null not permitted: used 'emptyList'
 * - Set     -->  null not permitted: used 'emptySet'
 * - Map     -->  null not permitted: used 'emptyMap'
 *
 */

public @interface JkEntityField {

    // Must be specified only for List and Set
	Class<?> collectionClass() default Object.class;

    // Must be specified only for Map and Pair
	Class<?> keyClass() default Object.class;
	Class<?> valueClass() default Object.class;

}
