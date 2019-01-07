package xxx.joker.libs.excel;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import xxx.joker.libs.core.objects.Area;
import xxx.joker.libs.core.objects.Pos;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public interface JkSheet {

    Sheet getSheet();

    String getName();

    int getNumberOfRows();

    Pos findCellPos(String cellValue, boolean caseSensitive, boolean searchByColumn);

    boolean isValueEmpty(int rowNum, int colNum);

    String getString(int rowNum, int colNum);
    Integer getInt(int rowNum, int colNum);
    Integer getInt(int rowNum, int colNum, NumberFormat nf);
    Long getLong(int rowNum, int colNum);
    Long getLong(int rowNum, int colNum, NumberFormat nf);
    Double getDouble(int rowNum, int colNum);
    Double getDouble(int rowNum, int colNum, NumberFormat nf);
    LocalDate getLocalDate(int rowNum, int colNum, String pattern);
    LocalDate getLocalDate(int rowNum, int colNum, DateTimeFormatter dtf);
    LocalDateTime getLocalDateTime(int rowNum, int colNum, String pattern);
    LocalDateTime getLocalDateTime(int rowNum, int colNum, DateTimeFormatter dtf);

    void setValue(int rowNum, int colNum, Object value);
    void setValue(int rowNum, int colNum, Object value, CellStyle cellStyle);
    void setValues(int rowNum, int colNum, List<?> values);
    void setValues(int rowNum, int colNum, List<?> values, CellStyle cellStyle);

    void setStyle(int rowNum, int colNum, CellStyle cellStyle);
    void setStyle(Area area, CellStyle cellStyle);

    int getColWidth(int colNum);
    void setColWidth(int colNum, int points);
    void setColWidth(int colStart, int offset, int points);

    float getRowHeight(int rowNum);
    void setDefaultRowHeight(float points);
    void setRowHeight(int rowNum, float points);
    void setRowHeight(int rowStart, int offset, float points);

    void autoSizeCol(int... colNums);
    void autoSizeCol(int colStart, int offset);

    CellReference createCellReference(int rowNum, int colNum);

}

