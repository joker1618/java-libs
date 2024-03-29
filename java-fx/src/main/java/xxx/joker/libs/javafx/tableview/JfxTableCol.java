package xxx.joker.libs.javafx.tableview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.util.JkStrings;

import java.util.function.Function;

public class JfxTableCol<T, V> extends TableColumn<T, V> {

    private String varName;
    private Function<T, V> extractor;
    private Function<V, String> formatter;

    public JfxTableCol() {
        getStyleClass().addAll("jfxTableCol");
    }

    public static <T,V> JfxTableCol<T,V> createCol(String header, String varName, String... styleClasses) {
        return createCol(header, varName, null, null, styleClasses);
    }
    public static <T,V> JfxTableCol<T,V> createCol(String header, Function<T, V> extractor, String... styleClasses) {
        return createCol(header, null, extractor, null,styleClasses);
    }
    public static <T,V> JfxTableCol<T,V> createCol(String header, String varName, Function<V, String> formatter, String... styleClasses) {
        return createCol(header, varName, null, formatter, styleClasses);
    }
    public static <T,V> JfxTableCol<T,V> createCol(String header, Function<T, V> extractor, Function<V, String> formatter, String... styleClasses) {
        return createCol(header, null, extractor, formatter, styleClasses);
    }
    private static <T,V> JfxTableCol<T,V> createCol(String header, String varName, Function<T, V> extractor, Function<V, String> formatter, String... styleClasses) {
        JfxTableCol<T,V> col = new JfxTableCol<>();

        for (String str : styleClasses) {
            col.getStyleClass().addAll(JkStrings.splitList(str, " ", true));
        }

        if(StringUtils.isNotBlank(header)) {
            col.setText(header);
        }

        if(StringUtils.isNotBlank(varName)) {
            col.setCellValueFactory(new PropertyValueFactory<>(varName));
        } else {
            col.setExtractor(extractor);
        }

        if(formatter != null) {
            col.setFormatter(formatter);
        }

        return col;
    }

    public String formatCellData(int i) {
        V cellData = super.getCellData(i);
        return formatter == null ? cellData.toString() : formatter.apply(cellData);
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public Function<T, V> getExtractor() {
        return extractor;
    }

    public void setExtractor(Function<T, V> extractor) {
        this.extractor = extractor;
        if(extractor == null) {
            setCellValueFactory(param -> new SimpleObjectProperty<>());
        } else {
            setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue())));
        }
    }

    public Function<V, String> getFormatter() {
        return formatter;
    }

    public void setFormatter(Function<V, String> formatter) {
        this.formatter = formatter;
        setCellFactory(param -> new TableCell<T, V>() {
            @Override
            protected void updateItem (V item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else if(formatter != null) {
                    setText(formatter.apply(item));
                } else {
                    setText(item.toString());
                }
            }
        });
    }

}
