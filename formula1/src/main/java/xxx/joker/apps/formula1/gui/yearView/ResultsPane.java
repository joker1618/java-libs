package xxx.joker.apps.formula1.gui.yearView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.beans.F1SeasonResult;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.apps.formula1.model.managers.ResourceManager;

import java.util.Comparator;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class ResultsPane extends BorderPane {

    private TableView<F1SeasonResult> table;

    public ResultsPane(F1Season season) {
        table = createTableResults(season.getGpList());
        table.getItems().setAll(season.getResults());
        setCenter(table);
    }

    private TableView<F1SeasonResult> createTableResults(List<F1GranPrix> gpList) {
        TableView<F1SeasonResult> t = new TableView<>();
        t.getColumns().add(X_FxTable.createColumn("DRIVER", e -> e.getDriver().getFullName()));

        gpList.forEach(gp -> {
            TableColumn<F1SeasonResult, F1Race> col = new TableColumn<>();
            Image img = ResourceManager.getFlagIconImage(gp.getCircuit().getNation());
            col.setGraphic(new ImageView(img));
            col.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPoints().get(gp)));
            col.setCellFactory(param -> new TableCell<F1SeasonResult, F1Race>() {
                @Override
                protected void updateItem (F1Race item, boolean empty) {
                super.updateItem (item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.isRetired() ? "RET" : ""+item.getPos());
                }
                }
            });
            col.setComparator(Comparator.comparing(F1Race::getPos));
            t.getColumns().add(col);
        });

        t.getColumns().add(X_FxTable.createColumn("TOT.", F1SeasonResult::getTotPoints));

        return t;
    }

    public TableView<F1SeasonResult> getTable() {
        return table;
    }
}
