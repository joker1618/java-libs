package stuff;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class Writer {

	@Test
	public void trytest() {
	    dostreamtest(0);
	    dostreamtest(2);
	    dostreamtest(10);
    }
	public void dostreamtest(int magicnum) {
        List<Integer> list = Arrays.asList(1, 23, 42, 135, 21, 723, 22, 6);

        Stream<Integer> stream = list.stream();
        if(magicnum > 0) {
            stream = stream.filter(n -> n > 10);
            if(magicnum > 5) {
                stream = stream.filter(n -> n < 100);
            }
        }

        list = stream.collect(Collectors.toList());

        display("\nmagic num = %d", magicnum);
        list.forEach(num -> display(""+num));
        display("List size = %d", list.size());
    }
}
