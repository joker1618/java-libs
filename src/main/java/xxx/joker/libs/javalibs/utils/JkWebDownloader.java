package xxx.joker.libs.javalibs.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 29/10/2017.
 */
public class JkWebDownloader {

	public static String downloadHtml(String webPageURL) throws IOException {
		List<String> lines = downloadHtmlLines(webPageURL);
		return JkStreams.join(lines, StringUtils.LF);
	}

	public static List<String> downloadHtmlLines(String webPageURL) throws IOException {
		URL webURL = new URL(webPageURL);
		List<String> lines = new ArrayList<>();

		try (InputStream is = webURL.openStream();
			 InputStreamReader isr = new InputStreamReader(is);
			 BufferedReader reader = new BufferedReader(isr)) {

			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				lines.add(inputLine);
			}
		}

		return lines;
	}

	public static void downloadResource(String resourceURL, Path outputPath) throws IOException {
		URL webURL = new URL(resourceURL);
		URLConnection conn = webURL.openConnection();

		byte[] arr = new byte[500*1024];
		try(InputStream is = new BufferedInputStream(conn.getInputStream());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {
			int nread;
			while((nread = is.read(arr)) != -1) {
				os.write(arr, 0, nread);
			}
		}
	}

	public static byte[] downloadResource(String resourceURL) throws IOException {
		URL webURL = new URL(resourceURL);
		URLConnection conn = webURL.openConnection();

		byte[] arr = new byte[500*1024];
		byte[] toRet = new byte[0];
		try(InputStream is = new BufferedInputStream(conn.getInputStream())) {
			int nread;
			while((nread = is.read(arr)) != -1) {
				byte[] readBytes = Arrays.copyOfRange(arr, 0, nread);
				toRet = JkBytes.mergeArrays(toRet, readBytes);
			}
		}

		return toRet;
	}

}
