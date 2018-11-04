package xxx.joker.libs.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.barbano on 21/02/2018.
 */
public class JkExcelSheet {

	private String sheetName;
	private List<List<String>> lines;

	public JkExcelSheet(String sheetName) {
		this.sheetName = sheetName;
		this.lines = new ArrayList<>();
	}

	public JkExcelSheet(String sheetName, List<List<String>> lines) {
		this.sheetName = sheetName;
		this.lines = lines;
	}

	public String getSheetName() {
		return sheetName;
	}

	public List<List<String>> getLines() {
		return lines;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public void setLines(List<List<String>> lines) {
		this.lines = lines;
	}
}
