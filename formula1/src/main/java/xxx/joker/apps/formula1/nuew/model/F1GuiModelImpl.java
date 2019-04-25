package xxx.joker.apps.formula1.nuew.model;

import xxx.joker.apps.formula1.nuew.common.F1Const;
import xxx.joker.libs.repository.JkRepoFile;

import java.nio.file.Path;

public class F1GuiModelImpl extends JkRepoFile implements F1GuiModel {

    protected F1GuiModelImpl(Path dbFolder, String dbName, String... pkgsToScan) {
        super(F1Const.DB_FOLDER, F1Const.DB_NAME,
                "xxx.joker.apps.formula1.nuew.model.entities",
                "xxx.joker.apps.formula1.nuew.model.views"
        );
    }





}
