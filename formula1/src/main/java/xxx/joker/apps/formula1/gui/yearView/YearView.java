package xxx.joker.apps.formula1.gui.yearView;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import xxx.joker.apps.formula1.fxlibs.X_FxTable;
import xxx.joker.apps.formula1.model.beans.F1Season;
import xxx.joker.apps.formula1.model.beans.F1SeasonResult;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.apps.formula1.model.managers.ResourceManager;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class YearView extends BorderPane {

    private final F1Model model = F1ModelImpl.getInstance();
    private F1Season season;

    private CheckPane checkPane;
    private EntrantsPane entrantsPane;
    private GranPrixPane granPrixPane;
    private ResultsPane resultPane;

    public YearView(int year) {
        this.season = model.getSeason(year);

        this.checkPane = new CheckPane();
        this.entrantsPane = new EntrantsPane(season.getEntrants());
        this.resultPane = new ResultsPane(season);
        this.granPrixPane = new GranPrixPane(season);

        setLeft(createLeft());
        setCenter(entrantsPane);
//        setCenter(resultPane);

    }

    private Pane createLeft() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        ObservableList<Node> nodes = vBox.getChildren();

        Button btnCheck = new Button("CHECK PANE");
        btnCheck.setOnAction(e -> setCenter(checkPane));
        nodes.add(btnCheck);

        Button btnEntrants = new Button("ENTRANTS");
        btnEntrants.setOnAction(e -> setCenter(entrantsPane));
        nodes.add(btnEntrants);

        Button btnResult = new Button("RESULTS");
        btnResult.setOnAction(e -> setCenter(resultPane));
        nodes.add(btnResult);

        ListView<F1GranPrix> lview = new ListView<>();
        lview.getItems().setAll(season.getGpList());
        lview.setCellFactory(lv -> new ListCell<F1GranPrix>() {
            @Override
            public void updateItem(F1GranPrix item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.getCircuit().getNation());
                }
            }
        });
        lview.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(n != null && n != o) {
                granPrixPane.setGranPrix(n);
                setCenter(granPrixPane);
            }
        });
        lview.setMaxWidth(-1);
        nodes.add(lview);

        return vBox;
    }
    
//    private TableView<F1Entrant> createTableEntrants() {
//        TableView<F1Entrant> t = new TableView<>();
//        t.getColumns().add(X_FxTable.createColumn("TEAM NAME", e -> e.getTeam().getTeamName()));
//        TableColumn<F1Entrant, String> colTeamNation = X_FxTable.createColumn("TEAM NATION", e -> e.getTeam().getNation());
////        colTeamNation.setCellFactory(p -> new ImageCell<>());
//        t.getColumns().add(colTeamNation);
//        t.getColumns().add(X_FxTable.createColumn("engine"));
//        t.getColumns().add(X_FxTable.createColumn("carNo"));
//        t.getColumns().add(X_FxTable.createColumn("DRIVER NAME", e -> e.getDriver().getFullName()));
//        t.getColumns().add(X_FxTable.createColumn("DRIVER INFO", e -> strf("{}, {}, {}", e.getDriver().getBirthDate(), e.getDriver().getCity(), e.getDriver().getNation())));
//        return t;
//    }
//
//    private TableView<F1SeasonResult> createTableResults() {
//        TableView<F1SeasonResult> t = new TableView<>();
//        t.getColumns().add(X_FxTable.createColumn("DRIVER", e -> e.getDriver().getFullName()));
//
//        season.getGpList().forEach(gp -> {
//            TableColumn<F1SeasonResult, F1Race> col = new TableColumn<>();
//            Image img = new ResourceManager().getFlagIconImage(gp.getCircuit().getNation());
//            col.setGraphic(new ImageView(img));
//            col.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getPoints().get(gp)));
//            col.setCellFactory(param -> new TableCell<F1SeasonResult, F1Race> () {
//                @Override
//                protected void updateItem (F1Race item, boolean empty) {
//                    super.updateItem (item, empty);
//                    if (item == null || empty) {
//                        setText(null);
//                    } else {
//                        setText(item.isRetired() ? "RET" : ""+item.getPos());
//                    }
//                }
//            });
//            col.setComparator(Comparator.comparing(F1Race::getPos));
//            t.getColumns().add(col);
//        });
//
//        t.getColumns().add(X_FxTable.createColumn("TOT.", F1SeasonResult::getTotPoints));
//
//        return t;
//    }



    private static class ImageCell<T> extends TableCell<T, String> {

        private final ImageView image;

        public ImageCell() {
            // add ImageView as graphic to display it in addition
            // to the text in the cell
            this.image = new ImageView();
//            this.image.setFitHeight(30);
//            this.image.setFitWidth(45);
            this.image.setPreserveRatio(true);
            setGraphic(this.image);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                // set back to look of empty cell
                setText(null);
                image.setImage(null);
            } else {
                // set image and text for non-empty cell
                Path flagPath = Paths.get("C:\\Users\\fede\\.appsFolder\\formula1\\images\\icons\\flags\\Monaco.png");
                Image img = new Image(JkFiles.toURL(flagPath));
                image.setImage(img);
                setText(item);
            }
        }
    }


}
