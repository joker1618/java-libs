package xxx.joker.apps.formula1.finalGUI.panes;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.finalGUI.SubPane;

public class YearPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearPane.class);

    public YearPane() {
        getStyleClass().add("bgCyan");
        setTop(new Label("YEAR PANE"));
    }
}
