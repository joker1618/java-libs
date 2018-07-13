package xxx.joker.libs.javalibs.dao.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})

/**
 * ALLOWED SIMPLE FIELD TYPES:
 *
 * 	boolean.class 	Boolean.class
 * 	int.class     	Integer.class
 * 	long.class    	Long.class
 * 	float.class   	Float.class
 * 	double.class  	Double.class
 *
 * 	File.class
 * 	Path.class
 * 	LocalTime.class
 * 	LocalDate.class
 * 	LocalDateTime.class
 * 	String.class
 *
 * 	? extends CsvElement
 *
 * ALLOWED COLLECTION FIELD TYPES:   (elements class type must be one of simple allowed above)
 * 	- List<>
 *
 * PARAMETERS:
 * - subElemType: must be specified for 'List' and 'Arrays[]' fields. Must be one of the classes specified above.
 *
 * DETAILS:
 * - String  -->  null not permitted: used ""
 * - List    -->  null not permitted: used 'emptyList'
 * - Set     -->  null not permitted: used 'emptySet'
 * - T[]     -->  null not permitted: used 'emptyArray', no array of primitives allowed
 *
 */

public @interface CsvField {

	int index();

	String header() default "";

	// Must be specified for 'List' and 'Arrays[]' fields
	Class<?> subElemType() default String.class;

}
