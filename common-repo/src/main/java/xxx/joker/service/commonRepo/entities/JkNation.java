package xxx.joker.service.commonRepo.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;
import xxx.joker.libs.repository.entities.RepoResource;

public class JkNation extends RepoEntity {

    @RepoField
    private String name;
    @RepoField
    private String code;
    @RepoField
    private RepoResource flagIcon;
    @RepoField
    private RepoResource flagImage;

    public JkNation() {
    }

    public JkNation(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryKey() {
        return "nation-" + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public RepoResource getFlagIcon() {
        return flagIcon;
    }

    public void setFlagIcon(RepoResource flagIcon) {
        this.flagIcon = flagIcon;
    }

    public RepoResource getFlagImage() {
        return flagImage;
    }

    public void setFlagImage(RepoResource flagImage) {
        this.flagImage = flagImage;
    }

}
