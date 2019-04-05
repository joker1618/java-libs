package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.time.LocalDate;
import java.util.List;

public class F1GranPrix extends RepoEntity {

    @RepoField
    private int year;
    @RepoField
    private int num;
    @RepoField
    private LocalDate day;
    @RepoField
    private List<F1Qualify> qualifies;
    @RepoField
    private List<F1Race> races;

    @Override
    public String getPrimaryKey() {
        return null;
    }
}
