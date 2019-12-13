package xxx.joker.libs.excel;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.charts.XSSFChartLegend;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.object.JkArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JkExcelChartBuilder {

    private final JkSheetXSSF sheet;

    private JkExcelChartSerie serieX;
    private List<JkExcelChartSerie> seriesY = new ArrayList<>();

    private CellPos dataLocation;
    private boolean dataOrientationVertical = true;

    private XSSFCellStyle dataHeaderStyle;
    private XSSFCellStyle dataXStyle;
    private XSSFCellStyle dataYStyle;

    protected JkExcelChartBuilder(JkSheetXSSF sheet) {
        this.sheet = sheet;
    }

    public void reset() {
        serieX = null;
        seriesY.clear();
        dataLocation = null;
        dataOrientationVertical = true;
        dataHeaderStyle = null;
        dataXStyle = null;
        dataYStyle = null;
    }

    public void setSerieX(JkExcelChartSerie serie) {
        serieX = serie;
    }
    public void setSerieX(String serieName, List<? extends Number> dataList) {
        Double[] data = JkStreams.map(dataList, Number::doubleValue).toArray(new Double[0]);
        setSerieX(new JkExcelChartSerie(serieName, data));
    }
    public void setSerieX(String serieName, int[] data) {
        setSerieX(new JkExcelChartSerie(serieName, data));
    }
    public void setSerieX(String serieName, double[] data) {
        setSerieX(new JkExcelChartSerie(serieName, data));
    }

    public void addSerieY(JkExcelChartSerie serie) {
        seriesY.add(serie);
    }
    public void addSerieY(String serieName, List<? extends Number> dataList) {
        Double[] data = JkStreams.map(dataList, Number::doubleValue).toArray(new Double[0]);
        addSerieY(new JkExcelChartSerie(serieName, data));
    }
    public void addSerieY(String serieName, int[] data) {
        addSerieY(new JkExcelChartSerie(serieName, data));
    }
    public void addSerieY(String serieName, double[] data) {
        addSerieY(new JkExcelChartSerie(serieName, data));
    }

    public void setDataLocation(int rowNum, int colNum) {
        dataLocation = new CellPos(rowNum, colNum);
    }
    public void setDataOrientationVertical(boolean dataOrientationVertical) {
        this.dataOrientationVertical = dataOrientationVertical;
    }

    public void setDataHeaderStyle(XSSFCellStyle dataHeaderStyle) {
        this.dataHeaderStyle = dataHeaderStyle;
    }
    public void setDataXStyle(XSSFCellStyle dataXStyle) {
        this.dataXStyle = dataXStyle;
    }
    public void setDataYStyle(XSSFCellStyle dataYStyle) {
        this.dataYStyle = dataYStyle;
    }

    public void drawChart(String chartTitle, JkArea chartArea) {
        if(serieX == null) {
            throw new JkRuntimeException("X axis not set");
        }
        if(seriesY.isEmpty()) {
            throw new JkRuntimeException("No series data found");
        }

        CellPos dataCellPos = dataLocation == null ? new CellPos(0, chartArea.getEndX()+1) : dataLocation;

        XSSFCellStyle styleHeader = getHeaderStyle(dataOrientationVertical);
        XSSFCellStyle styleDataX = getDataXStyle();
        XSSFCellStyle styleDataY = getDataYStyle();

        // X axis data
        JkArea areaX = computeOutputArea(dataCellPos, 0, serieX.getData().size(), dataOrientationVertical);
        int pos = -1;
        for(int r = areaX.getY(); r < areaX.getEndY(); r++) {
            for(int c = areaX.getX(); c < areaX.getEndX(); c++, pos++) {
                if(pos == -1) {
                    sheet.setValue(r, c, serieX.getName(), styleHeader);
                } else {
                    sheet.setValue(r, c, serieX.getData().get(pos), styleDataX);
                }
            }
        }

        // Series data
        List<JkArea> areasY = new ArrayList<>();
        for(int i = 0; i < seriesY.size(); i++) {
            JkArea areaY = computeOutputArea(dataCellPos, i+1, serieX.getData().size(), dataOrientationVertical);
            areasY.add(areaY);
            pos = -1;
            for(int r = areaY.getY(); r < areaY.getEndY(); r++) {
                for(int c = areaY.getX(); c < areaY.getEndX(); c++, pos++) {
                    if(pos == -1) {
                        sheet.setValue(r, c, seriesY.get(i).getName(), styleHeader);
                    } else {
                        sheet.setValue(r, c, seriesY.get(i).getData().get(pos), styleDataY);
                    }
                }
            }
        }

        // Autosize data column
        if(!dataOrientationVertical) {
            for(int c = areaX.getX(); c < areaX.getEndX(); c++) {
                sheet.autoSizeCol(c);
            }
        } else {
            for(int c = 0; c < seriesY.size() + 1; c++) {
                sheet.autoSizeCol(c + areaX.getX());
            }
        }

        /* Create a drawing canvas on the worksheet */
        XSSFDrawing drawing = sheet.getSheet().createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, chartArea.getX(), chartArea.getY(), chartArea.getEndX(), chartArea.getEndY());
        XSSFChart lineChart = drawing.createChart(anchor);
        lineChart.setTitle(chartTitle);

        /* Define legends for the line chart and set the position of the legend */
        XSSFChartLegend legend = lineChart.getOrCreateLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        /* Create data for the chart */
        LineChartData chartData = lineChart.getChartDataFactory().createLineChartData();
        ChartDataSource<Number> xds = DataSources.fromNumericCellRange(sheet.getSheet(), convertArea(areaX));

        for(int i = 0; i < areasY.size(); i++) {
            JkArea areaY = areasY.get(i);
            LineChartSeries chartSerie = chartData.addSeries(xds, DataSources.fromNumericCellRange(sheet.getSheet(), convertArea(areaY)));
            CellReference cellRef = sheet.createCellReference(areaY.getY(), areaY.getX());
            chartSerie.setTitle(cellRef);
        }

        /* Define chart AXIS */
        ChartAxis bottomAxis = lineChart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = lineChart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        /* Plot the chart with the inputs from data and chart axis */
        lineChart.plot(chartData, new ChartAxis[] { bottomAxis, leftAxis });
    }

    private JkArea computeOutputArea(CellPos startCellPos, int serieOffset, int dataSize, boolean vertical) {
        JkArea area = new JkArea();
        area.setX(startCellPos.getColNum() + (vertical ? serieOffset : 0));
        area.setY(startCellPos.getRowNum() + (vertical ? 0 : serieOffset));
        area.setWidth(1 + (vertical ? 0 : dataSize));
        area.setHeight(1 + (vertical ? dataSize : 0));
        return area;
    }

    private CellRangeAddress convertArea(JkArea area) {
        if(area.getWidth() == 1) {
            return new CellRangeAddress(area.getY() + 1, area.getEndY() - 1, area.getX(), area.getEndX() - 1);
        } else {
            return new CellRangeAddress(area.getY(), area.getEndY() - 1, area.getX() + 1, area.getEndX() - 1);
        }
    }


    private XSSFCellStyle getHeaderStyle(boolean vertical) {
        if(dataHeaderStyle == null) {
            XSSFFont hfont = sheet.getSheet().getWorkbook().createFont();
            hfont.setBold(true);
            hfont.setFontName("Calibri");
            hfont.setFontHeightInPoints((short) 10);
            hfont.setColor(IndexedColors.WHITE.index);
            XSSFCellStyle hstyle = sheet.getSheet().getWorkbook().createCellStyle();
            hstyle.setFont(hfont);
            hstyle.setFillForegroundColor(new XSSFColor(new Color(0, 32, 96)));
            hstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            hstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            hstyle.setAlignment(vertical ? HorizontalAlignment.CENTER : HorizontalAlignment.LEFT);
            dataHeaderStyle = hstyle;
        }
        return dataHeaderStyle;
    }
    private XSSFCellStyle getDataXStyle() {
        if(dataXStyle == null) {
            XSSFFont dfont = sheet.getSheet().getWorkbook().createFont();
            dfont.setFontName("Calibri");
            dfont.setFontHeightInPoints((short) 10);
            dfont.setColor(IndexedColors.BLACK.index);
            XSSFCellStyle dstyle = sheet.getSheet().getWorkbook().createCellStyle();
            dstyle.setFont(dfont);
            dstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dstyle.setAlignment(HorizontalAlignment.CENTER);
            dataXStyle = dstyle;
        }
        return dataXStyle;
    }
    private XSSFCellStyle getDataYStyle() {
        if(dataYStyle == null) {
            XSSFFont dfont = sheet.getSheet().getWorkbook().createFont();
            dfont.setFontName("Calibri");
            dfont.setFontHeightInPoints((short) 10);
            dfont.setColor(IndexedColors.BLACK.index);
            XSSFCellStyle dstyle = sheet.getSheet().getWorkbook().createCellStyle();
            dstyle.setFont(dfont);
            dstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dstyle.setAlignment(HorizontalAlignment.CENTER);
            dataYStyle = dstyle;
        }
        return dataYStyle;
    }

}
