package zip;

import org.junit.Test;
import xxx.joker.libs.core.files.JkZip;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TryZip {

    static final Path TEST_FOLDER = Paths.get("src\\test\\resources\\zip");
    static final Path INPUT_FOLDER = TEST_FOLDER.resolve("input_data");

    @Test
    public void fullZipTest() {
        singleFileTest();
        multiFileTest();
        singleFolderTest();
        multiFolderTest();
        allDataTest();
    }

    public void singleFileTest() {
        Path outFolder = TEST_FOLDER.resolve("out_A_single_file");
        Path archivePath = outFolder.resolve("SingleFile.zip");
        JkZip.zipFiles(archivePath, INPUT_FOLDER.resolve("file1.txt"));
        JkZip.unzipArchive(archivePath, outFolder);
    }

    public void multiFileTest() {
        Path folder = TEST_FOLDER.resolve("out_B_multi_file");
        Path archivePath = folder.resolve("MultiFile.zip");
        JkZip.zipFiles(archivePath, INPUT_FOLDER.resolve("file1.txt"), INPUT_FOLDER.resolve("dir2/file5.txt"));
        JkZip.unzipArchive(archivePath, folder);
    }

    public void singleFolderTest() {
        Path folder = TEST_FOLDER.resolve("out_C_single_folder");
        Path archivePath = folder.resolve("SingleFolder.zip");
        JkZip.zipFiles(archivePath, INPUT_FOLDER.resolve("dir2"));
        JkZip.unzipArchive(archivePath, folder);
    }

    public void multiFolderTest() {
        Path folder = TEST_FOLDER.resolve("out_D_multi_folder");
        Path archivePath = folder.resolve("MultiFolder.zip");
        JkZip.zipFiles(archivePath, INPUT_FOLDER.resolve("dir1"), INPUT_FOLDER.resolve("dir2"));
        JkZip.unzipArchive(archivePath, folder);
    }

    public void allDataTest() {
        Path folder = TEST_FOLDER.resolve("out_E_all_input_data");
        Path archivePath = folder.resolve("AllData.zip");
        JkZip.zipFiles(archivePath, INPUT_FOLDER.resolve("dir1"), INPUT_FOLDER.resolve("dir2"), INPUT_FOLDER.resolve("file1.txt"), INPUT_FOLDER.resolve("file4.txt"));
        JkZip.unzipArchive(archivePath, folder);
    }
}
