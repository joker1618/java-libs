package xxx.joker.apps.formula1.nuew.finalGUI.views;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.nuew.finalGUI.views.types.HomeView;
import xxx.joker.apps.formula1.nuew.model.F1Model;
import xxx.joker.apps.formula1.nuew.model.F1ModelImpl;

import java.util.HashMap;
import java.util.Map;

public class RootView extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(RootView.class);

    private Map<ViewType, SubView> viewMap = new HashMap<>();
    private F1Model model = F1ModelImpl.getInstance();

    public RootView() {
        setLeft(createLeftMenu());
        changeSubView(ViewType.HOME);

        getStyleClass().add("bgRed");
//        getLeft().getStyleClass().add("bgGreen");
    }

    private Pane createLeftMenu() {
        VBox menuBox = new VBox();
        menuBox.getStyleClass().addAll("menuBox", "spacing10", "pad10", "hcenter");

        Button btn = new Button("HOME");
        menuBox.getChildren().add(btn);

        btn = new Button("DRIVERS");
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

    private void changeSubView(ViewType viewType) {
        SubView subView = viewMap.get(viewType);
        if(subView == null) {
            switch (viewType) {
                case HOME:
                    subView = new HomeView();
                    break;
            }
            viewMap.put(viewType, subView);
        }
        setCenter(subView);
    }
}
