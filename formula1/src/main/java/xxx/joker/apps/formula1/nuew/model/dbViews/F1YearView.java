package xxx.joker.apps.formula1.nuew.model.dbViews;

import xxx.joker.apps.formula1.nuew.model.entities.F1Entrant;
import xxx.joker.libs.repository.design.RepoEntity;

import java.util.List;

public class F1YearView extends RepoEntity {

    private int year;
    private List<F1Entrant> entrants;


    @Override
    public String getPrimaryKey() {
        return null;
    }
}
