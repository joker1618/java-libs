package xxx.joker.libs.repository.design;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation if you want to allow NULL strings.
 * By default, NULL strings are replaced by ""
 *
 * Usage:
 * - on Field annotated with @RepoField
 * - on Class implementing RepoEntity
 * NB: Class directive has priority with respect to Field
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface AllowNullString {

}
