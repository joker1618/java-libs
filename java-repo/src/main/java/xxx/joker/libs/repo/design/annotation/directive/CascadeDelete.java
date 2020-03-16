package xxx.joker.libs.repo.design.annotation.directive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The field must have one of the following types:
 * - RepoEntity
 * - Collection&lt;RepoEntity&gt;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CascadeDelete {

}
