package xxx.joker.libs.javalibs.dao.csv;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class DaoUtil {

	public static final List<Class<?>> allowedClasses = Arrays.asList(
		boolean.class,
		int.class,
		long.class,
		float.class,
		double.class,

		Boolean.class,        Boolean[].class,
		Integer.class,        Integer[].class,
		Long.class,           Long[].class,
		Float.class,          Float[].class,
		Double.class,         Double[].class,
		File.class,           File[].class,
		Path.class,           Path[].class,
		LocalTime.class,      LocalTime[].class,
		LocalDate.class,      LocalDate[].class,
		LocalDateTime.class,  LocalDateTime[].class,
		String.class,         String[].class,

		List.class,
		Set.class
	);
}
