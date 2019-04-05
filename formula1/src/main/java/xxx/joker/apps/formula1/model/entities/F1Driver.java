package xxx.joker.apps.formula1.model.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

public class F1Driver extends RepoEntity {
    @RepoField
    private String driverName;

    public F1Driver() {
    }

    public F1Driver(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String getPrimaryKey() {
        return driverName;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

}
