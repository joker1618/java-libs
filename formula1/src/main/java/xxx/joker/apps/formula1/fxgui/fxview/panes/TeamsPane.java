package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Team;

public class TeamsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(TeamsPane.class);


    public TeamsPane() {
        getStyleClass().add("teamsPane");

        setLeft(createTableTeams());
    }

    private BorderPane createTableTeams() {
        TableColumn<F1Team, String> colName = JfxTable.createColumn("TEAM", "teamName");
        TableColumn<F1Team, String> colNation = JfxTable.createColumn("NATION", "nation");
        TableView<F1Team> tableTeams = new TableView<>();
        tableTeams.getStyleClass().addAll("tableChoose");
        tableTeams.getColumns().addAll(colName, colNation);
        tableTeams.getItems().addAll(model.getTeams());

        Label caption = new Label("TEAMS");
        HBox boxCaption = new HBox(caption);
        boxCaption.getStyleClass().addAll("tableChooseCaption");

        BorderPane bp = new BorderPane();
//        bp.getStyleClass().addAll("pad10");
//        bp.getStyleClass().add("bgBlue");
        bp.setTop(boxCaption);
//        bp.setCenter(tableCircuits);

        JfxTable.autoResizeColumns(tableTeams, true);
        HBox tbox = new HBox(tableTeams);
        tbox.getStyleClass().add("tableChooseBox");
        bp.setCenter(tbox);

        return bp;
    }
}
