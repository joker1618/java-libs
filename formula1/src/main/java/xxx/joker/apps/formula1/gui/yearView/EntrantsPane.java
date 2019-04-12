package xxx.joker.apps.formula1.gui.yearView;

import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.entities.F1Entrant;

import java.util.Collection;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class EntrantsPane extends BorderPane {

    private TableView<F1Entrant> table;

    public EntrantsPane(F1Season season) {
        table = createTableEntrants();
        table.getItems().setAll(season.getEntrants());
        setCenter(table);
    }

    private TableView<F1Entrant> createTableEntrants() {
        TableView<F1Entrant> t = new TableView<>();
        t.getColumns().add(X_FxTable.createColumn("TEAM NAME", e -> e.getTeam().getTeamName()));
        t.getColumns().add(X_FxTable.createColumn("TEAM NATION", e -> e.getTeam().getNation()));
        t.getColumns().add(X_FxTable.createColumn("engine"));
        t.getColumns().add(X_FxTable.createColumn("carNo"));
        t.getColumns().add(X_FxTable.createColumn("DRIVER NAME", e -> e.getDriver().getFullName()));
        t.getColumns().add(X_FxTable.createColumn("DRIVER INFO",
                e -> strf("{}, {}, {}", e.getDriver().getBirthDate(), e.getDriver().getBirthCity(), e.getDriver().getNation())
        ));
        return t;
    }

    public TableView<F1Entrant> getTable() {
        return table;
    }
}
