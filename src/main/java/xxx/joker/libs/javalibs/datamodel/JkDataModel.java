package xxx.joker.libs.javalibs.datamodel;


import xxx.joker.libs.javalibs.datamodel.entity.JkEntity;
import xxx.joker.libs.javalibs.datamodel.persistence.JkPersistor;

import java.nio.file.Path;
import java.util.List;

public class JkDataModel {

    private JkPersistor persistor;

    protected JkDataModel(Path dbFolder, String dbName, String pkgToScan) {
        this.persistor = new JkPersistor(dbFolder, dbName, pkgToScan);
        persistor.loadData();
    }

    protected void commit() {
        persistor.saveData();
    }

    protected <T extends JkEntity> List<T> getData(Class<T> entityClazz) {
        return (List<T>) persistor.getData(entityClazz);
    }

}
