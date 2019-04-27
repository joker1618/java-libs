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
        this.flagImage = fromRepoUri(nation.getFlagImage().getRepoURI());
        this.flagIcon = fromRepoUri(nation.getFlagIcon().getRepoURI());
    }

    private JkImage fromRepoUri(RepoUri repoUri) {
        JkImage img = new JkImage();
        img.setPath(repoUri.getPath());
        img.setWidth(Integer.parseInt(repoUri.getMetaData().get(RepoMetaData.Attrib.WIDTH)));
        img.setHeight(Integer.parseInt(repoUri.getMetaData().get(RepoMetaData.Attrib.HEIGHT)));
        return img;
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
