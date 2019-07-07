package xxx.joker.libs.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.Closeable;
import java.nio.file.Path;

public interface JkWorkbook extends Closeable {

    Workbook getWorkbook();

    boolean containsSheet(String sheetName);

    JkExcelType getExcelType();

    JkSheet getSheet(int sheetNumber);
    JkSheet getSheet(String sheetName);
    JkSheet getSheet(String sheetName, boolean createIfMissing);

    JkSheet cloneSheet(int num, String newName);
    JkSheet cloneSheet(String sheetName, String newName);

    JkSheet[] getSheets();

//    JkSheet removeSheet(int sheetNum);
//    JkSheet removeSheet(String sheetName);

    void persist(Path outputPath);

}
