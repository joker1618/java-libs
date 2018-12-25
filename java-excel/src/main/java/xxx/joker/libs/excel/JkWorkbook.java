package xxx.joker.libs.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.nio.file.Path;

public interface JkWorkbook extends AutoCloseable {

    Workbook getWorkbook();

    boolean containsSheet(String sheetName);

    ExcelType getExcelType();

    JkSheet getSheet(int sheetNumber);
    JkSheet getSheet(String sheetName);
    JkSheet getSheet(String sheetName, boolean createIfMissing);

    JkSheet[] getSheets();

    void persist(Path outputPath);

}
