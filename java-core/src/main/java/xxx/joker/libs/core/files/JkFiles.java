package xxx.joker.libs.core.files;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.utils.JkBytes;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 26/05/2018.
 */

public class JkFiles {

	private static final String NEWLINE = StringUtils.LF;
	private static final String TEMP_FILE = "generic.file.util.temp";

	/* WRITE methods */
	// Append
	public static void appendToFile(Path outputPath, String data) {
		appendToFile(outputPath, data, null);
	}
	public static void appendToFile(Path outputPath, String data, Charset encoding) {
		appendToFile(outputPath, data, encoding, false);
	}
	public static void appendToFile(Path outputPath, String data, Charset encoding, boolean finalNewline) {
		appendToFile(outputPath, Collections.singletonList(data), encoding, finalNewline);
	}

	public static void appendToFile(Path outputPath, List<String> lines) {
		appendToFile(outputPath, lines, null);
	}
	public static void appendToFile(Path outputPath, List<String> lines, Charset encoding) {
		appendToFile(outputPath, lines, encoding, !lines.isEmpty());
	}
	public static void appendToFile(Path outputPath, List<String> lines, Charset encoding, boolean finalNewline) {
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
	public static void writeFile(Path outputPath, String content, boolean overwrite) {
		writeFile(outputPath, content, overwrite, null);
	}
	public static void writeFile(Path outputPath, String content, boolean overwrite, Charset encoding) {
		writeFile(outputPath, content, overwrite, encoding, false);
	}
	public static void writeFile(Path outputPath, String content, boolean overwrite, Charset encoding, boolean finalNewLine) {
		writeFile(outputPath, Arrays.asList(content), overwrite, encoding, finalNewLine);
	}

	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite) {
		writeFile(outputPath, lines, overwrite, null);
	}
	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite, Charset encoding) {
		writeFile(outputPath, lines, overwrite, encoding, !lines.isEmpty());
	}
	public static void writeFile(Path outputPath, List<String> lines, boolean overwrite, Charset encoding, boolean finalNewline) {
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

	public static void writeFile(Path outputPath, byte[] bytes, boolean overwrite) {
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

	// Insert at the beginning
	public static void insertFirstToFile(Path outputPath, String content) {
		insertFirstToFile(outputPath, content, null);
	}
	public static void insertFirstToFile(Path outputPath, String content, Charset encoding) {
		insertFirstToFile(outputPath, Collections.singletonList(content), encoding);
	}

	public static void insertFirstToFile(Path outputPath, List<String> lines) {
		insertFirstToFile(outputPath, lines, null);
	}
	public static void insertFirstToFile(Path outputPath, List<String> lines, Charset encoding) {
		try {
            if (!Files.exists(outputPath)) {
                writeFile(outputPath, lines, false, encoding);
            } else {
                Path tempFile = Paths.get(TEMP_FILE);
                writeFile(tempFile, lines, true, encoding);
                appendToFile(tempFile, Files.readAllLines(outputPath, encoding));
                Files.move(tempFile, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException ex) {
            throw new JkRuntimeException(ex);
        }
	}


	/* READ methods */
	public static List<String> readLines(InputStream is) {
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
	public static List<String> readLines(Path filePath, Predicate<String>... filters) {
		try {
			List<String> lines = Files.readAllLines(filePath);

			Stream<String> stream = lines.stream();
			for(Predicate<String> filter : filters) {
				stream = stream.filter(filter);
			}

			return stream.collect(Collectors.toList());

		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}

	public static byte[] readBytes(Path path) {
		try {
			int size = (int) Files.size(path);
			return readBytes(path, 0, size);
		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}
	public static byte[] readBytes(Path path, long start, int length) {
		try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
			return readBytes(raf, start, length);
		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}
	public static byte[] readBytes(RandomAccessFile raf, long start, int length) {
		try {
			byte[] toRet = new byte[length];
			raf.seek(start);
			int counter = raf.read(toRet);
			if (counter == length) {
				return toRet;
			}

			toRet = Arrays.copyOfRange(toRet, 0, counter);
			while (counter < length) {
				int rem = length - counter;
				byte[] arr = new byte[rem];
				int read = raf.read(arr);
				toRet = JkBytes.mergeArrays(toRet, Arrays.copyOfRange(arr, 0, read));
				counter += read;
			}

			return toRet;

		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}



	/* FIND methods */
	public static List<Path> findFiles(Path root, int maxDepth, Predicate<Path>... filterConds) {
		return findFiles1(root, maxDepth, Arrays.asList(filterConds));
	}
	public static List<Path> findFiles(Path root, boolean recursive, Predicate<Path>... filterConds) {
		return findFiles1(root, recursive ? Integer.MAX_VALUE : 1, Arrays.asList(filterConds));
	}
	private static List<Path> findFiles1(Path root, int maxDepth, List<Predicate<Path>> filterConds) {
		try {
			if (Files.notExists(root)) {
				return Collections.emptyList();
			}
			Stream<Path> stream = Files.find(root, maxDepth, (p, a) -> !JkFiles.areEquals(p, root));
			for (Predicate<Path> pred : filterConds) {
				stream = stream.filter(pred);
			}
			return stream.distinct().sorted().collect(Collectors.toList());

		} catch (IOException e) {
			throw new JkRuntimeException(e);
		}
	}


	/* REMOVE methods */
	public static boolean remove(Path fileToDel) {
		if(!Files.exists(fileToDel)) {
			return false;
		}

		try {
			if(Files.isRegularFile(fileToDel)) {
				Files.delete(fileToDel);

			} else {
				List<Path> files = JkFiles.findFiles(fileToDel, true);
				Map<Boolean, List<Path>> filesMap = JkStreams.toMap(files, Files::isRegularFile);
				// Remove all files before
				for(Path file : filesMap.getOrDefault(true, Collections.emptyList())) {
					Files.delete(file);
				}
				// Remove all subfolder, beginning with leaves
				List<Path> subFolders = JkStreams.reverseOrder(filesMap.getOrDefault(false, Collections.emptyList()), Comparator.comparing(Path::getNameCount));
				for(Path subFolder : subFolders) {
					Files.delete(subFolder);
				}
				// Remove folder in input (pathToDel)
				Files.delete(fileToDel);
			}

			return true;

		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}


	/* COPY-MOVE methods */
	public static void copyFile(Path sourcePath, Path targetPath, boolean overwrite) {
		copyFile1(sourcePath, targetPath, overwrite, false);
	}
	public static Path copyFileSafely(Path sourcePath, Path targetPath) {
		return copyFile1(sourcePath, targetPath, false, true);
	}
	private static Path copyFile1(Path sourcePath, Path targetPath, boolean overwrite, boolean safePath) {
		try {
			if (!Files.exists(sourcePath)) {
				throw new FileNotFoundException(strf("Source file [%s] not exists!", sourcePath));
			}

			if (safePath) targetPath = safePath(targetPath);

			if (Files.exists(targetPath)) {
				if(!overwrite) {
					throw new FileAlreadyExistsException(strf("Unable to move [%s] to [%s]: target path already exists", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
				if(Files.isDirectory(targetPath) && Files.isRegularFile(sourcePath)) {
					throw new FileAlreadyExistsException(strf("Unable to move file [%s] to [%s]: target path is a directory", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
				if(Files.isDirectory(sourcePath) && Files.isRegularFile(targetPath)) {
					throw new FileAlreadyExistsException(strf("Unable to move folder [%s] to [%s]: target path is a file", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
			}

			remove(targetPath);

			if(Files.isRegularFile(sourcePath)) {
				Files.createDirectories(getParent(targetPath));
				Files.copy(sourcePath, targetPath);

			} else {
				Files.createDirectories(targetPath);
				Path absSource = sourcePath.toAbsolutePath();
				Path absTarget = targetPath.toAbsolutePath();
				List<Path> files = findFiles(absSource, true);
				Map<Boolean, List<Path>> filesMap = JkStreams.toMap(files, Files::isRegularFile);
				// Create all folders before
				for(Path folder : filesMap.getOrDefault(false, Collections.emptyList())) {
					Path targetSubFolder = absTarget.resolve(absSource.relativize(folder));
					Files.createDirectories(targetSubFolder);
				}
				// Copy all files
				for(Path sourceFile : filesMap.getOrDefault(true, Collections.emptyList())) {
					Path targetFile = absTarget.resolve(absSource.relativize(sourceFile));
					Files.copy(sourceFile, targetFile);
				}
			}

			return targetPath;

		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
		}
	}

	public static void moveFile(Path sourcePath, Path targetPath, boolean overwrite) {
		moveFile1(sourcePath, targetPath, overwrite, false);
	}
	public static Path moveFileSafely(Path sourcePath, Path targetPath) {
		return moveFile1(sourcePath, targetPath, false, true);
	}
	private static Path moveFile1(Path sourcePath, Path targetPath, boolean overwrite, boolean safePath) {
		try {
			if (!Files.exists(sourcePath)) {
				throw new FileNotFoundException(strf("Source path [%s] not exists!", sourcePath.toAbsolutePath()));
			}

			if (safePath) targetPath = safePath(targetPath);

			if (Files.exists(targetPath)) {
				if(!overwrite) {
					throw new FileAlreadyExistsException(strf("Unable to move [%s] to [%s]: target path already exists", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
				if(Files.isDirectory(targetPath) && Files.isRegularFile(sourcePath)) {
					throw new FileAlreadyExistsException(strf("Unable to move file [%s] to [%s]: target path is a directory", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
				if(Files.isDirectory(sourcePath) && Files.isRegularFile(targetPath)) {
					throw new FileAlreadyExistsException(strf("Unable to move folder [%s] to [%s]: target path is a file", sourcePath.toAbsolutePath(), targetPath.toAbsolutePath()));
				}
			}

			remove(targetPath);
			Files.createDirectories(getParent(targetPath));
			Files.move(sourcePath, targetPath);

			return targetPath;

		} catch (IOException ex) {
			throw new JkRuntimeException(ex);
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
		String fn = path.toAbsolutePath().normalize().getFileName().toString();
		if(Files.isDirectory(path)) return fn;
		String ext = getExtension(path);
		return fn.replaceAll(Pattern.quote(".") + ext + "$", "");
	}
	public static String getFileName(String fileName) {
		return StringUtils.isBlank(fileName) ? null : getFileName(Paths.get(fileName));
	}

	public static String getExtension(Path path) {
		if(Files.isDirectory(path)) {
			return "";
		}
		String fn = path.toAbsolutePath().normalize().getFileName().toString();
		int index = fn.lastIndexOf('.');
		return index == -1  ? "" : fn.substring(index+1);
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
		Path parent = getParent(file.toPath());
		return parent == null ? null : parent.toFile();
	}

	public static Path safePath(String targetPath) {
		return safePath(Paths.get(targetPath));
	}
	public static Path safePath(Path targetPath) {
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
			URI uri = clazz.getProtectionDomain().getCodeSource().getLocation().toURI();
			return toPath(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/* CONVERSIONS */
	public static Path[] toPaths(String[] source) {
		Path[] toRet = new Path[source.length];
		for(int i = 0; i < source.length; i++) {
			toRet[i] = Paths.get(source[i]);
		}
		return toRet;
	}
	public static Path toPath(URI sourceURI) {
		String path = sourceURI.getPath();
		return Paths.get(path.startsWith("/") ? path.substring(1) : path);
	}

	public static String toURL(Path source) {
		try {
			return source.toUri().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			return null;
		}
	}


	/* TESTS */
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

}