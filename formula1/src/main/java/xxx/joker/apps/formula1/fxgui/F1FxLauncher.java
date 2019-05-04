package xxx.joker.apps.formula1.fxgui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.PaneType;
import xxx.joker.apps.formula1.fxgui.fxview.RootPane;

import java.util.logging.Level;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class F1FxLauncher extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(F1FxLauncher.class);
    private static boolean scenicView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        RootPane rootPane = new RootPane();

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root, 600, 500);
        scene.setRoot(rootPane);

        // Show stage
        primaryStage.setScene(scene);
//        primaryStage.sizeToScene();
        primaryStage.setMaximized(true);

//        primaryStage.setResizable(false);
        primaryStage.show();


        if(scenicView) {
            ScenicView.show(scene);
        }

//        rootPane.heightProperty().addListener(o -> LOG.debug("height {}", o));

        scene.getStylesheets().add(getClass().getResource("/css/common.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/tableView.css").toExternalForm());
    }


    @Override
    public void stop() throws Exception {
        LOG.debug("STOP APP");
    }

    public static void main(String[] args) {
        scenicView = args.length > 0 && args[0].equals("-sv");
        launch(args);
    }


}
