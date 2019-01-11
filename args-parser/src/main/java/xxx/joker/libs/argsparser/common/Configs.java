package xxx.joker.libs.argsparser.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.format.JkViewBuilder;
import xxx.joker.libs.core.utils.JkEnvProps;
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

	public static final Logger logger = LoggerFactory.getLogger(Configs.class);

	public static final Path TEMP_FOLDER = JkEnvProps.getTempFolder().resolve("args_parser");

	// If an InputCommand has more independent evolutions than MAX_EVOLUTIONS, then the evolutions will be computed
	// considering only required independent parameters
	public static final int MAX_EVOLUTIONS = 1_000_000;

	public static final List<Class<?>> SUPPORTED_CLASSES = Arrays.asList(
		boolean.class,
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

	static {
		logger.info("ArgsParser config: temp folder is {}", TEMP_FOLDER.toAbsolutePath().normalize());
	}
}
