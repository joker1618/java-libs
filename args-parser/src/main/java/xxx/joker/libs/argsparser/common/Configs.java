package xxx.joker.libs.argsparser.common;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.format.JkViewBuilder;
import xxx.joker.libs.core.utils.JkRuntime;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 27/08/2017.
 */

public class Configs {

	public static final Path TEMP_FOLDER = JkRuntime.getTempFolder().resolve("args_parser");

	public static final List<Class<?>> SUPPORTED_CLASSES = Arrays.asList(
		boolean.class,			boolean[].class,
		Integer.class,			int[].class,
		Long.class,				long[].class,
		Double.class,        	double[].class,
		Path.class,				Path[].class,
		LocalDate.class,   		LocalDate[].class,
		LocalTime.class,   		LocalTime[].class,
		LocalDateTime.class,	LocalDateTime[].class,
		String.class,			String[].class
	);

	public static String toStringSupportedClasses() {
		List<String> lines = new ArrayList<>();

		for(int i = 0; i < SUPPORTED_CLASSES.size(); i+=2) {
			String line = SUPPORTED_CLASSES.get(i).getSimpleName();
			if(i+1 < SUPPORTED_CLASSES.size()) {
				line += "|" + SUPPORTED_CLASSES.get(i+1).getSimpleName();
			}
			lines.add(line);
		}

		JkViewBuilder vb = new JkViewBuilder(lines);
		vb.insertPrefix(StringUtils.repeat(' ', 4));

		return vb.toString("|", 3);
	}
}
