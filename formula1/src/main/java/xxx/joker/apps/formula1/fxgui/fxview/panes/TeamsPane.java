package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Team;

public class TeamsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(TeamsPane.class);

    private TableView<F1Team> tableTeams;

    public TeamsPane() {
        getStyleClass().add("bgCyan");

        tableTeams = createTableTeams();
        tableTeams.getItems().addAll(model.getTeams());

        BorderPane bp = new BorderPane();
        bp.getStyleClass().add("pad10");
        bp.getStyleClass().add("bgBlack");
        bp.setCenter(tableTeams);

        setLeft(bp);
    }

    private TableView<F1Team> createTableTeams() {
        TableColumn<F1Team, String> colName = JfxTable.createColumn("TEAM", "teamName");
        TableColumn<F1Team, String> colNation = JfxTable.createColumn("NATION", "nation");
        TableView<F1Team> tv = new TableView<>();
        tv.getColumns().addAll(colName, colNation);
        JfxTable.setFixedWidth(tv, "250 150", true);
        return tv;
    }
}
