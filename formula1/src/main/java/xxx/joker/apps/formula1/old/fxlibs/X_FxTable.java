package xxx.joker.apps.formula1.old.fxlibs;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.util.function.Function;

public class X_FxTable {

    public static <T, V> TableColumn<T, V> createColumn(String varName) {
        return createColumn(getColumnHeader(varName), varName);
    }
    public static <T, V> TableColumn<T, V> createColumn(String header, String varName) {
        TableColumn<T, V> colTeamName = new TableColumn<>();
        colTeamName.setText(header);
        colTeamName.setCellValueFactory(new PropertyValueFactory<>(varName));
        return colTeamName;
    }
    public static <T,V> TableColumn<T, V> createColumn(String header, Function<T, V> extractor) {
        TableColumn<T, V> colTeamName = new TableColumn<>();
        colTeamName.setText(header);
        colTeamName.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue())));
        return colTeamName;
    }

    private static String getColumnHeader(String varName) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < varName.length(); i++) {
            char c = varName.charAt(i);
            if(c >= 'A' && c <= 'Z') {
                sb.append(" ");
            }
            sb.append(c);
        }
        String res = sb.toString().replace("_", " ").replaceAll(" +", " ").trim();
        return res.toUpperCase();
    }

}
