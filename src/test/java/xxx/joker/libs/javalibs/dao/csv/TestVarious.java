package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class TestVarious {

	@Test
	public void testvar() {

		Class<?> fclazz = Boolean[].class;
		Boolean[] barr = new Boolean[] {true, false};
		Object obj = barr;
		Object[] objArr = (Object[]) obj;
		Boolean[] finalArr = (Boolean[]) obj;
		display(Arrays.toString(finalArr));
		Boolean[] finalArr2 = (Boolean[]) objArr;
		display(Arrays.toString(finalArr2));

		Object cast = fclazz.cast(obj);
//		display(Arrays.toString(cast));
		display(Arrays.toString(finalArr2));
		display(Arrays.toString(objArr));



		display("END");
	}

	@Test
	public void test() {
		Class<?> clazz = ClazzA.class;


		display("%s", Arrays.toString(clazz.getInterfaces()));
		display("%s", Arrays.asList(clazz.getInterfaces()).contains(Interface.class));
		display("END");
	}

	interface Interface {
		void doAction();
	}

	class ClazzA implements Interface {

		@Override
		public void doAction() {

		}
	}
}
