package xxx.joker.libs.excel;

import org.apache.commons.lang3.tuple.Pair;
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
import xxx.joker.libs.core.objects.Area;
import xxx.joker.libs.core.objects.Pos;
import xxx.joker.libs.core.utils.JkStreams;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JkExcelChartBuilder {

    private final JkSheetXSSF sheet;

    private Serie serieX;
    private List<Serie> seriesY = new ArrayList<>();

    private Pos dataLocation;
    private boolean dataOrientationVertical = true;

    private XSSFCellStyle dataHeaderStyle;
    private XSSFCellStyle dataValuesStyle;

    protected JkExcelChartBuilder(JkSheetXSSF sheet) {
        this.sheet = sheet;
    }

    public void reset() {
        serieX = null;
        seriesY.clear();
        dataLocation = null;
        dataOrientationVertical = true;
        dataHeaderStyle = null;
        dataValuesStyle = null;
    }

    public void setSerieX(String serieName, List<? extends Number> dataList) {
        Double[] data = JkStreams.map(dataList, Number::doubleValue).toArray(new Double[0]);
        serieX = new Serie(serieName, data);
    }
    public void setSerieX(String serieName, int[] data) {
        serieX = new Serie(serieName, data);
    }
    public void setSerieX(String serieName, double[] data) {
        serieX = new Serie(serieName, data);
    }

    public void addSerieY(String serieName, List<? extends Number> dataList) {
        Double[] data = JkStreams.map(dataList, Number::doubleValue).toArray(new Double[0]);
        serieX = new Serie(serieName, data);
    }
    public void addSerieY(String serieName, int[] data) {
        seriesY.add(new Serie(serieName, data));
    }
    public void addSerieY(String serieName, double[] data) {
        seriesY.add(new Serie(serieName, data));
    }

    public void setDataLocation(int rowNum, int colNum) {
        dataLocation = new Pos(rowNum, colNum);
    }
    public void setDataOrientationVertical(boolean dataOrientationVertical) {
        this.dataOrientationVertical = dataOrientationVertical;
    }

    public void setDataHeaderStyle(XSSFCellStyle dataHeaderStyle) {
        this.dataHeaderStyle = dataHeaderStyle;
    }
    public void setDataValuesStyle(XSSFCellStyle dataValuesStyle) {
        this.dataValuesStyle = dataValuesStyle;
    }

    public void drawChart(String chartTitle, Area chartArea) {
        if(serieX == null) {
            throw new JkRuntimeException("X axis not set");
        }
        if(seriesY.isEmpty()) {
            throw new JkRuntimeException("No series data found");
        }

        Pos dataPos = dataLocation == null ? new Pos(0, chartArea.getEndX()+1) : dataLocation;

        XSSFCellStyle styleHeader = getHeaderStyle(dataOrientationVertical);
        XSSFCellStyle styleData = getDataStyle();

        // X axis data
        Area areaX = computeOutputArea(dataPos, 0, serieX.getData().size(), dataOrientationVertical);
        int pos = -1;
        for(int r = areaX.getY(); r < areaX.getEndY(); r++) {
            for(int c = areaX.getX(); c < areaX.getEndX(); c++, pos++) {
                if(pos == -1) {
                    sheet.setValue(r, c, serieX.getName(), styleHeader);
                } else {
                    sheet.setValue(r, c, serieX.getData().get(pos), styleData);
                }
            }
        }

        // Series data
        List<Area> areasY = new ArrayList<>();
        for(int i = 0; i < seriesY.size(); i++) {
            Area areaY = computeOutputArea(dataPos, i+1, serieX.getData().size(), dataOrientationVertical);
            areasY.add(areaY);
            pos = -1;
            for(int r = areaY.getY(); r < areaY.getEndY(); r++) {
                for(int c = areaY.getX(); c < areaY.getEndX(); c++, pos++) {
                    if(pos == -1) {
                        sheet.setValue(r, c, seriesY.get(i).getName(), styleHeader);
                    } else {
                        sheet.setValue(r, c, seriesY.get(i).getData().get(pos), styleData);
                    }
                }
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
            Area areaY = areasY.get(i);
            LineChartSeries chartSerie = chartData.addSeries(xds, DataSources.fromNumericCellRange(sheet.getSheet(), convertArea(areaY)));
            CellReference cellRef = sheet.createCellReference(areaY.getY(), areaY.getX());
            chartSerie.setTitle(cellRef);
        }

        /* Define chart AXIS */
        ChartAxis bottomAxis = lineChart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
        ValueAxis leftAxis = lineChart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);

        /* Plot the chart with the inputs from data and chart axis */
        lineChart.plot(chartData, new ChartAxis[] { bottomAxis, leftAxis });
    }

    private Area computeOutputArea(Pos startPos, int serieOffset, int dataSize, boolean vertical) {
        Area area = new Area();
        area.setX(startPos.getColNum() + (vertical ? serieOffset : 0));
        area.setY(startPos.getRowNum() + (vertical ? 0 : serieOffset));
        area.setWidth(1 + (vertical ? 0 : dataSize));
        area.setHeight(1 + (vertical ? dataSize : 0));
        return area;
    }

    private CellRangeAddress convertArea(Area area) {
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
    private XSSFCellStyle getDataStyle() {
        if(dataValuesStyle == null) {
            XSSFFont dfont = sheet.getSheet().getWorkbook().createFont();
            dfont.setFontName("Calibri");
            dfont.setFontHeightInPoints((short) 10);
            dfont.setColor(IndexedColors.BLACK.index);
            XSSFCellStyle dstyle = sheet.getSheet().getWorkbook().createCellStyle();
            dstyle.setFont(dfont);
            dstyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dstyle.setAlignment(HorizontalAlignment.CENTER);
            dataValuesStyle = dstyle;
        }
        return dataValuesStyle;
    }


    private static class Serie {
        private String name;
        private List<Double> data;

        public Serie(String name, int[] data) {
            this.name = name;
            this.data = new ArrayList<>();
            Arrays.stream(data).forEach(d -> this.data.add((double)d));
        }
        public Serie(String name, double[] data) {
            this.name = name;
            this.data = new ArrayList<>();
            Arrays.stream(data).forEach(d -> this.data.add(d));
        }
        public Serie(String name, Double[] data) {
            this.name = name;
            this.data = new ArrayList<>();
            this.data.addAll(Arrays.asList(data));
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<Double> getData() {
            return data;
        }
        public void setData(List<Double> data) {
            this.data = data;
        }
    }
}
