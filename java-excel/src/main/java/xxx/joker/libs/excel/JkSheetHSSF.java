package xxx.joker.libs.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
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
