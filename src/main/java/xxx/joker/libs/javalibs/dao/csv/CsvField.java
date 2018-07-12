package xxx.joker.libs.javalibs.dao.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})

/**
 * ALLOWED SIMPLE FIELD TYPES:
 * 	Boolean.class	boolean.class
 * 	Integer.class	int.class
 * 	Long.class		long.class
 * 	Float.class		float.class
 * 	Double.class	double.class
 * 	File.class
 * 	Path.class
 * 	LocalTime.class
 * 	LocalDate.class
 * 	LocalDateTime.class
 * 	String.class
 * 	CsvElement.class
 *
 * ALLOWED COLLECTION FIELD TYPES:   (elements class type must be one of simple allowed above)
 * 	- List<>
 *
 * PARAMETERS:
 * - listElemType: must be specified for List fields. Must be one of the classes specified above.
 *
 * DETAILS:
 * - String  -->  null not permitted: used ""
 * - List    -->  null not permitted: used 'emptyList'
 *
 */
public @interface CsvField {

	int index();

	String header() default "";

	// Must be specified for List fields
	Class<?> listElemType() default String.class;

}
