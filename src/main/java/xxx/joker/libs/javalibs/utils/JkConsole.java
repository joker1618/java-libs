package xxx.joker.libs.javalibs.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.function.Predicate;

import static java.lang.System.out;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

/**
 * Created by f.barbano on 26/05/2018.
 */
public class JkConsole {

	// Out display
	public static void display(String mexFormat, Object... params) {
		out.print(strf(mexFormat, params));
	}
	public static void displayln(String mexFormat, Object... params) {
		out.println(strf(mexFormat, params));
	}

	public static String readUserInput(String label) throws IOException {
		return readUserInput(label, s -> true);
	}
	public static String readUserInput(String label, Predicate<String> acceptCond) throws IOException {
		String heading = "";
		if(!StringUtils.isEmpty(label)) {
			heading = strf("%s ", JkStrings.safeTrim(label));
		}

		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

		out.print(heading);
		String userInput = console.readLine();
		while(!acceptCond.test(userInput)) {
			out.print(heading);
			userInput = console.readLine();
		}

		return userInput;
	}

}
