package zip;

import org.junit.Test;
import xxx.joker.libs.core.zip.JkZipUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

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
        JkZipUtil.zipFiles(archivePath, INPUT_FOLDER.resolve("file1.txt"));
        JkZipUtil.unzipArchive(archivePath, outFolder);
    }

    public void multiFileTest() {
        Path folder = TEST_FOLDER.resolve("out_B_multi_file");
        Path archivePath = folder.resolve("MultiFile.zip");
        JkZipUtil.zipFiles(archivePath, INPUT_FOLDER.resolve("file1.txt"), INPUT_FOLDER.resolve("dir2/file5.txt"));
        JkZipUtil.unzipArchive(archivePath, folder);
    }

    public void singleFolderTest() {
        Path folder = TEST_FOLDER.resolve("out_C_single_folder");
        Path archivePath = folder.resolve("SingleFolder.zip");
        JkZipUtil.zipFiles(archivePath, INPUT_FOLDER.resolve("dir2"));
        JkZipUtil.unzipArchive(archivePath, folder);
    }

    public void multiFolderTest() {
        Path folder = TEST_FOLDER.resolve("out_D_multi_folder");
        Path archivePath = folder.resolve("MultiFolder.zip");
        JkZipUtil.zipFiles(archivePath, INPUT_FOLDER.resolve("dir1"), INPUT_FOLDER.resolve("dir2"));
        JkZipUtil.unzipArchive(archivePath, folder);
    }

    public void allDataTest() {
        Path folder = TEST_FOLDER.resolve("out_E_all_input_data");
        Path archivePath = folder.resolve("AllData.zip");
        JkZipUtil.zipFiles(archivePath, INPUT_FOLDER.resolve("dir1"), INPUT_FOLDER.resolve("dir2"), INPUT_FOLDER.resolve("file1.txt"), INPUT_FOLDER.resolve("file4.txt"));
        JkZipUtil.unzipArchive(archivePath, folder);
    }
}
