package xxx.joker.libs.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class JkSheetXSSF extends JkAbstractSheet {

    private final XSSFSheet sheet;

    public JkSheetXSSF(XSSFSheet sheet) {
        super(sheet);
        this.sheet = sheet;
    }

    @Override
    public XSSFSheet getSheet() {
        return sheet;
    }

    public JkExcelChartBuilder createChartBuilder() {
        return new JkExcelChartBuilder(this);
    }


}
