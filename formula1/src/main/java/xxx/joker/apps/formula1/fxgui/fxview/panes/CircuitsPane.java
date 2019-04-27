package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.model.entities.F1Circuit;
import xxx.joker.libs.core.media.JkImage;

public class CircuitsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitsPane.class);

    private TableView<F1Circuit> tableCircuits;

    public CircuitsPane() {
        getStyleClass().add("bgOrange");

        setLeft(createCircuitsPane());
        setCenter(createInfoPane());
    }

    private Pane createCircuitsPane() {
        TableColumn<F1Circuit, String> colNation = JfxTable.createColumn("NATION", "nation");
        TableColumn<F1Circuit, String> colCity = JfxTable.createColumn("CITY", "city");

        tableCircuits = new TableView<>();
        tableCircuits.getStyleClass().add("f1-table-main");
        tableCircuits.getColumns().addAll(colNation, colCity);
        tableCircuits.getItems().addAll(model.getCircuits());
        JfxTable.setFixedWidth(tableCircuits, "200 150", true);
//        tableCircuits.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
//            if(n == null) {
//                tableCircuits.getSelectionModel().select(o);
//            }
//        });

        Label caption = new Label("CIRCUIT LIST");
        HBox boxCaption = new HBox(caption);
        boxCaption.getStyleClass().addAll("f1-table-main-caption-box");

        BorderPane bp = new BorderPane();
        bp.getStyleClass().addAll("pad10");
        bp.getStyleClass().add("bgBlue");
        bp.setTop(boxCaption);
        bp.setCenter(tableCircuits);

        return bp;
    }

    private Pane createInfoPane() {
        BorderPane bp = new BorderPane();
        bp.getStyleClass().addAll("pad10");
        bp.getStyleClass().add("bgYellow");

        ImageView ivFlag = new ImageView();
        double ivW = 150d;
        double ivH = 100d;
        ivFlag.setFitWidth(150d);
        ivFlag.setFitHeight(100d);
        ivFlag.setPreserveRatio(true);
        Label lblTitle = new Label();
        HBox topBox = new HBox(ivFlag, lblTitle);
        topBox.getStyleClass().addAll("pad10", "spacing10");
        topBox.getStyleClass().addAll("bgGrey");
        bp.setTop(topBox);

        tableCircuits.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(n != null && n != o) {
                FxNation fxNation = guiModel.getNation(n.getNation());
                JkImage flag = fxNation.getFlagImage();
                ivFlag.setImage(flag.toFxImage());
            }
        });

        return bp;
    }
}
