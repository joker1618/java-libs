package stuff;

import org.junit.Test;
import xxx.joker.libs.javalibs.dao.csv.DaoUtil;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class Writer {

	@Test
	public void our() {

		for (Class<?> clazz : DaoUtil.allowedClasses) {
			if(!clazz.isArray() && !clazz.isPrimitive()) {
				String a = clazz.getSimpleName();
				display("if(fclazz == %s[].class)\ttypeArray = new %s[0];", a, a);
			}
		}
	}
}
