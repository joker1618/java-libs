package xxx.joker.service.sharedRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.repository.JkRepoFile;
import xxx.joker.service.sharedRepo.entities.JkNation;

import java.nio.file.Path;

import static xxx.joker.service.sharedRepo.config.Configs.DB_FOLDER;
import static xxx.joker.service.sharedRepo.config.Configs.DB_NAME;

public class JkSharedRepoImpl extends JkRepoFile implements JkSharedRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkSharedRepoImpl.class);
    private static JkSharedRepoImpl instance;


    protected JkSharedRepoImpl(Path dbFolder, String dbName, String pkgToScan) {
        super(dbFolder, dbName, pkgToScan);
    }

    public static synchronized JkSharedRepo getInstance() {
        if(instance == null) {
            instance = new JkSharedRepoImpl(DB_FOLDER, DB_NAME, "xxx.joker.service.sharedRepo.entities");
        }
        return instance;
    }

    @Override
    public JkNation getNation(String nationName) {
        return get(JkNation.class, n -> n.getName().equalsIgnoreCase(nationName));
    }

}
