package xxx.joker.libs.javalibs.zip;

import xxx.joker.libs.javalibs.exception.JkRuntimeException;
import xxx.joker.libs.javalibs.utils.JkFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class JkZipUtil {

    public static void unzipArchive(Path archivePath, Path outFolder) throws JkRuntimeException {
        byte[] buffer = new byte[1024];

        try (FileInputStream fis = new FileInputStream(archivePath.toFile());
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry = zis.getNextEntry();
            while(zipEntry != null){
                File newFile = outFolder.resolve(zipEntry.getName()).toFile();
                if(zipEntry.isDirectory()) {
                    Files.createDirectories(newFile.toPath());
                } else {
                    Files.createDirectories(JkFiles.getParent(newFile.toPath()));
                    try(FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    /**
     * Create a ZIP archive of filesToZip and/or folders in input
     */
    public static void zipFiles(Path archivePath, Path... filesToZip) throws JkRuntimeException {
        zipFiles(archivePath, Arrays.asList(filesToZip));
    }

    public static void zipFiles(Path archivePath, Collection<Path> filesToZip) throws JkRuntimeException {
        Path middleOutPath = JkFiles.computeSafelyPath(archivePath);

        try (FileOutputStream fos = new FileOutputStream(middleOutPath.toFile());
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for(Path path : filesToZip) {
                File fileToZip = path.toFile();
                zipFile(fileToZip, fileToZip.getName(), zipOut);
            }

        } catch (Exception ex) {
            try {
                Files.deleteIfExists(middleOutPath);
            } catch (IOException e) {
                throw new JkRuntimeException(e);
            }
            throw new JkRuntimeException(ex);
        }

        if(!JkFiles.areEquals(archivePath, middleOutPath)) {
            JkFiles.moveFile(middleOutPath, archivePath, true);
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

        if (fileToZip.isDirectory()) {
            String dirname = fileName.endsWith("/") ? fileName : fileName + "/";
            zipOut.putNextEntry(new ZipEntry(dirname));
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            if(children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }

        try(FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

}