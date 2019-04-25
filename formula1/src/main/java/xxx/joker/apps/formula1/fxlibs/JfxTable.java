package xxx.joker.apps.formula1.fxlibs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

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
        TableColumn<T, V> colTeamName = new TableColumn<>();
        colTeamName.setText(header);
        colTeamName.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue())));
        return colTeamName;
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


}
