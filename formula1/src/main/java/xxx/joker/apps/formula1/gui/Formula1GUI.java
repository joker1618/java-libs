package xxx.joker.apps.formula1.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxlibs.X_FxUtil;
import xxx.joker.apps.formula1.gui.wrapper.F1EntrantWrapper;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Driver;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Team;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Formula1GUI extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Formula1GUI.class);
    private static boolean scenicView;

    private Stage primaryStage;

    private final int year = 2018;
    TableView<F1EntrantWrapper> table = new TableView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Pane pane = createRootPane();

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root, 600, 300);
        scene.setRoot(pane);

        // Show stage
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        if(scenicView) {
            ScenicView.show(scene);
        }
    }

    private Pane createRootPane() {
        F1Model model = F1ModelImpl.getInstance();
        table = F1EntrantWrapper.createTableView();
//        table = new TableView<>();
//        TableColumn<F1Entrant, F1Team> colTeamName = X_FxUtil.createTableColumn("TEAM", "team", F1Team::getTeamName);
//        TableColumn<F1Entrant, F1Team> colTeamNat = X_FxUtil.createTableColumn("NATION", "team", F1Team::getNation);
//        TableColumn<F1Entrant, String> colEngine = X_FxUtil.createTableColumnString("ENGINE", "engine");
//        TableColumn<F1Entrant, Integer> colCarNo = X_FxUtil.createTableColumnInteger("CAR NUM", "carNo");
//        TableColumn<F1Entrant, F1Driver> colDriverName = X_FxUtil.createTableColumn("DRIVER", "driver", F1Driver::getFullName);
//        TableColumn<F1Entrant, String> colDriverNat = X_FxUtil.createTableColumnString("NATION", "DN");
////        TableColumn<F1Entrant, F1Driver> colDriverNat = X_FxUtil.createTableColumn("NATION", "driver", F1Driver::getNation);
//        TableColumn<F1Entrant, F1Driver> colDriverCity = X_FxUtil.createTableColumn("BIRTH CITY", "driver", F1Driver::getBirthCity);
//        TableColumn<F1Entrant, F1Driver> colDriverBirthDate = X_FxUtil.createTableColumn("BIRTH DATE", "driver", d -> d.getBirthDate().toString());
////        table.getColumns().addAll(colEngine, colCarNo);
//        table.getColumns().addAll(colTeamName, colTeamNat, colEngine, colCarNo, colDriverName, colDriverNat, colDriverCity, colDriverBirthDate);
        List<F1Entrant> entrants = model.getEntrants(year);
        List<F1EntrantWrapper> items = JkStreams.map(entrants, F1EntrantWrapper::new);
        table.getItems().setAll(items);

        Label labelTitle = new Label(strf("ENTRANTS {}", year));

        BorderPane bpane = new BorderPane();
        bpane.setTop(labelTitle);
        bpane.setCenter(table);
        return bpane;
    }

    @Override
    public void stop() throws Exception {
//        F1Model.getInstance().commit();
        logger.debug("STOP APP");
        table.getItems().forEach(e -> display(e.toString()));
    }

    public static void main(String[] args) {
        scenicView = args.length > 0 && args[0].equals("-scenicView");
//		scenicView = true;
        launch(args);
    }

}
