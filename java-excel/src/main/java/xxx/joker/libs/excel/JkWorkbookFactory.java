package xxx.joker.libs.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xxx.joker.libs.core.exception.JkRuntimeException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JkWorkbookFactory {

    public static JkWorkbook create(Path excelPath) {
        if (!Files.exists(excelPath)) {
            throw new JkRuntimeException("The excel file does not exists. [%s]", excelPath);
        } else if (Files.isDirectory(excelPath)) {
            throw new JkRuntimeException("The input path is a folder (must be and excel file). ["+excelPath+"]");
        }

        JkExcelType excelType = JkExcelType.fromExtension(excelPath);
        if(excelType == null) {
            throw new JkRuntimeException("File %s is not an excel file", excelPath);
        }

        try (FileInputStream fis = new FileInputStream(excelPath.toFile())) {
            Workbook wb = WorkbookFactory.create(fis);
            return excelType == JkExcelType.HSSF ? new JkWorkbookHSSF((HSSFWorkbook)wb) : new JkWorkbookXSSF((XSSFWorkbook)wb);

        } catch(IOException | InvalidFormatException ex) {
            throw new JkRuntimeException(ex, "Error creating workbook from file %s", excelPath);
        }
    }

}
