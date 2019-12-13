package zip;

import org.junit.Test;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.file.JkZip;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TryZip {

    static final Path TEST_FOLDER = Paths.get("C:\\Users\\fede\\Desktop\\zip");

    @Test
    public void zipAllFiles() {
        Path zipPath = TEST_FOLDER.resolve("content.zip");
        List<Path> files = JkFiles.find(TEST_FOLDER, false);
        JkZip.zipFiles(zipPath, files);
    }

    @Test
    public void unzipArchive() {
        Path zipPath = TEST_FOLDER.resolve("content.zip");
        JkZip.unzipArchive(zipPath, TEST_FOLDER);
    }

}
