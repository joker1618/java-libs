package xxx.joker.libs.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class JkWorkbookHSSF implements JkWorkbook {

    private HSSFWorkbook wb;
    private List<JkSheetHSSF> sheets = new ArrayList<>();

    public JkWorkbookHSSF() {
        this(new HSSFWorkbook());
    }

    protected JkWorkbookHSSF(HSSFWorkbook wb) {
        this.wb = wb;
        init();
    }

    private void init() {
        for(int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheets.add(new JkSheetHSSF(wb.getSheetAt(i)));
        }
    }

    @Override
    public void close() throws IOException {
        wb.close();
    }

    @Override
    public HSSFWorkbook getWorkbook() {
        return wb;
    }

    @Override
    public boolean containsSheet(String sheetName) {
        return getSheet(sheetName) != null;
    }

    @Override
    public JkExcelType getExcelType() {
        return JkExcelType.HSSF;
    }

    @Override
    public JkSheetHSSF getSheet(int sheetNumber) {
        return sheetNumber < sheets.size() ? sheets.get(sheetNumber) : null;
    }

    @Override
    public JkSheetHSSF getSheet(String sheetName) {
        return getSheet(sheetName, false);
    }

    @Override
    public JkSheetHSSF getSheet(String sheetName, boolean createIfMissing) {
        JkSheetHSSF toRet = null;

        List<JkSheetHSSF> filter = JkStreams.filter(sheets, s -> s.getName().equalsIgnoreCase(sheetName));
        if(filter.isEmpty()) {
            if(createIfMissing) {
                HSSFSheet sheet = wb.createSheet(sheetName);
                toRet = new JkSheetHSSF(sheet);
                sheets.add(toRet);
            }
        } else {
            toRet = filter.get(0);
        }

        return toRet;
    }

    @Override
    public JkSheetHSSF[] getSheets() {
        return sheets.toArray(new JkSheetHSSF[0]);
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
