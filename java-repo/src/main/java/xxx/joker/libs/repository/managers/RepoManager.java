package xxx.joker.libs.repository.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.JkEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoManager {

    private static final Logger logger = LoggerFactory.getLogger(RepoManager.class);

    private static final String EXT_REPO_FILE = "jkrepo";
    private static final String EXT_DEPS_FILE = "jkdeps";
    private static final String EXT_SEQUENCE_FILE = "jkseq";

    private final Path dbFolder;
    private final String dbName;
    private final RepoDataHandler repoHandler;
    private final DesignService designService;


    public RepoManager(Path dbFolder, String dbName, Collection<Class<?>> classes) {
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.designService = new DesignService(classes);
        this.repoHandler = readRepoData(designService.getEntityClasses());
        repoHandler.setDbSequence(loadSequenceValue());
    }

    public TreeMap<Class<?>, Set<JkEntity>> getDataSets() {
        return repoHandler.getDataSets();
    }
    public <T extends JkEntity> Set<T> getDataSet(Class<T> entityClazz) {
        return repoHandler.getDataSet(entityClazz);
    }

    private RepoDataHandler readRepoData(List<Class<?>> classes) {
        // Read entity data
        RepoLines repoLines = new RepoLines();
        for (Class<?> eclazz : classes) {
            Path repoPath = createRepoPath(eclazz);
            List<String> lines = new ArrayList<>();
            if (Files.exists(repoPath)) {
                lines.addAll(JkFiles.readLinesNotBlank(repoPath));
            }
            repoLines.getEntityLines().put(eclazz, lines);
        }
        // Read dependencies data
        Path depsPath = createDepsPath();
        if (Files.exists(depsPath)) {
            repoLines.getDepsLines().addAll(JkFiles.readLinesNotBlank(depsPath));
        }

        return designService.parseLines(repoLines);
    }
    private long loadSequenceValue() {
        Path pseq = createSequencePath();
        long seqVal = 0L;
        if(Files.exists(pseq)) {
            String strSeq = JkFiles.readLines(pseq).get(0);
            seqVal = JkConvert.toLong(strSeq);
        }
        return seqVal;
    }

    private Path createDepsPath() {
        String fname = strf("{}#{}", dbName, EXT_DEPS_FILE);
        return dbFolder.resolve(fname);
    }
    private Path createRepoPath(Class<?> clazz) {
        String fname = strf("{}#{}#{}", dbName, clazz.getName(), EXT_REPO_FILE);
        return dbFolder.resolve(fname);
    }
    private Path createSequencePath() {
        String fname = strf("{}#{}", dbName, EXT_SEQUENCE_FILE);
        return dbFolder.resolve(fname);
    }

}
