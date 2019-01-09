package xxx.joker.libs.core.utils;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.exception.JkRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Predicate;

import static java.lang.System.out;
import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 26/05/2018.
 */

public class JkConsole {

	public static void display(String mexFormat, Object... params) {
		out.println(strf(mexFormat, params));
		out.flush();
	}
	public static void display(boolean newLine, String mexFormat, Object... params) {
		if(newLine) {
			out.println(strf(mexFormat, params));
		} else {
			out.print(strf(mexFormat, params));
		}
		out.flush();
	}

	public static String readUserInput(String label) {
		return readUserInput(label, s -> true);
	}
	public static String readUserInput(String label, Predicate<String> acceptCond) {
		try {
			String heading = StringUtils.isEmpty(label) ? "" : label;

			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

			out.print(heading);
			String userInput = console.readLine();
			while (!acceptCond.test(userInput)) {
				out.print(heading);
				userInput = console.readLine();
			}

			return userInput;

		} catch(IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}

	// todo remove
//	public static void main(String[] args) throws IOException {
//		AtomicInteger counter = new AtomicInteger(0);
//		readUserInput(">", s -> counter.getAndIncrement()==5);
//	}

}
