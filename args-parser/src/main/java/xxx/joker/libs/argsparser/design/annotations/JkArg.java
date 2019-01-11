package xxx.joker.libs.argsparser.design.annotations;

import xxx.joker.libs.core.ToAnalyze;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by f.barbano on 26/08/2017.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface JkArg {

	String argName();
	String[] aliases() default {};

	/**
	 * All classes that the variable can assume in different commands
	 * If not specified, the field must belong to a type contained in Configs.SUPPORTED_CLASSES
	 * If specified, the field type must be Object
	 */
	Class<?>[] classes() default {};

}
