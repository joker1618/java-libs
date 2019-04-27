package xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;

public class YearResultsPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearResultsPane.class);

    public YearResultsPane() {
        getStyleClass().add("bgBlue");
        setTop(new Label("YEAR RESULTS"));
    }
}
