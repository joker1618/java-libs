package xxx.joker.libs.argsparser.common;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.format.JkColumnFmtBuilder;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 27/08/2017.
 */
public class Const {

	// If an InputCommand has more independent evolutions than MAX_EVOLUTIONS, then the evolutions will be computed
	// considering only required independent parameters
	public static final int MAX_INDEPENDENT_EVOLUTIONS = 1_000_000;

	public static final String NEWLINE = StringUtils.LF;

	public static final List<Class<?>> SUPPORTED_CLASSES = Arrays.asList(
		Boolean.class,			Boolean[].class,
		Integer.class,			Integer[].class,
		Double.class,        	Double[].class,
		Long.class,				Long[].class,
		Path.class,				Path[].class,
//		LocalDate.class,   		LocalDate[].class,
//		LocalTime.class,   		LocalTime[].class,
//		LocalDateTime.class,	LocalDateTime[].class,
		String.class,			String[].class
	);

	public static String toStringSupportedClasses() {
		List<String> lines = new ArrayList<>();

		for(int i = 0; i < Const.SUPPORTED_CLASSES.size(); i+=2) {
			String line = Const.SUPPORTED_CLASSES.get(i).getSimpleName();
			if(i+1 < Const.SUPPORTED_CLASSES.size()) {
				line += "|" + Const.SUPPORTED_CLASSES.get(i+1).getSimpleName();
			}
			lines.add(line);
		}

		String str = new JkColumnFmtBuilder(lines).toString("|", 3);
		str = JkStrings.leftPadLines(str, " ", 4);

		return str;
	}
}
