package xxx.joker.libs.javalibs.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import xxx.joker.libs.javalibs.utils.JkFiles;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/**
 * Created by f.barbano on 21/05/2017.
 */
public class JkExcelUtil {

	private static final DataFormatter DATA_FORMATTER = new DataFormatter(true);

	public static boolean isExcelFile(Path path) {
		String ext = JkFiles.getExtension(path);
		return StringUtils.equalsAnyIgnoreCase(ext, "xls", "xlsx");
	}
	public static boolean isExcelFile(File file) {
		return isExcelFile(file.toPath());
	}

	public static List<JkExcelSheet> parseExcelFile(Path excelPath) throws IOException, InvalidFormatException {
		return parseExcelFile(excelPath.toFile());
	}
	public static List<JkExcelSheet> parseExcelFile(File excelFile) throws IOException, InvalidFormatException {

		if (!excelFile.exists()) {
			throw new FileNotFoundException("The excel file cannot be found. ["+excelFile+"]");
		} else if (excelFile.isDirectory()) {
			throw new IllegalArgumentException("The input path is a folder (must be and excel file). ["+excelFile+"]");
		}

		// Open the workbook (HSSF or XSSF)
		Workbook workbook = openWorkbook(excelFile);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		// Parse every sheet in the excel file
		List<JkExcelSheet> toRet = new ArrayList<>();

		int sheetNum = workbook.getNumberOfSheets();
		for(int i = 0; i < sheetNum; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			List<List<String>> sheetLines = parseExcelSheet(sheet, formulaEvaluator);
			toRet.add(new JkExcelSheet(sheet.getSheetName(), sheetLines));
		}

		return toRet;
	}

	public static JkExcelSheet parseExcelSheet(Sheet sheet) {
		FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		List<List<String>> lines = parseExcelSheet(sheet, formulaEvaluator);
		return new JkExcelSheet(sheet.getSheetName(), lines);
	}

	private static Workbook openWorkbook(File file) throws IOException, InvalidFormatException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return WorkbookFactory.create(fis);

		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	private static List<List<String>> parseExcelSheet(Sheet sheet, FormulaEvaluator evaluator) {
		List<List<String>> sheetLines = new ArrayList<>();

		if (sheet.getPhysicalNumberOfRows() > 0) {
			int lastRowNum = sheet.getLastRowNum();
			for (int j = 0; j <= lastRowNum; j++) {
				Row row = sheet.getRow(j);
				sheetLines.add(parseExcelRow(row, evaluator));
			}
		}

		return sheetLines;
	}

	private static List<String> parseExcelRow(Row row, FormulaEvaluator evaluator) {
		List<String> rowLines = new ArrayList<>();

		if (row != null) {

			int lastCellNum = row.getLastCellNum();
			for (int i = 0; i < lastCellNum; i++) {
				Cell cell = row.getCell(i);

				if (cell == null) {
					rowLines.add("");
				} else {
					String cellValue;
					switch(cell.getCellTypeEnum()) {
						case FORMULA:	cellValue = DATA_FORMATTER.formatCellValue(cell, evaluator);	break;
						default:		cellValue = DATA_FORMATTER.formatCellValue(cell);	break;
					}
					rowLines.add(cellValue);
				}
			}
		}

		return rowLines;
	}


	public static void createExcelFile(File excelFile, JkExcelSheet sheet, boolean overwrite) throws IOException {
		createExcelFile(excelFile, Collections.singletonList(sheet), overwrite);
	}
	public static void createExcelFile(File excelFile, List<JkExcelSheet> sheets, boolean overwrite) throws IOException {
		if(!isExcelFile(excelFile)) {
			throw new FileFormatException(String.format("File \"%s\" does not have excel extension ('.xls', '.xlsx')", excelFile));
		}
		if(excelFile.exists()) {
			if(!excelFile.isFile())	{
				throw new FileAlreadyExistsException(String.format("File \"%s\" already exists and is not a file", excelFile));
			}
			if(!overwrite) {
				throw new FileAlreadyExistsException(String.format("File \"%s\" already exists", excelFile));
			}
			excelFile.delete();
		}

		Workbook workbook = excelFile.getName().endsWith("x") ? new XSSFWorkbook() : new HSSFWorkbook();

		CellStyle cs = workbook.createCellStyle();
		cs.setWrapText(true);

		for(JkExcelSheet xlsSheet : sheets) {
			Sheet sheet = workbook.createSheet(xlsSheet.getSheetName());
			fillSheet(sheet, cs, xlsSheet.getLines());
		}

		JkFiles.getParent(excelFile).mkdirs();

		FileOutputStream fos = new FileOutputStream(excelFile);
		workbook.write(fos);
		fos.close();
		workbook.close();
	}

	private static void fillSheet(Sheet sheet, CellStyle cs, List<List<String>> data) {
		OptionalInt max = data.stream().mapToInt(List::size).max();
		if(!max.isPresent())	return;

		int maxCols = max.getAsInt();
		for(int row = 0; row < data.size(); row++) {
			int numFields = data.get(row).size();
			int col = 0;
			Row xlsRow = sheet.createRow(row);
			for(; col < numFields; col++) {
				Cell cell = xlsRow.createCell(col);
				cell.setCellValue(data.get(row).get(col));
				cell.setCellStyle(cs);
			}
			for(; col < maxCols; col++) {
				Cell cell = xlsRow.createCell(col);
				cell.setCellValue("");
			}
		}
	}
}