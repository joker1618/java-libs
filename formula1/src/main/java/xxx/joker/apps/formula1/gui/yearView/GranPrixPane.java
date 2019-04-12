package xxx.joker.apps.formula1.gui.yearView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Qualify;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.libs.core.datetime.JkDuration;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class GranPrixPane extends BorderPane {

    private SimpleObjectProperty<F1GranPrix> gp;
    private TableView<F1Qualify> tableQualify;
    private TableView<F1Race> tableRace;

    public GranPrixPane(int numQualifyRounds) {
        gp = new SimpleObjectProperty<>();
        gp.addListener((obs,o,n) -> updatePane());

        tableQualify = createTableQualify(numQualifyRounds);
        tableRace = createTableRace();

        Pane spacer = new Pane();
        spacer.setMinWidth(50);
        HBox hbox = new HBox(tableQualify, spacer, tableRace);

        setCenter(hbox);
    }

    private void updatePane() {
        tableQualify.getItems().setAll(gp.get().getQualifies());
        tableRace.getItems().setAll(gp.get().getRaces());
    }

    public void setGranPrix(F1GranPrix gp) {
        this.gp.set(gp);
    }

    private TableView<F1Qualify> createTableQualify(int numRounds) {
        TableView<F1Qualify> table = new TableView<>();
        table.getColumns().add(X_FxTable.createColumn("#", F1Qualify::getPos));
        table.getColumns().add(X_FxTable.createColumn("DRIVER", q -> q.getEntrant().getDriver().getFullName()));
        for(int i = 0; i < numRounds; i++) {
            int num = i;
            table.getColumns().add(X_FxTable.createColumn("Q"+(num+1),
                    q -> {
                        JkDuration qtime = q.getTimes().get(num);
                        return qtime == null ? "" : qtime.toStringElapsed();
                    }
            ));
        }
        table.getColumns().add(X_FxTable.createColumn("finalGrid"));
        return table;
    }

    private TableView<F1Race> createTableRace() {
        TableView<F1Race> table = new TableView<>();
        table.getColumns().add(X_FxTable.createColumn("#", F1Race::getPos));
        table.getColumns().add(X_FxTable.createColumn("DRIVER", q -> q.getEntrant().getDriver().getFullName()));
        table.getColumns().add(X_FxTable.createColumn("laps"));
        table.getColumns().add(X_FxTable.createColumn("TIME", r -> {
            if(r.isRetired()) {
                return "RET";
            }
            if(r.getTime() != null) {
                return r.getTime().toStringElapsed();
            }
            int winnerLaps = gp.get().getRaces().get(0).getLaps();
            int diffLaps = winnerLaps - r.getLaps();
            return strf("+{} lap{}", diffLaps, diffLaps > 1 ? "s" : "");
        }));
        table.getColumns().add(X_FxTable.createColumn("points"));
        return table;
    }
}
