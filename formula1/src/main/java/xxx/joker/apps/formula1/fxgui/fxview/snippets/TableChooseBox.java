package xxx.joker.apps.formula1.fxgui.fxview.snippets;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xxx.joker.apps.formula1.fxlibs.JfxTable;

import java.util.Collection;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class TableChooseBox extends BorderPane {

    private Label lblCaption;
    private TableView<?> tableView;

    public TableChooseBox(TableView<?> tableView) {
        this("", tableView);
    }
    public TableChooseBox(String caption, TableView<?> tableView) {
        this.tableView = tableView;

        tableView.getStyleClass().add("tableChoose");
        resizeTable();
        tableView.getItems().addListener((ListChangeListener)c -> resizeTable());

        HBox tbox = new HBox(tableView);
        tbox.getStyleClass().add("tableChooseBox");

        lblCaption = new Label(caption);
        HBox boxCaption = new HBox(lblCaption);
        boxCaption.getStyleClass().addAll("tableChooseCaption");

        setTop(boxCaption);
        setCenter(tbox);

        getStylesheets().add(getClass().getResource("/css/tableView.css").toExternalForm());
    }

    public TableView<?> getTableView() {
        return tableView;
    }

    public void updateBox(String caption, Collection items) {
        lblCaption.setText(caption);
        tableView.getItems().setAll(items);
    }

    private void resizeTable() {
        JfxTable.autoResizeColumns(tableView, true);
        tableView.setMaxHeight(38d + tableView.getItems().size() * 35d);
    }
}
