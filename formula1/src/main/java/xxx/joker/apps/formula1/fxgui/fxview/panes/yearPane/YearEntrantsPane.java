package xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxgui.fxview.snippets.TableChooseBox;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Team;

public class YearEntrantsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearEntrantsPane.class);

    public YearEntrantsPane() {
        getStyleClass().add("entrantsPane");

        setLeft(createTableEntrants());

    }

    private TableChooseBox createTableEntrants() {
        TableColumn<F1Entrant, String> colDriver = JfxTable.createColumn("DRIVER", e -> e.getDriver().getFullName());
        TableColumn<F1Entrant, Integer> colCarNo = JfxTable.createColumn("CAR", "carNo");
        colCarNo.getStyleClass().add("centered");
        TableColumn<F1Entrant, String> colTeam = JfxTable.createColumn("TEAM", e -> e.getTeam().getTeamName());

        TableView<F1Entrant> tv = new TableView<>();
        tv.getColumns().addAll(colDriver, colCarNo, colTeam);
        TableChooseBox tbox = new TableChooseBox(tv);

        guiModel.addChangeActionYear(year -> tbox.updateBox("Entrants " + year, model.getEntrants(year)));
//        guiModel.addChangeActionYear(year -> {
//            tv.getItems().setAll(model.getEntrants(year));
//            setLeft(new TableChooseBox("Entrants " + year, tv));
//        });

        return tbox;
    }
}
