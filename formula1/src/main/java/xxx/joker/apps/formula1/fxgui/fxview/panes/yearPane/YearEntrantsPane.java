package xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Team;

public class YearEntrantsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearEntrantsPane.class);

    private TableView<F1Entrant> tableEntrants;

    public YearEntrantsPane() {
        getStyleClass().add("bgGrey");

        tableEntrants = createTableEntrants();
//        setCenter(tableEntrants);
        setCenter(new HBox(tableEntrants));

        guiModel.addChangeActionYear(year -> tableEntrants.getItems().setAll(model.getEntrants(year)));
    }

    private TableView<F1Entrant> createTableEntrants() {
        TableColumn<F1Entrant, F1Team> colTeam = JfxTable.createColumn("TEAM", "team", F1Team::getTeamName);
        TableColumn<F1Entrant, Integer> colCarNo = JfxTable.createColumn("CAR", "carNo");
        TableColumn<F1Entrant, F1Driver> colDriver = JfxTable.createColumn("DRIVER", "driver", F1Driver::getFullName);

        TableView<F1Entrant> tv = new TableView<>();
        tv.getColumns().addAll(colTeam, colCarNo, colDriver);

        JfxTable.setFixedWidth(tv, "200 100 200", true);

        return tv;
    }
}