package xxx.joker.apps.formula1.gui.yearView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.F1ResourceManager;
import xxx.joker.apps.formula1.model.F1Resources;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.format.JkOutput;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CheckPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(CheckPane.class);

    private TableView<F1Team> tableTeams;
    private TableView<F1Driver> tableDrivers;
    private TableView<F1Circuit> tableCircuits;

    public CheckPane() {
        F1Model model = F1ModelImpl.getInstance();

        tableTeams = createTableTeams();
        tableTeams.getItems().setAll(model.getTeams());

        tableDrivers = createTableDrivers();
        tableDrivers.getItems().setAll(model.getDrivers());

        tableCircuits = createTableCircuits();
        tableCircuits.getItems().setAll(model.getCircuits());

        HBox hbox = new HBox(tableTeams, tableDrivers, tableCircuits);
        hbox.setSpacing(20);

        setCenter(hbox);
    }


    private TableView<F1Team> createTableTeams() {
        TableView<F1Team> table = new TableView<>();
        table.getColumns().add(X_FxTable.createColumn("entityID"));
        table.getColumns().add(X_FxTable.createColumn("teamName"));
        table.getColumns().add(X_FxTable.createColumn("nation"));
        table.setMinWidth(400);
        return table;
    }

    private TableView<F1Driver> createTableDrivers() {
        TableView<F1Driver> table = new TableView<>();
        table.getColumns().add(X_FxTable.createColumn("entityID"));
        table.getColumns().add(X_FxTable.createColumn("fullName"));
        table.getColumns().add(X_FxTable.createColumn("nation"));
        table.getColumns().add(X_FxTable.createColumn("city"));
        table.getColumns().add(X_FxTable.createColumn("birthDate"));
        table.setMinWidth(750);
        return table;
    }

    private TableView<F1Circuit> createTableCircuits() {
        TableView<F1Circuit> table = new TableView<>();
        table.getColumns().add(X_FxTable.createColumn("city"));
        table.getColumns().add(X_FxTable.createColumn("nation"));
        table.getColumns().add(X_FxTable.createColumn("entityID"));
        table.setMinWidth(400);
        return table;
    }

}
