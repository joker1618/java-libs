package xxx.joker.apps.formula1.finalGUI;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;

public abstract class SubPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(SubPane.class);

    private F1Model model = F1ModelImpl.getInstance();


}
