package xxx.joker.apps.formula1.old.finalGUI.views.types;

import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.old.finalGUI.views.SubView;

public class HomeView extends SubView {

    private static final Logger LOG = LoggerFactory.getLogger(HomeView.class);

    public HomeView() {
        getStyleClass().add("bgYellow");
        setTop(new Label("HOMEPAGE"));
    }

}
