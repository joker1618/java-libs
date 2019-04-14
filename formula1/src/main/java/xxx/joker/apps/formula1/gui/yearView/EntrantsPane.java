package xxx.joker.apps.formula1.gui.yearView;

import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xxx.joker.apps.formula1.fxlibs.JkImage;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.F1ResourceManager;
import xxx.joker.apps.formula1.model.F1Resources;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.files.JkFiles;

import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class EntrantsPane extends BorderPane {

    private final F1Resources resources;
    private TableView<F1Entrant> table;
    private BorderPane driverPane;

    public EntrantsPane(List<F1Entrant> entrants) {
        this.resources = F1ResourceManager.getInstance();

        table = createTableEntrants();
        table.getItems().setAll(entrants);
        driverPane = createDriverPane();

        setCenter(table);
        setRight(driverPane);
    }


    public void setEntrants(List<F1Entrant> entrants) {
        table.getItems().setAll(entrants);
    }

    private TableView<F1Entrant> createTableEntrants() {
        TableView<F1Entrant> t = new TableView<>();
        t.getColumns().add(X_FxTable.createColumn("TEAM NAME", e -> e.getTeam().getTeamName()));
        t.getColumns().add(X_FxTable.createColumn("TEAM NATION", e -> e.getTeam().getNation()));
        t.getColumns().add(X_FxTable.createColumn("engine"));
        t.getColumns().add(X_FxTable.createColumn("carNo"));
        t.getColumns().add(X_FxTable.createColumn("DRIVER NAME", e -> e.getDriver().getFullName()));
        t.getColumns().add(X_FxTable.createColumn("DRIVER INFO",
                e -> strf("{}, {}, {}", e.getDriver().getBirthDate(), e.getDriver().getCity(), e.getDriver().getNation())
        ));
        t.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        return t;
    }

    private BorderPane createDriverPane() {
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
        Label lblBDate = new Label();
//        lblBDate.textProperty().bind(Bindings.createStringBinding(() -> JkDates.format(gp.get().getDate(), "dd/MM/yyyy"), gp));
        gridPane.add(new Label("BIRTH DATE:"), 0, row);
        gridPane.add(lblBDate, 1, row);

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

        // BOTTOM
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
//        imageView.setFitWidth(400d);
//        imageView.setFitHeight(400d);
//        imageView.imageProperty().bind(Bindings.createObjectBinding(() -> resources.getTrackMap(gp.get()).getImage(), gp));
        HBox hBox = new HBox(imageView);
        hBox.setStyle("-fx-border-color: blue");
        vBox.getChildren().add(hBox);

        table.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(o != n && n != null) {
                caption.setText(n.getDriver().getFullName());
                lblBDate.setText(JkDates.format(n.getDriver().getBirthDate(), "dd/MM/yyyy"));
                lblNation.setText(n.getDriver().getNation());
                lblCity.setText(n.getDriver().getCity());
                JkImage imgDriver = resources.getDriverPicture(n.getDriver());
                if(imgDriver.getRatio() > 0.8) {
                    imageView.setFitWidth(400d);
                } else {
                    imageView.setFitHeight(500d);
                }
                imageView.setImage(imgDriver.getImage());
            }
        });

        return bpane;
    }
}
