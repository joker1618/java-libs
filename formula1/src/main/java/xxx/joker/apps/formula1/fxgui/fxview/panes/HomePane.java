package xxx.joker.apps.formula1.fxgui.fxview.panes;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxview.SubPane;

public class HomePane extends SubPane {

    private static final Logger LOG = LoggerFactory.getLogger(HomePane.class);

    public HomePane() {
        getStyleClass().add("bgYellow");
        setTop(new Label("HOMEPAGE"));
    }

}
