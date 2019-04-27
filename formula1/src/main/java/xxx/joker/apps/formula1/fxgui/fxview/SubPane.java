package xxx.joker.apps.formula1.fxgui.fxview;

import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModel;
import xxx.joker.apps.formula1.fxgui.fxmodel.F1GuiModelImpl;
import xxx.joker.apps.formula1.fxgui.fxmodel.FxNation;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.libs.core.cache.JkCache;
import xxx.joker.service.sharedRepo.JkSharedRepo;
import xxx.joker.service.sharedRepo.JkSharedRepoImpl;

public abstract class SubPane extends BorderPane {

    private static final Logger LOG = LoggerFactory.getLogger(SubPane.class);

    protected F1Model model = F1ModelImpl.getInstance();
    protected JkSharedRepo sharedRepo = JkSharedRepoImpl.getInstance();
    protected F1GuiModel guiModel = F1GuiModelImpl.getInstance();

}
