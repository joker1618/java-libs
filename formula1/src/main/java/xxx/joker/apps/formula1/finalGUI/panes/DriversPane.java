package xxx.joker.apps.formula1.finalGUI.panes;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.finalGUI.SubPane;

public class DriversPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(DriversPane.class);

    public DriversPane() {
        getStyleClass().add("bgOrange");
        setTop(new Label("DRIVERS PANE"));
    }

}
