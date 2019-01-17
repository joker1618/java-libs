package xxx.joker.libs.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;

public class JkSheetHSSF extends JkAbstractSheet {

    private final HSSFSheet sheet;

    public JkSheetHSSF(HSSFSheet sheet) {
        super(sheet);
        this.sheet = sheet;
    }

    @Override
    public HSSFSheet getSheet() {
        return sheet;
    }




}
