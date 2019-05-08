package xxx.joker.libs.repository.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

class RepoDAOEncrypted extends RepoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(RepoDAOEncrypted.class);

    private final Path tempFolder;
    private final String password;

    RepoDAOEncrypted(Path dbFolder, String dbName, List<ClazzWrapper> clazzWrappers, String password) {
        super(dbFolder, dbName, clazzWrappers);
        this.password = password;
        this.tempFolder = JkEnvironment.getAppsTempFolder().resolve(".repoTemp");
    }

    @Override
    protected List<String> readRepoFile(Path sourcePath) {
        if(!Files.exists(sourcePath)) {
            return Collections.emptyList();
        } else {
            Path tmpPath = tempFolder.resolve(sourcePath.getFileName());
            JkEncryption.decryptFile(sourcePath, tmpPath, password);
            List<String> lines = JkFiles.readLinesNotBlank(tmpPath);
            JkFiles.delete(tmpPath);
            return lines;
        }

    }

    @Override
    protected void saveRepoFile(Path outputPath, List<String> lines) {
        Path tmpPath = tempFolder.resolve(outputPath.getFileName());
        JkFiles.writeFile(tmpPath, lines);
        JkEncryption.encryptFile(tmpPath, outputPath, password);
        JkFiles.delete(tmpPath);
    }


}
