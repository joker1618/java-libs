package xxx.joker.libs.argsparser.design.annotation;

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

@ToAnalyze
@Deprecated
public @interface Cmd {

}
