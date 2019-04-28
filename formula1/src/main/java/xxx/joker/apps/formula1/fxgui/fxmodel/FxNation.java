package xxx.joker.apps.formula1.fxgui.fxmodel;

import javafx.scene.image.Image;
import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.repository.entities.RepoMetaData;
import xxx.joker.libs.repository.entities.RepoUri;
import xxx.joker.service.sharedRepo.entities.JkNation;

public class FxNation {

    private JkNation nation;
    private JkImage flagImage;
    private JkImage flagIcon;

    public FxNation(JkNation nation) {
        this.nation = nation;
        this.flagImage = nation.getFlagImage().getRepoURI().toJkImage();
        this.flagIcon = nation.getFlagIcon().getRepoURI().toJkImage();
    }

    public String getName() {
        return nation.getName();
    }

    public String getCode() {
        return nation.getCode();
    }

    public JkImage getFlagImage() {
        return flagImage;
    }

    public JkImage getFlagIcon() {
        return flagIcon;
    }
}
