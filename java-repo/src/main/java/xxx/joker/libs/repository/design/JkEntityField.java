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
 * 	? extends JkEntityFieldCustom
 * 	? extends JkEntity
 *
 * ALLOWED COLLECTION TYPES:
 * 	- List
 * 	- Set
 *
 * PARAMETERS:
 * - collectionType: must be specified for 'List' and 'Set' fields only
 * 		a) List can be of any type allowed
 * 		b) Set can be of any Comparable type allowed
 *
 *
 * DETAILS:
 * - String  -->  null not permitted: used ""
 * - List    -->  null not permitted: used 'emptyList'
 * - Set     -->  null not permitted: used 'emptySet'
 *
 */

public @interface JkEntityField {

	int idx();

    // Must be specified  for List and Set only
	Class<?> collectionType() default Object.class;

}
