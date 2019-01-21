package trymodel.entities3;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

public class Categ extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private String name;

    public Categ() {
    }

    public Categ(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryKey() {
        return name;
    }
}
