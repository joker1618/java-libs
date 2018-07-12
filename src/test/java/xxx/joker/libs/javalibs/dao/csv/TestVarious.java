package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Test;

import java.util.Arrays;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class TestVarious {

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
