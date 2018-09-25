package stuff;

import org.junit.Test;
import xxx.joker.libs.javalibs.utils.JkStrings;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class Writer {

	@Test
	public void our() {

	    String s = "as/.m2/repository/fede";
        display("%s", JkStrings.matchRegExp(".*[/\\\\]{1}.m2[/\\\\]{1}repository[/\\\\]{1}.*", s));

//        for (Class<?> clazz : DaoUtil.allowedClasses) {
//			if(!clazz.isArray() && !clazz.isPrimitive()) {
//				String a = clazz.getSimpleName();
//				display("if(fclazz == %s[].class)\ttypeArray = new %s[0];", a, a);
//			}
//		}
	}
}
