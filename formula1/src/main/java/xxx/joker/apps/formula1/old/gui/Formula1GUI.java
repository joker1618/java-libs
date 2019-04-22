package xxx.joker.apps.formula1.old.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.old.gui.yearView.YearView;
import xxx.joker.apps.formula1.old.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.old.dataCreator.model.F1ModelImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Formula1GUI extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Formula1GUI.class);
    private static boolean scenicView;

    private Stage primaryStage;

    private int year = 2017;
    private Map<Integer, YearView> seasonPanes = new TreeMap<>(Comparator.reverseOrder());

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        BorderPane rootPane = createRootPane();

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root, 600, 500);
        scene.setRoot(rootPane);

        // Show stage
        primaryStage.setScene(scene);
//        primaryStage.sizeToScene();
        primaryStage.setMaximized(true);
        primaryStage.show();

        if(scenicView) {
            ScenicView.show(scene);
        }

        rootPane.heightProperty().addListener(o -> LOG.debug("height {}", o));
    }

    private BorderPane createRootPane() {
        BorderPane rootPane = new BorderPane();

        ComboBox<Integer> cbox = new ComboBox<>();
        rootPane.setTop(cbox);

        F1Model model = F1ModelImpl.getInstance();
        List<Integer> years = model.getAvailableYears();
        cbox.getItems().setAll(years);
        cbox.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> {
            if(n != null && o != n) {
                YearView yearView = seasonPanes.get(n);
                if(yearView == null) {
                    yearView = new YearView(n);
                    seasonPanes.put(n, yearView);
                }
                rootPane.setCenter(yearView);
            }
        });

        cbox.getSelectionModel().selectFirst();

        return rootPane;
    }


    @Override
    public void stop() throws Exception {
//        F1Model.getInstance().commit();
        LOG.debug("STOP APP");
//        table.getItems().forEach(e -> display(e.toString()));
    }

    public static void main(String[] args) {
        scenicView = args.length > 0 && args[0].equals("-sv");
//		scenicView = true;
        launch(args);
    }


}
