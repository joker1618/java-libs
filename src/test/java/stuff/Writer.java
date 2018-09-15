package stuff;

import org.junit.Test;
import xxx.joker.libs.javalibs.dao.csv.DaoUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class Writer {

	@Test
	public void our() {

	    String s = "26 March";
        LocalDate d_l = LocalDate.parse(s+" 2017", DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        display("%s", d_l);

//        for (Class<?> clazz : DaoUtil.allowedClasses) {
//			if(!clazz.isArray() && !clazz.isPrimitive()) {
//				String a = clazz.getSimpleName();
//				display("if(fclazz == %s[].class)\ttypeArray = new %s[0];", a, a);
//			}
//		}
	}
}
