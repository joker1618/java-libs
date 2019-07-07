package xxx.joker.libs.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JkWorkbookXSSF implements JkWorkbook {

    private XSSFWorkbook wb;
    private List<JkSheetXSSF> sheets = new ArrayList<>();

    public JkWorkbookXSSF() {
        this(new XSSFWorkbook());
    }

    public JkWorkbookXSSF(XSSFWorkbook wb) {
        this.wb = wb;
        init();
    }

    private void init() {
        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheets.add(new JkSheetXSSF(wb.getSheetAt(i)));
        }
    }

    @Override
    public void close() throws IOException {
        wb.close();
    }

    @Override
    public XSSFWorkbook getWorkbook() {
        return wb;
    }

    @Override
    public boolean containsSheet(String sheetName) {
        return getSheet(sheetName) != null;
    }

    @Override
    public JkExcelType getExcelType() {
        return JkExcelType.XSSF;
    }

    @Override
    public JkSheetXSSF getSheet(int sheetNumber) {
        return sheetNumber < sheets.size() ? sheets.get(sheetNumber) : null;
    }

    @Override
    public JkSheetXSSF getSheet(String sheetName) {
        return getSheet(sheetName, false);
    }

    @Override
    public JkSheetXSSF getSheet(String sheetName, boolean createIfMissing) {
        JkSheetXSSF toRet = null;

        List<JkSheetXSSF> filter = JkStreams.filter(sheets, s -> s.getName().equalsIgnoreCase(sheetName));
        if(filter.isEmpty()) {
            if(createIfMissing) {
                XSSFSheet sheet = wb.createSheet(sheetName);
                toRet = new JkSheetXSSF(sheet);
                sheets.add(toRet);
            }
        } else {
            toRet = filter.get(0);
        }

        return toRet;
    }

    @Override
    public JkSheet cloneSheet(int num, String newName) {
        XSSFSheet cloned = wb.cloneSheet(num, newName);
        return new JkSheetXSSF(cloned);
    }

     @Override
    public JkSheet cloneSheet(String sheetName, String newName) {
         int sidx = wb.getSheetIndex(sheetName);
         XSSFSheet cloned = wb.cloneSheet(sidx, newName);
        return new JkSheetXSSF(cloned);
    }

    @Override
    public JkSheetXSSF[] getSheets() {
        return sheets.toArray(new JkSheetXSSF[0]);
    }

    @Override
    public void persist(Path outputPath) {
        try {
            Files.createDirectories(JkFiles.getParent(outputPath));
            try(OutputStream os = new FileOutputStream(outputPath.toFile())) {
                wb.write(os);
            }

        } catch(IOException ex) {
            throw new JkRuntimeException(ex, "Error persisting workbook at path '%s'", outputPath);
        }
    }
}
