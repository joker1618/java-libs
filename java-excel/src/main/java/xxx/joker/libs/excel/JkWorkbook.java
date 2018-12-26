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

    JkSheet[] getSheets();

    void persist(Path outputPath);

}
