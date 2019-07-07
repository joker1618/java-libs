package xxx.joker.libs.repository.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkEncryption;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.repository.config.RepoCtx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

class RepoDAOEncrypted extends RepoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(RepoDAOEncrypted.class);

    RepoDAOEncrypted(RepoCtx ctx) {
        super(ctx);
    }

    @Override
    protected List<String> readRepoFile(Path sourcePath) {
        if(!Files.exists(sourcePath)) {
            return Collections.emptyList();
        } else {
            Path tmpPath = ctx.getTempFolder().resolve(sourcePath.getFileName());
            JkEncryption.decryptFile(sourcePath, tmpPath, ctx.getEncryptionPwd());
            List<String> lines = JkFiles.readLinesNotBlank(tmpPath);
            JkFiles.delete(tmpPath);
            return lines;
        }

    }

    @Override
    protected void saveRepoFile(Path outputPath, List<String> lines) {
        Path tmpPath = ctx.getTempFolder().resolve(outputPath.getFileName());
        JkFiles.writeFile(tmpPath, lines);
        JkEncryption.encryptFile(tmpPath, outputPath, ctx.getEncryptionPwd());
        JkFiles.delete(tmpPath);
    }


}
