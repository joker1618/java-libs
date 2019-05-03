package xxx.joker.apps.formula1.fxgui.fxview;

import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModel;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModelImpl;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxview.panes.CircuitsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.DriversPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.HomePane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.TeamsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearEntrantsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearGpPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearResultsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearSummaryPane;
import xxx.joker.apps.formula1.fxlibs.JfxUtil;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.libs.core.cache.JkCache;

import java.util.List;

public class RootPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(RootPane.class);


    private F1Model model = F1ModelImpl.getInstance();
    protected F1GuiModel guiModel = F1GuiModelImpl.getInstance();

    private JkCache<PaneType, SubPane> cachePanes = new JkCache<>();


    public RootPane() {
        createSubPanes();

        setLeft(createLeftMenu());

//        changeSubView(PaneType.HOME);
//        changeSubView(PaneType.CIRCUITS);
        changeSubView(PaneType.YEAR_ENTRANTS);

        getStyleClass().add("bgRed");
        getStylesheets().add(getClass().getResource("/css/RootPane.css").toExternalForm());
//        getLeft().getStyleClass().add("bgGreen");
    }

    private Pane createLeftMenu() {
        VBox menuBox = new VBox();
        menuBox.getStyleClass().add("rootMenu");

        VBox globalBox = new VBox();
        menuBox.getChildren().add(globalBox);
        globalBox.getStyleClass().add("globalBox");

        Button btn = new Button("HOME");
        btn.setOnAction(e -> changeSubView(PaneType.HOME));
        globalBox.getChildren().add(btn);

        btn = new Button("DRIVERS");
        btn.setOnAction(e -> changeSubView(PaneType.DRIVERS));
        globalBox.getChildren().add(btn);

        btn = new Button("TEAMS");
        btn.setOnAction(e -> changeSubView(PaneType.TEAMS));
        globalBox.getChildren().add(btn);

        btn = new Button("CIRCUITS");
        btn.setOnAction(e -> changeSubView(PaneType.CIRCUITS));
        globalBox.getChildren().add(btn);

        VBox yearBox = new VBox();
        yearBox.getStyleClass().addAll("yearBox");
        menuBox.getChildren().add(yearBox);

        ComboBox<Integer> comboSelYear = new ComboBox<>();
        yearBox.getChildren().add(comboSelYear);
        comboSelYear.getItems().setAll(model.getAvailableYears());
        comboSelYear.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> guiModel.setSelectedYear(n));

        btn = new Button("SUMMARY");
        btn.setOnAction(e -> changeSubView(PaneType.YEAR_SUMMARY));
        yearBox.getChildren().add(btn);

        btn = new Button("ENTRANTS");
        btn.setOnAction(e -> changeSubView(PaneType.YEAR_ENTRANTS));
        yearBox.getChildren().add(btn);

        btn = new Button("RESULTS");
        btn.setOnAction(e -> changeSubView(PaneType.YEAR_RESULTS));
        yearBox.getChildren().add(btn);

        ListView<F1GranPrix> gpListView = new ListView<>();
        gpListView.getStyleClass().addAll("gpList");
        yearBox.getChildren().add(gpListView);
        gpListView.setCellFactory(param -> new ListCell<F1GranPrix>() {
            @Override
            protected void updateItem(F1GranPrix item, boolean empty) {
                super.updateItem(item, empty);
                if(item == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    FxNation fnat = guiModel.getNation(item.getCircuit().getNation());
                    HBox iconBox = new HBox(JfxUtil.createImageView(fnat.getFlagIcon().toFxImage(), 45, 28));
                    iconBox.getStyleClass().addAll("iconBox");
                    setGraphic(iconBox);
                    setText(fnat.getCode());
                }
            }
        });
        gpListView.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            guiModel.setSelectedGranPrix(n);
            changeSubView(PaneType.YEAR_GRAN_PRIX);
        });
        guiModel.addChangeActionYear(n -> {
            List<F1GranPrix> gps = model.getGranPrixs(n);
            gpListView.getItems().setAll(gps);
            guiModel.setSelectedGranPrix(gps.get(0));
        });

        comboSelYear.getSelectionModel().selectFirst();

//        menuBox.heightProperty().addListener(o -> LOG.debug("height {}", o));

        return menuBox;
    }

    private void createSubPanes() {
        cachePanes.add(PaneType.HOME, new HomePane());
        cachePanes.add(PaneType.DRIVERS, new DriversPane());
        cachePanes.add(PaneType.TEAMS, new TeamsPane());
        cachePanes.add(PaneType.CIRCUITS, new CircuitsPane());
        cachePanes.add(PaneType.YEAR_SUMMARY, new YearSummaryPane());
        cachePanes.add(PaneType.YEAR_ENTRANTS, new YearEntrantsPane());
        cachePanes.add(PaneType.YEAR_RESULTS, new YearResultsPane());
        cachePanes.add(PaneType.YEAR_GRAN_PRIX, new YearGpPane());
    }

    private void changeSubView(PaneType paneType) {
        SubPane pane = cachePanes.get(paneType);
        setCenter(pane);
    }
}
