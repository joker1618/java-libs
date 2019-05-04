package xxx.joker.apps.formula1.fxlibs;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JfxTable {

    public static <T, V> TableColumn<T, V> createColumn(String header, String varName) {
        return createColumn(header, varName, null);
    }
    public static <T, V> TableColumn<T, V> createColumn(String header, String varName, Function<V, String> strFunc) {
        TableColumn<T, V> col = new TableColumn<>();
        col.setText(header);
        col.setCellValueFactory(new PropertyValueFactory<>(varName));
        if(strFunc != null) {
            setCellFactory(col, strFunc);
        }
        return col;
    }

    public static <T,V> TableColumn<T, V> createColumn(String header, Function<T, V> extractor) {
        return createColumn(header, extractor, null);
    }
    public static <T,V> TableColumn<T, V> createColumn(String header, Function<T, V> extractor, Function<V, String> strFunc) {
        TableColumn<T, V> col = new TableColumn<>();
        col.setText(header);
        col.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue())));
        if(strFunc != null) {
            setCellFactory(col, strFunc);
        }
        return col;
    }

    public static <T, V> void setCellFactory(TableColumn<T, V> col, Function<V, String> strFunc) {
        col.setCellFactory(param -> new TableCell<T, V>() {
            @Override
            protected void updateItem (V item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(strFunc.apply(item));
                }
            }
        });
    }



    public static void autoResizeColumns(TableView<?> table, boolean reserveScrollSpace) {
        double tablePrefWidth = 2d + (reserveScrollSpace ? 17d : 0d);

        //Set the right policy
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        for (TableColumn<?, ?> column : table.getColumns()) {
            //Minimal width = columnheader
            Text t = new Text( column.getText() );
            double max = t.getLayoutBounds().getWidth();
            for ( int i = 0; i < table.getItems().size(); i++ ) {
                //cell must not be empty
                if ( column.getCellData( i ) != null ) {
                    t = new Text( column.getCellData( i ).toString() );
                    double calcwidth = t.getLayoutBounds().getWidth();
                    if ( calcwidth > max ) {
                        max = calcwidth;
                    }
                }
            }
            // add extra space
            double wcol = max + 30d;
            tablePrefWidth += wcol;
            column.setPrefWidth(wcol);
        }

        table.setMinWidth(tablePrefWidth);
    }

    /**
     * @param widthsStr "0.2:2 0.1 0.25:2" (dove :X indica il numero di colonne, 1 default)
     */
    public static void setColPercWidth(TableView<?> tableView, String widthsStr, boolean reserveScrollSpace) {
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        DoubleBinding wprop = tableView.widthProperty().subtract(2d + (reserveScrollSpace ? 17d : 0d));
        List<Double> percs = parseWidths(widthsStr);
        for(int i = 0; i < percs.size(); i++) {
            tableView.getColumns().get(i).prefWidthProperty().bind(wprop.multiply(percs.get(i)));
        }
    }

    /**
     * @param widthsStr "12:3 45 7:8" (dove :X indica il numero di colonne, 1 default)
     */
    public static void setFixedWidth(TableView<?> tableView, String widthsStr, boolean reserveScrollSpace) {
//        tableView.getColumns().forEach(col -> col.setResizable(false));
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        List<Double> widths = parseWidths(widthsStr);
        for(int i = 0; i < widths.size(); i++) {
            tableView.getColumns().get(i).setPrefWidth(widths.get(i));
        }

        double wsum = JkStreams.sumDouble(widths);
        double tableWidth = wsum + 2 + (reserveScrollSpace ? 17 : 0);
        tableView.setPrefWidth(tableWidth);
    }
    private static List<Double> parseWidths(String widthsStr) {
        List<Double> widths = new ArrayList<>();
        List<String> elems = JkStrings.splitList(widthsStr, " ", true);
        for (String elem : elems) {
            if(elem.contains(":")) {
                String[] split = JkStrings.splitArr(elem, ":");
                int numCols = Integer.parseInt(split[1]);
                double w = Double.parseDouble(split[0]);
                for(int i = 0; i < numCols; i++) {
                    widths.add(w);
                }
            } else {
                widths.add(Double.parseDouble(elem));
            }
        }
        return widths;
    }

}
