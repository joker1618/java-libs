package xxx.joker.apps.formula1.gui.yearView;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.F1ResourceManager;
import xxx.joker.apps.formula1.model.F1Resources;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.entities.*;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.format.JkOutput;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class GranPrixPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(GranPrixPane.class);

    private final F1Season season;
    private SimpleObjectProperty<F1GranPrix> gp;
    private TableView<F1Qualify> tableQualify;
    private TableView<F1Race> tableRace;
    private F1Resources resources;

    public GranPrixPane(F1Season season) {
        this.season = season;
        this.resources = F1ResourceManager.getInstance();

        gp = new SimpleObjectProperty<>();
        gp.addListener((obs,o,n) -> {if(o != n) updatePane();});

        tableQualify = createTableQualify(season.numberOfQualifyRounds());
        tableRace = createTableRace();
        BorderPane detailsPane = createDetailsPane();

        // todo change
        Pane spacer1 = new Pane();
        spacer1.setMinWidth(30);
        Pane spacer2 = new Pane();
        spacer2.setMinWidth(30);
        HBox hbox = new HBox(tableQualify, spacer1, tableRace, spacer2, detailsPane);

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
                        if(q.getTimes().size() <= num)    return "";
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

    private BorderPane createDetailsPane() {
        BorderPane bpane = new BorderPane();

        // TOP
        Label caption = new Label();
//        caption.textProperty().bind(Bindings.createStringBinding(() -> strf("GRAN PRIX {}/{}", gp.get().getNum(), season.getGpList().size()), gp));
        bpane.setTop(caption);

        // CENTER
        VBox vBox = new VBox();
        bpane.setCenter(vBox);

        GridPane gridPane = new GridPane();
        vBox.getChildren().add(gridPane);

        int row = 0;
        Label lblDate = new Label();
//        lblDate.textProperty().bind(Bindings.createStringBinding(() -> JkDates.format(gp.get().getDate(), "dd/MM/yyyy"), gp));
        gridPane.add(new Label("DATE:"), 0, row);
        gridPane.add(lblDate, 1, row);

        row++;
        Label lblNation = new Label();
//        lblNation.textProperty().bind(Bindings.createStringBinding(() -> gp.get().getCircuit().getNation(), gp));
        gridPane.add(new Label("NATION:"), 0, row);
        gridPane.add(lblNation, 1, row);

        row++;
        Label lblCity = new Label();
//        lblCity.textProperty().bind(Bindings.createStringBinding(() -> gp.get().getCircuit().getCity(), gp));
        gridPane.add(new Label("CITY:"), 0, row);
        gridPane.add(lblCity, 1, row);

        row++;
        Label lblLapLength = new Label();
//        lblLapLength.textProperty().bind(Bindings.createStringBinding(() -> JkOutput.getNumberFmtEN(3).format(gp.get().getLapLength())+" km", gp));
        gridPane.add(new Label("LAP LENGTH:"), 0, row);
        gridPane.add(lblLapLength, 1, row);

        row++;
        Label lblNumLaps = new Label();
//        lblNumLaps.textProperty().bind(Bindings.createStringBinding(() -> gp.get().getNumLapsRace()+"", gp));
        gridPane.add(new Label("NUM LAPS:"), 0, row);
        gridPane.add(lblNumLaps, 1, row);

        row++;
        Label lblRaceLength = new Label();
//        lblRaceLength.textProperty().bind(Bindings.createStringBinding(() -> JkOutput.getNumberFmtEN(3).format(gp.get().getLapLength()*gp.get().getNumLapsRace())+" km", gp));
        gridPane.add(new Label("RACE LENGTH:"), 0, row);
        gridPane.add(lblRaceLength, 1, row);

        row++;
        Label lblFast = new Label();
//        lblFast.textProperty().bind(Bindings.createStringBinding(() -> gp.get().getFastLap().toLine(), gp));
        gridPane.add(new Label("FAST LAP:"), 0, row);
        gridPane.add(lblFast, 1, row);

        // BOTTOM
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
//        imageView.setFitWidth(400d);
//        imageView.setFitHeight(400d);
//        imageView.imageProperty().bind(Bindings.createObjectBinding(() -> resources.getTrackMap(gp.get()).getImage(), gp));
        HBox hBox = new HBox(imageView);
        hBox.setStyle("-fx-border-color: red");
        vBox.getChildren().add(hBox);

        imageView.imageProperty().addListener((obs,o,n) -> {
            if(o!=n) {
//                LOG.debug("GP {}: {}x{}  -  {}x{}", gp.get().getCircuit().getNation(), n.getWidth(), n.getHeight(), n.getRequestedWidth(), n.getRequestedHeight());
//                LOG.debug("GP {}: {}x{}  -  {}x{}", gp.get().getCircuit().getNation(), imageView.getFitWidth(), imageView.getFitHeight(), hBox.getWidth(), hBox.getHeight());
            }
        });

        gp.addListener((obs,o,n) -> {
            if(o != n) {
                caption.setText(strf("GRAN PRIX {}/{}", n.getNum(), season.getGpList().size()));
                lblDate.setText(JkDates.format(n.getDate(), "dd/MM/yyyy"));
                lblNation.setText(n.getCircuit().getNation());
                lblCity.setText(n.getCircuit().getCity());
                lblLapLength.setText(JkOutput.getNumberFmtEN(3).format(n.getLapLength())+" km");
                lblNumLaps.setText(n.getNumLapsRace()+"");
                lblRaceLength.setText(JkOutput.getNumberFmtEN(3).format(n.getLapLength()*n.getNumLapsRace())+" km");
                lblFast.setText(n.getFastLap().toLine());
                JkImage imgTrackMap = resources.getTrackMap(n);
                if(imgTrackMap.getRatio() > 1d) {
                    imageView.setFitWidth(400d);
                } else {
                    imageView.setFitHeight(400d);
                }
                imageView.setImage(imgTrackMap.getImage());
            }
        });

        return bpane;
    }
}
