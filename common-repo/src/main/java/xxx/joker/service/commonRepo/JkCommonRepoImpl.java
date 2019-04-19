package xxx.joker.service.commonRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.repository.JkRepoFile;
import xxx.joker.service.commonRepo.entities.JkNation;

import java.nio.file.Path;
import java.util.Set;

import static xxx.joker.service.commonRepo.config.Configs.*;

public class JkCommonRepoImpl extends JkRepoFile implements JkCommonRepo {

    private static final Logger LOG = LoggerFactory.getLogger(JkCommonRepoImpl.class);
    private static JkCommonRepoImpl instance;


    protected JkCommonRepoImpl(Path dbFolder, String dbName, String pkgToScan) {
        super(dbFolder, dbName, pkgToScan);
    }

    public static synchronized JkCommonRepo getInstance() {
        if(instance == null) {
            instance = new JkCommonRepoImpl(DB_FOLDER, DB_NAME, "xxx.joker.service.commonRepo.entities");
        }
        return instance;
    }
    
//    public JkNation getNation(String nationName) {
//        Set<JkNation> ds = getDataSet(JkNation.class);
//        JkNation unique = JkStreams.findUnique(ds, e -> e.getName().equals(nationName));
//    }
}
