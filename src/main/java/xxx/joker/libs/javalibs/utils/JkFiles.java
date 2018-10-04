package xxx.joker.libs.javalibs.utils;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

/**
 * Created by f.barbano on 26/05/2018.
 */
public class JkFiles {

	private static final String NEWLINE = StringUtils.LF;
	private static final String TEMP_FILE = "generic.file.util.temp";

	public static final Charset CHARSET_ISO = Charset.forName("ISO-8859-1");
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	public static final Charset CHARSET_UTF16 = Charset.forName("UTF-16BE");


	/* WRITE methods */

	// Append
	public static void appendToFile(Path outputPath, String data) throws JkRuntimeException {
		appendToFile(outputPath, data, null);
	}
	public static void appendToFile(Path outputPath, String data, Charset encoding) throws JkRuntimeException {
		appendToFile(outputPath, data, encoding, false);
	}
	public static void appendToFile(Path outputPath, String data, Charset encoding, boolean finalNewline) throws JkRuntimeException {
		appendToFile(outputPath, Collections.singletonList(data), encoding, finalNewline);
	}

	public static void appendToFile(Path outputPath, List<String> lines) throws JkRuntimeException {
		appendToFile(outputPath, lines, null);
	}
	public static void appendToFile(Path outputPath, List<String> lines, Charset encoding) throws JkRuntimeException {
		appendToFile(outputPath, lines, encoding, !lines.isEmpty());
	}
	public static void appendToFile(Path outputPath, List<String> lines, Charset encoding, boolean finalNewline) throws JkRuntimeException {
	    try {
            Files.createDirectories(outputPath.toAbsolutePath().getParent());
            BufferedWriter writer = null;

            try {
                if (encoding == null) {
                    writer = Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } else {
                    writer = Files.newBufferedWriter(outputPath, encoding, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }

                for (int i = 0; i < lines.size(); i++) {
                    if (i > 0) writer.write(NEWLINE);
                    writer.write(lines.get(i));
                }

                if (finalNewline) {
                    writer.write(NEWLINE);
                }

            } finally {
                if (writer != null) {
                    writer.close();
                }
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	// Write
	public static void writeFile(Path outputPath, String content, boolean overwrite) throws JkRuntimeException {
		writeFile(outputPath, content, overwrite, null);
	}
	public static void writeFile(Path outputPath, String content, boolean overwrite, Charset encoding) throws JkRuntimeException {
		writeFile(outputPath, content, overwrite, encoding, false);
	}
	public static void writeFile(Path outputPath, String content, boolean overwrite, Charset encoding, boolean finalNewLine) throws JkRuntimeException {
		writeFile(outputPath, Arrays.asList(content), overwrite, encoding, finalNewLine);
	}

	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite) throws JkRuntimeException {
		writeFile(outputPath, lines, overwrite, null);
	}
	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite, Charset encoding) throws JkRuntimeException {
		writeFile(outputPath, lines, overwrite, encoding, !lines.isEmpty());
	}
	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite, Charset encoding, boolean finalNewline) throws JkRuntimeException {
	    try {
            if (Files.exists(outputPath) && !overwrite) {
                throw new JkRuntimeException("File [" + outputPath.normalize().toString() + "] already exists");
            }
            Files.deleteIfExists(outputPath);
            appendToFile(outputPath, lines, encoding, finalNewline);
        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	public static void writeFile(Path outputPath, byte[] bytes, boolean overwrite) throws JkRuntimeException {
	    try {
            if (Files.exists(outputPath) && !overwrite) {
                throw new JkRuntimeException("File [" + outputPath.normalize().toString() + "] already exists");
            }

            Files.createDirectories(outputPath.toAbsolutePath().getParent());
            Files.deleteIfExists(outputPath);
            Files.createFile(outputPath);

            try (OutputStream writer = new FileOutputStream(outputPath.toFile())) {
                writer.write(bytes);
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	// Insert header
	public static void insertFirstToFile(Path outputPath, String content) throws JkRuntimeException {
		insertFirstToFile(outputPath, content, null);
	}
	public static void insertFirstToFile(Path outputPath, String content, Charset encoding) throws JkRuntimeException {
		insertFirstToFile(outputPath, Collections.singletonList(content), encoding);
	}

	public static void insertFirstToFile(Path outputPath, List<String> lines) throws JkRuntimeException {
		insertFirstToFile(outputPath, lines, null);
	}
	public static void insertFirstToFile(Path outputPath, List<String> lines, Charset encoding) throws JkRuntimeException {
		try {
            if (!Files.exists(outputPath)) {
                writeFile(outputPath, lines, false, encoding);
            } else {
                Path tempFile = Paths.get(TEMP_FILE);
                writeFile(tempFile, lines, true, encoding);
                appendToFile(tempFile, Files.readAllLines(outputPath, encoding));
                copyAttributes(outputPath, tempFile);
                Files.delete(outputPath);
                Files.move(tempFile, outputPath);
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	/* READ methods */
	public static List<String> readLines(InputStream is) throws JkRuntimeException {
	    try {
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {

                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                return lines;
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	/* COPY-MOVE methods */
	public static void copyFile(Path sourcePath, Path targetPath, boolean overwrite) throws JkRuntimeException {
		copyFile(sourcePath, targetPath, overwrite, false);
	}
	public static void copyFile(Path sourcePath, Path targetPath, boolean overwrite, boolean holdAttributes) throws JkRuntimeException {
		copyFile1(sourcePath, targetPath, overwrite, holdAttributes, false);
	}
	public static Path copyFileSafely(Path sourcePath, Path targetPath) throws JkRuntimeException {
		return copyFileSafely(sourcePath, targetPath, false);
	}
	public static Path copyFileSafely(Path sourcePath, Path targetPath, boolean holdAttributes) throws JkRuntimeException {
		return copyFile1(sourcePath, targetPath, false, holdAttributes, true);
	}
	private static Path copyFile1(Path sourcePath, Path targetPath, boolean overwrite, boolean holdAttributes, boolean safePath) throws JkRuntimeException {
		try {
            if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
                throw new FileNotFoundException(strf("Source file [%s] not exists or not a regular file!", sourcePath));
            }

            Path outPath = Files.isDirectory(targetPath) ? targetPath.resolve(sourcePath.getFileName()) : targetPath;
            if (safePath) outPath = computeSafelyPath(outPath);

            if (!overwrite && Files.exists(outPath)) {
                throw new FileAlreadyExistsException(String.format("Unable to copy [%s] to [%s]: target path already exists", sourcePath.toAbsolutePath(), outPath.toAbsolutePath()));
            }

            Files.deleteIfExists(outPath);
            Files.createDirectories(getParent(outPath));
            Files.copy(sourcePath, outPath);
            if (holdAttributes) {
                copyAttributes(sourcePath, outPath);
            }

            return outPath;

        } catch (IOException ex) {
		    throw new JkRuntimeException(ex);
        }
	}

	public static void copyAttributes(Path sourcePath, Path targetPath) throws JkRuntimeException {
	    try {
            FileTime lastMod = Files.getLastModifiedTime(sourcePath);
            UserPrincipal owner = Files.getOwner(sourcePath);
            Files.setLastModifiedTime(targetPath, lastMod);
            Files.setOwner(targetPath, owner);

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	public static void moveFile(Path sourcePath, Path targetPath, boolean overwrite) throws JkRuntimeException {
		moveFile1(sourcePath, targetPath, overwrite, false);
	}
	public static Path moveFileSafely(Path sourcePath, Path targetPath) throws JkRuntimeException {
		return moveFile1(sourcePath, targetPath, false, true);
	}
	private static Path moveFile1(Path sourcePath, Path targetPath, boolean overwrite, boolean safePath) throws JkRuntimeException {
	    try {
            if (!Files.exists(sourcePath) || !Files.isRegularFile(sourcePath)) {
                throw new FileNotFoundException(strf("Source file [%s] not exists or not a regular file!", sourcePath));
            }

            Path outPath = Files.isDirectory(targetPath) ? targetPath.resolve(sourcePath.getFileName()) : targetPath;
            if (safePath) outPath = computeSafelyPath(outPath);

            if (!overwrite && Files.exists(outPath)) {
                throw new FileAlreadyExistsException(String.format("Unable to move [%s] to [%s]: target path already exists", sourcePath.toAbsolutePath(), outPath.toAbsolutePath()));
            }

            Files.deleteIfExists(outPath);
            Files.createDirectories(getParent(outPath));
            Files.move(sourcePath, outPath);

            return outPath;

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	/* REMOVE methods */
	public static boolean removeFile(Path pathToDel) throws JkRuntimeException {
	    try {
            return Files.deleteIfExists(pathToDel);
        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}
	public static void removeDirectory(Path folderToDel) throws JkRuntimeException {
	    try {
            if (!Files.exists(folderToDel)) {
                throw new FileNotFoundException("[" + folderToDel + "] does not exists");
            }

            if (!Files.isDirectory(folderToDel)) {
                throw new IllegalArgumentException("[" + folderToDel + "] is not a directory");
            }

            removeDirContent(folderToDel);
            Files.delete(folderToDel);

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}
	public static void removeDirectoryIfExists(Path folderToDel) throws JkRuntimeException {
		if(Files.isDirectory(folderToDel)) {
			removeDirectory(folderToDel);
		}
	}
	private static void removeDirContent(Path folder) throws JkRuntimeException {
	    try {
            // remove all files
            List<Path> files = Files.find(folder, 1, (p, a) -> !Files.isDirectory(p)).collect(Collectors.toList());
            for (Path f : files) Files.delete(f);

            // delete recursively the content of dirs
            List<Path> dirs = Files.find(folder, 1, (p, a) -> Files.isDirectory(p)).filter(p -> !p.equals(folder)).collect(Collectors.toList());
            for (Path d : dirs) {
                removeDirContent(d);
                Files.delete(d);
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}

	/* FIND methods */
	public static List<Path> findFiles(Path root, boolean recursive) {
		return findFiles1(root, recursive, Collections.emptyList());
	}
	public static List<Path> findFiles(Path root, boolean recursive, Predicate<Path>... filterConds) {
		return findFiles1(root, recursive, Arrays.asList(filterConds));
	}
	private static List<Path> findFiles1(Path root, boolean recursive, List<Predicate<Path>> filterConds) {
		try {
            if (Files.notExists(root)) {
                return Collections.emptyList();
            }
            Stream<Path> stream = Files.find(root, recursive ? Integer.MAX_VALUE : 1, (p, a) -> !areEquals(p, root));
            for (Predicate<Path> pred : filterConds) {
                stream = stream.filter(pred);
            }
            return stream.distinct().sorted().collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}

	/* MISCELLANEA methods */
	public static Long safeSize(Path path) {
		try {
			return Files.size(path);
		} catch (IOException e) {
			return null;
		}
	}    

	public static String getFileName(Path path) {
		if(path == null) return null;
		String fn = path.toAbsolutePath().normalize().getFileName().toString();
		String ext = getExtension(path);
		return fn.replaceAll(Pattern.quote(".") + ext + "$", "");
	}
	public static String getFileName(String fileName) {
		return StringUtils.isBlank(fileName) ? null : getFileName(Paths.get(fileName));
	}

	public static String getExtension(Path path) {
		String fn = path.toAbsolutePath().normalize().getFileName().toString();
		int index = fn.lastIndexOf('.');
		return index < 0  ? "" : fn.substring(index+1);
	}
	public static String getExtension(String fileName) {
		return getExtension(Paths.get(fileName));
	}

	public static Path getParent(Path path) {
		Path parent = path.normalize().getParent();
		if(parent == null) {
			parent = path.toAbsolutePath().normalize().getParent();
		}
		return parent;
	}
	public static File getParent(File file) {
		return getParent(file.toPath()).toFile();
	}

	public static Path computeSafelyPath(String targetPath) {
		return computeSafelyPath(Paths.get(targetPath));
	}
	public static Path computeSafelyPath(Path targetPath) {
		Path newPath = targetPath;

		if(Files.exists(targetPath)) {
			String fname = getFileName(targetPath);
			String fext = getExtension(targetPath);
			if(fext != null)	fext = "." + fext;
			else				fext = "";
			Path fparent = getParent(targetPath);

			Path tempPath = null;
			for(int i = 1; tempPath == null || Files.exists(tempPath); i++) {
				tempPath = fparent.resolve(strf("%s.%d%s", fname, i, fext));
			}

			newPath = tempPath;
		}

		return newPath;
	}

	public static Path getLauncherPath(Class<?> clazz) {
		try {
			String path = clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			return Paths.get(path.startsWith("/") ? path.substring(1) : path);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean areEquals(Path p1, Path p2) {
		return p1.toAbsolutePath().normalize().equals(p2.toAbsolutePath().normalize());
	}
	public static boolean containsPath(List<Path> source, Path toFind) {
		for(Path p : source) {
			if(areEquals(p, toFind)) {
				return true;
			}
		}
		return false;
	}

	public static String toUrlString(Path source) {
		try {
			return source.toUri().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			return null;
		}
	}
}
