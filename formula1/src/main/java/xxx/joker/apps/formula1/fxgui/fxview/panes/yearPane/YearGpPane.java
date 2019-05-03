package xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Qualify;
import xxx.joker.apps.formula1.model.entities.F1Team;

public class YearGpPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearGpPane.class);

    private TableView<F1Entrant> tableEntrants;

    public YearGpPane() {
        getStyleClass().add("bgGrey");

        TabPane tp = new TabPane();
        Tab tab1 = new Tab("fe");
        tp.getTabs().setAll(tab1);
        setCenter(tp);

//        tableEntrants = createTableEntrants();
//        setCenter(tableEntrants);
//
        guiModel.addChangeActionGranPrix(gp -> tab1.setContent(new HBox(new Label(gp.getPrimaryKey()))));
    }

//    private TableView<F1Qualify> createTableQualify() {
//        TableColumn<F1Qualify, Integer> colPos = JfxTable.createColumn("TEAM", "team", F1Team::getTeamName);
//        TableColumn<F1Entrant, Integer> colCarNo = JfxTable.createColumn("CAR", "carNo");
//        TableColumn<F1Entrant, F1Driver> colDriver = JfxTable.createColumn("DRIVER", "driver", F1Driver::getFullName);
//
//        TableView<F1Entrant> tv = new TableView<>();
//        tv.getColumns().addAll(colTeam, colCarNo, colDriver);
//        return tv;
//    }
}
