package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	@Test
	public void testA() {
//        URL resource = Thread.currentThread().getContextClassLoader().getResource("");
//        display("%s", resource);

        String[] strarr = new String[0];
        Class<?> aClass = strarr.getClass();
        display("clazz  %s", aClass);
        display("array?  %s", aClass.isArray());
        display("clazz type  %s", aClass.getTypeName());
        display("comp type  %s", aClass.getComponentType());

        List<Integer> list = new ArrayList<>();
        aClass = list.getClass();
        display("clazz  %s", aClass);
        display("array?  %s", aClass.isArray());
        display("clazz type  %s", aClass.getTypeName());
        display("comp type  %s", aClass.getComponentType());

        display("\n\n");

        Object ca = new ClazzA();
        display("isCA  %s", ca instanceof ClazzA);
        display("isInterface  %s", ca instanceof Interface);
        display("isString  %s", ca instanceof String);
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
