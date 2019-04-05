package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

public class F1Qualify extends RepoEntity {

    @RepoField
    private int year;

    @Override
    public String getPrimaryKey() {
        return null;
    }
}
