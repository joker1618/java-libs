package xxx.joker.service.commonRepo.entities;

import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

public class JkNation extends RepoEntity {

    @RepoField
    private String name;
    @RepoField
    private String code;
    @RepoField
    private JkFlag flag;

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

    public JkFlag getFlag() {
        return flag;
    }

    public void setFlag(JkFlag flag) {
        this.flag = flag;
    }
}
