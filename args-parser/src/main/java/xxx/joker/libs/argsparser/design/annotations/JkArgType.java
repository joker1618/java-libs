package xxx.joker.libs.argsparser.design.annotations;

import xxx.joker.libs.core.ToAnalyze;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})

public @interface JkArgType {
	
}
