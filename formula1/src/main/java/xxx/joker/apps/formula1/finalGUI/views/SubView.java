package xxx.joker.apps.formula1.finalGUI.views;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.dataCreator.model.F1Model;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;

public abstract class SubView extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(SubView.class);

    private F1Model model = F1ModelImpl.getInstance();


}
