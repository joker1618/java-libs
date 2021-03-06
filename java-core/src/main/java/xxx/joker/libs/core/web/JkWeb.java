package xxx.joker.libs.core.web;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.enumerative.JkSizeUnit;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.util.JkBytes;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JkWeb {
	
	private static final Logger LOG = LoggerFactory.getLogger(JkWeb.class);

	public static String downloadHtml(String webPageURL) {
		List<String> lines = downloadHtmlLines(webPageURL);
		return JkStreams.join(lines, StringUtils.LF);
	}
	public static List<String> downloadHtmlLines(String webPageURL) {
		try {
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

		} catch(IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}

	public static byte[] downloadResource(String resourceURL) {
		try {
			URL webURL = new URL(resourceURL);
			URLConnection conn = webURL.openConnection();

			byte[] arr = new byte[500 * 1024];
			byte[] toRet = new byte[0];
			try (InputStream is = new BufferedInputStream(conn.getInputStream())) {
				int nread;
				while ((nread = is.read(arr)) != -1) {
					byte[] readBytes = Arrays.copyOfRange(arr, 0, nread);
					toRet = JkBytes.mergeArrays(toRet, readBytes);
				}
			}

			return toRet;

		} catch(IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}
	public static void downloadResource(String resourceURL, Path outputPath) {
		downloadResource(resourceURL, outputPath, true);
	}
	public static void downloadResource(String resourceURL, Path outputPath, boolean printLog) {
		try {
			double logSize = JkSizeUnit.MB.size() * 10;
			JkTimer timer = JkTimer.start();

			URL webURL = new URL(resourceURL);
			URLConnection conn = webURL.openConnection();

			Files.deleteIfExists(outputPath);
			Files.createDirectories(JkFiles.getParent(outputPath));

			byte[] arr = new byte[500 * 1024];
			try (InputStream is = new BufferedInputStream(conn.getInputStream());
				 OutputStream os = new BufferedOutputStream(new FileOutputStream(outputPath.toFile()))) {
				int nread;
				double totread = 0d;
				double prev = 0d;
				while ((nread = is.read(arr)) != -1) {
					os.write(arr, 0, nread);
					if(printLog) {
						totread += nread;
						if((totread - prev) >= logSize) {
							prev = totread;
							long secElapsed = (long) timer.elapsed(ChronoUnit.SECONDS);
							LOG.info("Downloaded {}  ({}/s)", JkOutput.humanSize(totread), JkOutput.humanSize(totread / secElapsed));
						}
					}
				}
			}

		} catch(IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}

}
