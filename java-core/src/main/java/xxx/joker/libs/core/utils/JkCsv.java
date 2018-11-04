package xxx.joker.libs.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by f.barbano on 07/04/2018.
 */
public class JkCsv {

	public static final String CSV_SEP = ";";

	public static List<String> readLines(Path path, String header) throws IOException {
		List<String> lines = Files.readAllLines(path);
		lines.removeIf(l -> StringUtils.isBlank(l) || l.equals(header));
		return lines;
	}

	public static List<String[]> readLineFields(Path path, String header) throws IOException {
		return readLineFields(path, header, CSV_SEP);
	}
	public static List<String[]> readLineFields(Path path, String header, String separator) throws IOException {
		List<String> lines = Files.readAllLines(path);
		lines.removeIf(l -> StringUtils.isBlank(l) || l.equals(header));
		return JkStreams.map(lines, l -> JkStrings.splitAllFields(l, separator));
	}

}
