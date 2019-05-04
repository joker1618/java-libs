package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxgui.fxview.snippets.TableChooseBox;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Team;
import xxx.joker.libs.core.datetime.JkDates;

import java.time.LocalDate;

public class DriversPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(DriversPane.class);

    public DriversPane() {
        getStyleClass().add("driversPane");

        setLeft(createTableDrivers());
    }

    private BorderPane createTableDrivers() {
        TableColumn<F1Driver, String> colName = JfxTable.createColumn("NAME", "fullName");
        TableColumn<F1Driver, String> colNation = JfxTable.createColumn("NATION", "nation");
        TableColumn<F1Driver, LocalDate> colBorn = JfxTable.createColumn("BORN", F1Driver::getBirthDay, bd -> JkDates.format(bd, "dd/MM/yyyy"));

        TableView<F1Driver> tableDrivers = new TableView<>();
        tableDrivers.getColumns().addAll(colName, colNation, colBorn);
        tableDrivers.getItems().addAll(model.getDrivers());

        return new TableChooseBox("DRIVERS", tableDrivers);
    }
}
