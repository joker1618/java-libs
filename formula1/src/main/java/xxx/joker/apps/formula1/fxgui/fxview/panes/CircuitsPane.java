package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;
import xxx.joker.apps.formula1.fxgui.fxview.snippets.TableChooseBox;
import xxx.joker.apps.formula1.fxlibs.JfxTable;
import xxx.joker.apps.formula1.fxlibs.JfxUtil;
import xxx.joker.apps.formula1.model.entities.F1Circuit;
import xxx.joker.libs.core.media.JkImage;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CircuitsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(CircuitsPane.class);

    private SimpleObjectProperty<F1Circuit> selectedCircuit = new SimpleObjectProperty<>();

    public CircuitsPane() {
        TableChooseBox circuitsBox = createCircuitsPane();
        setLeft(circuitsBox);

        setCenter(createInfoPane());

        getStyleClass().add("circuitsPane");
        getStylesheets().add(getClass().getResource("/css/CircuitsPane.css").toExternalForm());

        circuitsBox.getTableView().getSelectionModel().selectFirst();
    }

    private TableChooseBox createCircuitsPane() {
        TableColumn<F1Circuit, String> colNation = JfxTable.createColumn("NATION", "nation");
        TableColumn<F1Circuit, String> colCity = JfxTable.createColumn("CITY", "city");

        TableView<F1Circuit> tableCircuits = new TableView<>();
//        tableCircuits.getStyleClass().add("tableChoose");
        tableCircuits.getColumns().addAll(colNation, colCity);
        tableCircuits.getItems().addAll(model.getCircuits());

//        JfxTable.setFixedWidth(tableCircuits, "200 150", true);

//        JfxTable.setColPercWidth(tableCircuits, "0.6 0.4", true);
//        tableCircuits.setPrefWidth(417d);

        tableCircuits.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(n != null && n != o) {
                selectedCircuit.set(n);
//            } else if(n == null && o != null) {
//                tableCircuits.getSelectionModel().select(o);
            }
        });

        return new TableChooseBox("CIRCUITS", tableCircuits);

//        tableCircuits.setRowFactory(tv -> {
//            TableRow<F1Circuit> row = new TableRow<>();
//            row.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
//                if (e.isControlDown()) {
//                    display("consumed");
//                    e.consume();
//                }
//            });
//            return row ;
//        });

//        Label caption = new Label("CIRCUITS");
//        HBox boxCaption = new HBox(caption);
//        boxCaption.getStyleClass().addAll("tableChooseCaption");
//
//        BorderPane bp = new BorderPane();
////        bp.getStyleClass().addAll("pad10");
////        bp.getStyleClass().add("bgBlue");
//        bp.setTop(boxCaption);
////        bp.setCenter(tableCircuits);
//
//        JfxTable.autoResizeColumns(tableCircuits, true);
//        HBox tbox = new HBox(tableCircuits);
//        tbox.getStyleClass().add("tableChooseBox");
//        bp.setCenter(tbox);
//
//        return bp;
    }

    private Pane createInfoPane() {
        BorderPane bp = new BorderPane();
//        bp.getStyleClass().addAll("pad10");
        bp.getStyleClass().add("infoPane");

        ImageView ivFlag = JfxUtil.createImageView(150, 100);
        Label lblTitle = new Label();
        HBox topBox = new HBox(ivFlag, lblTitle);
//        topBox.getStyleClass().addAll("pad10", "spacing10");
//        topBox.getStyleClass().addAll("bgGrey");
        bp.setTop(topBox);

        selectedCircuit.addListener((obs,o,n) -> {
            if(n != null && n != o) {
                FxNation fxNation = guiModel.getNation(n.getNation());
                JkImage flag = fxNation.getFlagImage();
                ivFlag.setImage(flag.toFxImage());
                lblTitle.setText(strf("{} - {}", fxNation.getName(), n.getCity()));
            }
        });

        BorderPane.setMargin(bp, new Insets(0d, 0d, 0d, 40d));

        return bp;
    }
}
