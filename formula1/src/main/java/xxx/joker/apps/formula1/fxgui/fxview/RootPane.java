package xxx.joker.apps.formula1.fxgui.fxview;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModel;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModelImpl;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.fxgui.fxmodel.SeasonView;
import xxx.joker.apps.formula1.fxgui.fxview.panes.CircuitsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.DriversPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.HomePane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.TeamsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearEntrantsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearResultsPane;
import xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane.YearSummaryPane;
import xxx.joker.apps.formula1.fxlibs.JfxUtil;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Circuit;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.libs.core.cache.JkCache;

import java.util.function.Supplier;

public class RootPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(RootPane.class);


    private F1Model model = F1ModelImpl.getInstance();
    protected F1GuiModel guiModel = F1GuiModelImpl.getInstance();

    private JkCache<PaneType, SubPane> cachePanes = new JkCache<>();


    public RootPane() {
        setLeft(createLeftMenu());

        changeSubView(PaneType.HOME);
//        changeSubView(PaneType.CIRCUITS);

        getStyleClass().add("bgRed");
        getStylesheets().add(getClass().getResource("/css/RootPane.css").toExternalForm());
//        getLeft().getStyleClass().add("bgGreen");
    }

    private Pane createLeftMenu() {
        VBox menuBox = new VBox();
        menuBox.getStyleClass().addAll("menuBox", "spacing10", "pad10", "hcenter");
        menuBox.getStyleClass().add("bgGreen");

        Button btn = new Button("HOME");
        btn.setOnAction(e -> changeSubView(PaneType.HOME));
        menuBox.getChildren().add(btn);

        btn = new Button("DRIVERS");
        btn.setOnAction(e -> changeSubView(PaneType.DRIVERS));
        menuBox.getChildren().add(btn);

        btn = new Button("TEAMS");
        btn.setOnAction(e -> changeSubView(PaneType.TEAMS));
        menuBox.getChildren().add(btn);

        btn = new Button("CIRCUITS");
        btn.setOnAction(e -> changeSubView(PaneType.CIRCUITS));
        menuBox.getChildren().add(btn);

        VBox yearBox = new VBox();
        yearBox.getStyleClass().addAll("yearBox", "spacing10", "pad10", "hcenter");
        menuBox.getChildren().add(yearBox);

        ComboBox<Integer> comboSelYear = new ComboBox<>();
        yearBox.getChildren().add(comboSelYear);
        comboSelYear.getItems().setAll(model.getAvailableYears());
        comboSelYear.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(n != null && n != o)     guiModel.setSelectedYear(n);
        });

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
                    setGraphic(JfxUtil.createImageView(fnat.getFlagIcon().toFxImage(), 45, 28));
                    setText(fnat.getCode());
                }
            }
        });
        guiModel.addYearChangeAction(n -> gpListView.getItems().setAll(model.getGranPrixs(n)));

        comboSelYear.getSelectionModel().select(0);

//        menuBox.heightProperty().addListener(o -> LOG.debug("height {}", o));

        return menuBox;
    }

    private void changeSubView(PaneType paneType) {
        Supplier<SubPane> subPane = null;
        switch (paneType) {
            case HOME:
                subPane = HomePane::new;
                break;
            case DRIVERS:
                subPane = DriversPane::new;
                break;
            case TEAMS:
                subPane = TeamsPane::new;
                break;
            case CIRCUITS:
                subPane = CircuitsPane::new;
                break;
            case YEAR_SUMMARY:
                subPane = YearSummaryPane::new;
                break;
            case YEAR_ENTRANTS:
                subPane = YearEntrantsPane::new;
                break;
            case YEAR_RESULTS:
                subPane = YearResultsPane::new;
                break;
        }

        SubPane pane = cachePanes.get(paneType, subPane);
        setCenter(pane);
    }
}
