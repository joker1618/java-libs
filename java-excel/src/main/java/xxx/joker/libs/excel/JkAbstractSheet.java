package xxx.joker.libs.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import xxx.joker.libs.core.datetime.JkTime;
import xxx.joker.libs.core.objects.Area;
import xxx.joker.libs.core.objects.Pos;
import xxx.joker.libs.core.utils.JkConverter;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

abstract class JkAbstractSheet implements JkSheet {

    private static final DataFormatter DATA_FORMATTER = new DataFormatter(true);

    private final Sheet sheet;
    private FormulaEvaluator formulaEvaluator;

    protected JkAbstractSheet(Sheet sheet) {
        this.sheet = sheet;
        this.formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
    }

    @Override
    public String getName() {
        return sheet.getSheetName();
    }

    @Override
    public int getNumberOfRows() {
        return sheet.getLastRowNum() + 1;
    }

    @Override
    public Pos findCellPos(String cellValue, boolean caseSensitive, boolean searchByColumn) {
        if(searchByColumn) {
            int maxColNum = -1;
            for (int c = 0; maxColNum == -1 || c < maxColNum; c++) {
                for (int r = 0; r < getNumberOfRows(); r++) {
                    Row row = sheet.getRow(r);
                    if (row != null) {
                        if (c == 0 && maxColNum < row.getLastCellNum()) {
                            maxColNum = row.getLastCellNum();
                        }
                        boolean res = caseSensitive ? cellValue.equals(getString(r, c)) : cellValue.equalsIgnoreCase(getString(r, c));
                        if (res) {
                            return new Pos(r, c);
                        }
                    }
                }
            }

        } else {
            for(int r = 0; r < getNumberOfRows(); r++) {
                Row row = sheet.getRow(r);
                if(row != null) {
                    for(int c = 0; c < row.getLastCellNum(); c++) {
                        boolean res = caseSensitive ? cellValue.equals(getString(r, c)) : cellValue.equalsIgnoreCase(getString(r, c));
                        if (res) {
                            return new Pos(r, c);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public boolean isValueEmpty(int rowNum, int colNum) {
        return StringUtils.isBlank(getString(rowNum, colNum));
    }

    @Override
    public String getString(int rowNum, int colNum) {
        String val = null;

        Cell cell = getCell(rowNum, colNum, false);
        if(cell != null) {
            switch(cell.getCellTypeEnum()) {
                case FORMULA:	val = DATA_FORMATTER.formatCellValue(cell, formulaEvaluator);	break;
                default:		val = DATA_FORMATTER.formatCellValue(cell);	break;
            }
        }

        return val;
    }

    @Override
    public Integer getInt(int rowNum, int colNum) {
        return JkConverter.stringToInteger(getString(rowNum, colNum));
    }

    @Override
    public Integer getInt(int rowNum, int colNum, NumberFormat nf) {
        try {
            return nf.parse(getString(rowNum, colNum)).intValue();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Long getLong(int rowNum, int colNum) {
        return JkConverter.stringToLong(getString(rowNum, colNum));
    }

    @Override
    public Long getLong(int rowNum, int colNum, NumberFormat nf) {
        try {
            return nf.parse(getString(rowNum, colNum)).longValue();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public Double getDouble(int rowNum, int colNum) {
        return JkConverter.stringToDouble(getString(rowNum, colNum));
    }

    @Override
    public Double getDouble(int rowNum, int colNum, NumberFormat nf) {
        try {
            return nf.parse(getString(rowNum, colNum)).doubleValue();
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    public LocalDate getLocalDate(int rowNum, int colNum, String pattern) {
        return getLocalDate(rowNum, colNum, DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public LocalDate getLocalDate(int rowNum, int colNum, DateTimeFormatter dtf) {
        try {
            return LocalDate.parse(getString(rowNum, colNum), dtf);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    @Override
    public LocalDateTime getLocalDateTime(int rowNum, int colNum, String pattern) {
        return getLocalDateTime(rowNum, colNum, DateTimeFormatter.ofPattern(pattern));
    }

    @Override
    public LocalDateTime getLocalDateTime(int rowNum, int colNum, DateTimeFormatter dtf) {
        try {
            return LocalDateTime.parse(getString(rowNum, colNum), dtf);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    @Override
    public void setValue(int rowNum, int colNum, Object value) {
        setValue(rowNum, colNum, value, null);
    }

    @Override
    public void setValue(int rowNum, int colNum, Object value, CellStyle cellStyle) {
        if(value == null) {
            removeCell(rowNum, colNum);

        } else {
            Cell cell = getCell(rowNum, colNum, true);

            if(value instanceof LocalDate) {
                cell.setCellValue(Date.valueOf((LocalDate) value));
            } else if(value instanceof LocalDateTime) {
                cell.setCellValue(new Date(JkTime.of((LocalDate) value).getTotalMillis()));
            } else if(value instanceof Integer) {
                cell.setCellValue(JkConverter.stringToInteger(String.valueOf(value)));
            } else if(value instanceof Long) {
                cell.setCellValue(JkConverter.stringToLong(String.valueOf(value)));
            } else if(value instanceof Double) {
                cell.setCellValue(JkConverter.stringToDouble(String.valueOf(value)));
            } else {
                cell.setCellValue(String.valueOf(value));
            }

            if(cellStyle != null) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    @Override
    public void setValues(int rowNum, int colNum, List<?> values) {
        setValues(rowNum, colNum, values, null);
    }

    @Override
    public void setValues(int rowNum, int colNum, List<?> values, CellStyle cellStyle) {
        if(values != null && !values.isEmpty()) {
            for(int i = 0; i < values.size(); i++) {
                setValue(rowNum, colNum + i, values.get(i), cellStyle);
            }
        }
    }


    @Override
    public void setStyle(int rowNum, int colNum, CellStyle cellStyle) {
        getCell(rowNum, colNum, true).setCellStyle(cellStyle);
    }

    @Override
    public void setStyle(Area area, CellStyle cellStyle) {
        for(int r = area.getY(); r < area.getEndY(); r++) {
            for(int c = area.getX(); c < area.getEndX(); c++) {
                setStyle(r, c, cellStyle);
            }
        }
    }

    @Override
    public int getColWidth(int colNum) {
        int excelWidth = sheet.getColumnWidth(colNum);
        return (excelWidth - 182) / 256;
    }

    @Override
    public void setColWidth(int colNum, int points) {
        int width = 182 + 256 * points;
        sheet.setColumnWidth(colNum, width);
    }

    @Override
    public float getRowHeight(int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row == null ? 0f : row.getHeightInPoints();
    }

    @Override
    public void setRowHeight(int rowNum, float points) {
        Row row = sheet.getRow(rowNum);
        if(row == null) {
            row = sheet.createRow(rowNum);
        }

        float fixedPoints = fixRowHeightPoints(points);
        row.setHeightInPoints(fixedPoints);
    }

    @Override
    public void setColWidth(int colStart, int offset, int points) {
        for(int i = 0; i < offset; i++) {
            setColWidth(i + colStart, points);
        }
    }

    @Override
    public void setRowHeight(int rowStart, int offset, float points) {
        for(int i = 0; i < offset; i++) {
            setRowHeight(i + rowStart, points);
        }
    }

    @Override
    public void autoSizeCol(int... colNums) {
        Arrays.stream(colNums).forEach(sheet::autoSizeColumn);
    }

    @Override
    public void autoSizeCol(int colStart, int offset) {
        for(int i = 0; i < offset; i++) {
            sheet.autoSizeColumn(i + colStart);
        }
    }

    @Override
    public void setDefaultRowHeight(float points) {
        float fixedPoints = fixRowHeightPoints(points);
        sheet.setDefaultRowHeightInPoints(fixedPoints);
    }

    @Override
    public CellReference createCellReference(int rowNum, int colNum) {
        CellReference cellReference = new CellReference(rowNum, colNum);
        String[] refParts = cellReference.getCellRefParts();
        String strRef = strf("%s!$%s$%s", getName(), refParts[2], refParts[1]);
        return new CellReference(strRef);
    }


    protected Cell getCell(int rowNum, int colNum, boolean createIfMissing) {
        Cell cell = null;

        Row row = sheet.getRow(rowNum);
        if(row == null && createIfMissing) {
            row = sheet.createRow(rowNum);
        }

        if(row != null) {
            cell = row.getCell(colNum);
            if(cell == null && createIfMissing) {
                cell = row.createCell(colNum);
            }
        }

        return cell;
    }

    protected boolean removeCell(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if(row != null) {
            Cell cell = row.getCell(colNum);
            if(cell != null) {
                row.removeCell(cell);
                return true;
            }
        }

        return false;
    }

    protected float fixRowHeightPoints(float points) {
        double pixelSize = 0.75;
        double rem = points % pixelSize;
        if(rem > pixelSize/2)  {
            points += pixelSize - rem;
        } else {
            points -= rem;
        }
        return points;
    }
}
