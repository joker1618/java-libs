package xxx.joker.apps.formula1.fxgui.fxview.panes.yearPane;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;

public class YearSummaryPane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(YearSummaryPane.class);

    public YearSummaryPane() {
        getStyleClass().add("bgPurple");
        setTop(new Label("YEAR SUMMARY"));
    }
}
