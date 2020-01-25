package xxx.joker.libs.javafx.tableview;

import javafx.scene.control.TableView;
import xxx.joker.libs.core.util.JkStrings;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static xxx.joker.libs.core.util.JkStrings.strf;

public class JfxTable<T> extends TableView<T> {

    private static final String CSS_FILEPATH = "/css/tableview/JfxTable.css";
    private static final double EXTRA_COL_WIDTH = 30d;

    private int headerHeight;
    private int rowHeight;
    private int extraTableWidth;
    private int[] colWidths;
    private int maxElemVisible;

    public JfxTable(String... styleClasses) {
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        getStyleClass().addAll("jfxTable");
        getStyleClass().addAll(JkStrings.splitFlat(styleClasses));
        getStylesheets().add(getClass().getResource(CSS_FILEPATH).toExternalForm());
    }

    public void addColumn(JfxTableCol... cols) {
        addColumn(getColumns().size(), cols);
    }
    public void addColumn(int index, JfxTableCol... cols) {
        AtomicInteger pos = new AtomicInteger(index);
        Arrays.stream(cols).forEach(c -> {
            getColumns().add(pos.getAndIncrement(), c);
        });

        if(!getColumns().isEmpty()){
            for(int i = 0; i < getColumns().size(); i++) {
                JfxTableCol<T, ?> col = getJfxCol(i);
                col.getStyleClass().removeIf(s -> s.startsWith("col") || s.equals("jfxTableCol"));
                col.getStyleClass().addAll("jfxTableCol", "col", "col" + i);
                col.getStyleClass().add(i % 2 == 0 ? "col-odd" : "col-even");
                col.getStyleClass().add(i == 0 ? "col-first" : i == getColumns().size() - 1 ? "col-last" : "col-middle");
            }
        }
    }

    public void setRowHeight(int headerHeight, int rowHeight) {
        this.headerHeight = headerHeight;
        this.rowHeight = rowHeight;
        String newStyle = strf("-headerHeight: {}; -rowHeight: {}; {}", headerHeight, rowHeight, getStyle());
        setStyle(newStyle);
    }

    public void setMaxElemVisible(int numElemVisible) {
        this.maxElemVisible = numElemVisible;
    }

    public void refreshTableSize() {
        refreshWidth();
        refreshHeight();
    }
    public void refreshWidth() {
        if(colWidths != null) {
            int iw = 0;
            int sum = 0;
            for (int col = 0; col < getColumns().size(); col++) {
                getColumns().get(col).setMinWidth(colWidths[iw]);
                getColumns().get(col).setPrefWidth(colWidths[iw]);
                sum += colWidths[iw];
                iw = (iw + 1) % colWidths.length;
            }
            int tableWidth = extraTableWidth + sum;
            setMinWidth(tableWidth);
            setPrefWidth(tableWidth);
        }
    }
    public void refreshHeight() {
        int num = Math.min(getItems().size(), maxElemVisible);
        num = Math.max(2, num);
        int h = 5 + headerHeight + rowHeight * num;
        setMinHeight(h);
        setPrefHeight(h);
    }

    public void setExtraTableWidth(int extraTableWidth) {
        this.extraTableWidth = extraTableWidth;
    }
    public void setColWidth(int colNum, int colWidth) {
        if(colWidths == null) {
            colWidths = new int[getColumns().size()];
        }
        colWidths[colNum] = colWidth;
    }
    public void setWidths(int extraTableWidth, int... colWidths) {
        setExtraTableWidth(extraTableWidth);
        this.colWidths = colWidths;
    }
    public void setWidthsGroups(int extraTableWidth, int widthColFirst, int widthColMiddle, int widthColLast) {
        setExtraTableWidth(extraTableWidth);
        this.colWidths = new int[getColumns().size()];
        for (int col = 0; col < getColumns().size(); col++) {
            int w = col == 0 ? widthColFirst : col < getColumns().size() - 1 ? widthColMiddle : widthColLast;
            colWidths[col] = w;
        }
    }

//    public void resizeWidth() {
//        double tablePrefWidth = 2d + 20d;
//
//        //Set the right policy
//        for(int idx = 0; idx < getColumns().size(); idx++) {
//            JfxTableCol<T, ?> col = getJfxCol(idx);
//
//            if(col.isVisible()) {
//                double wcol;
//                if (col.getStyleClass().contains(CSS_CLASS_FIXED_WIDTH)) {
//                    wcol = col.getPrefWidth();
//                } else {
//                    //Minimal width = columnheader
//                    Text t = new Text(col.getText());
//                    double max = t.getLayoutBounds().getWidth();
//                    for (int i = 1; i < getItems().size(); i++) {
//                        //cell must not be empty
//                        if (col.getCellData(i) != null) {
//                            t = new Text(col.formatCellData(i));
//                            double calcwidth = t.getLayoutBounds().getWidth();
//                            if (calcwidth > max) {
//                                max = calcwidth;
//                            }
//                        }
//                    }
//                    wcol = max + EXTRA_COL_WIDTH;
//                }
//                col.setPrefWidth(wcol);
//                // add extra space
//                tablePrefWidth += wcol;
//            }
//        }
//
//        setPrefWidth(tablePrefWidth);
//    }

    private JfxTableCol<T,?> getJfxCol(int index) {
        return (JfxTableCol<T,?>) getColumns().get(index);
    }
}