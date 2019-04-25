package xxx.joker.apps.formula1.finalGUI;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.finalGUI.panes.DriversPane;
import xxx.joker.apps.formula1.finalGUI.panes.HomePane;
import xxx.joker.apps.formula1.finalGUI.panes.yearPane.YearSummaryPane;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;

import java.util.HashMap;
import java.util.Map;

public class RootPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(RootPane.class);

    private Map<PaneType, SubPane> viewMap = new HashMap<>();
    private F1Model model = F1ModelImpl.getInstance();

    public RootPane() {
        setLeft(createLeftMenu());
        changeSubView(PaneType.HOME);

        getStyleClass().add("bgRed");
//        getLeft().getStyleClass().add("bgGreen");
    }

    private Pane createLeftMenu() {
        VBox menuBox = new VBox();
        menuBox.getStyleClass().addAll("menuBox", "spacing10", "pad10", "hcenter");

        Button btn = new Button("HOME");
        btn.setOnAction(e -> changeSubView(PaneType.HOME));
        menuBox.getChildren().add(btn);

        btn = new Button("DRIVERS");
        btn.setOnAction(e -> changeSubView(PaneType.DRIVERS));
        menuBox.getChildren().add(btn);

        btn = new Button("TEAMS");
        menuBox.getChildren().add(btn);

        btn = new Button("CIRCUITS");
        menuBox.getChildren().add(btn);

        VBox yearBox = new VBox();
        yearBox.getStyleClass().addAll("yearBox", "spacing10", "pad10", "hcenter");
        menuBox.getChildren().add(yearBox);

        ComboBox<Integer> comboSelYear = new ComboBox<>();
        yearBox.getChildren().add(comboSelYear);
        comboSelYear.getItems().setAll(model.getAvailableYears());
//        comboSelYear.getSelectionModel().select(0);

        menuBox.heightProperty().addListener(o -> LOG.debug("height {}", o));
        menuBox.getStyleClass().add("bgGreen");
        return menuBox;
    }

    private void changeSubView(PaneType paneType) {
        SubPane subPane = viewMap.get(paneType);
        if(subPane == null) {
            switch (paneType) {
                case HOME:
                    subPane = new HomePane();
                    break;
                case DRIVERS:
                    subPane = new DriversPane();
                    break;
                case YEAR_SUMMARY:
                    subPane = new YearSummaryPane();
                    break;
            }
            viewMap.put(paneType, subPane);
        }
        setCenter(subPane);
    }
}
