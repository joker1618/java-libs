package xxx.joker.libs.repository.design2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * 	LocalDate.class
 * 	LocalTime.class
 * 	LocalDateTime.class
 * 	String.class
 *
 * 	? extends RepoFieldCustom
 * 	? extends RepoEntity
 *
 * ALLOWED COLLECTION TYPES:
 * 	- List
 * 	- Set
 *
 * DETAILS:
 * - nested collections not allowed
 * - null collections not allowed: will be changed using empty collection
 * - String fields by default don't allow null values: is used "". To allow null values, use @{@link AllowNullString} on field or class
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RepoField {

}
