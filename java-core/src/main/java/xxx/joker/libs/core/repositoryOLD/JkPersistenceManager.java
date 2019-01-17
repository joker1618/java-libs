package xxx.joker.libs.core.repositoryOLD;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@ToAnalyze
@Deprecated
class JkPersistenceManager {

    private static final Logger logger = LoggerFactory.getLogger(JkPersistenceManager.class);

    private static final String FILENAME_SEP = "###";
    private static final String FILENAME_EXT = "jkrepo";
    private static final String SEQUENCE_EXT = "jkseq";
    private static final String PH_ENTITY = "ENTITY";
    private static final String PH_DEPENDENCIES = "DEPENDENCIES";

    private final Path dbFolder;
    private final String dbName;
    private final Map<Class<?>, EntityPath> entityPaths;
    private final Path sequencePath;

    public JkPersistenceManager(Path dbFolder, String dbName, Collection<Class<?>> entityClasses) {
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.entityPaths = new HashMap<>();
        entityClasses.forEach(c -> entityPaths.put(c, new EntityPath(c)));
        this.sequencePath = dbFolder.resolve(dbName + "." + SEQUENCE_EXT);
    }

    public Map<Class<?>, EntityLines> readData() {
        List<EntityLines> list = JkStreams.map(entityPaths.keySet(), this::readRepoFile);
        return JkStreams.toMapSingle(list, EntityLines::getEntityClazz);
    }

    public long readSequence() {
        if(!Files.exists(sequencePath)){
            return 0L;
        }

        try {
            String line = Files.readAllLines(sequencePath).get(0);
            return JkConvert.toLong(line);
        } catch(IOException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public void saveData(Map<Class<?>, EntityLines> elMap, long sequenceValue) {
        try {
            logger.info("Saving data to DB [dbName={}] [dbFolder={}]", dbName, dbFolder);
            // Delete all existing files
            List<Path> dbPaths = JkFiles.findFiles(dbFolder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).startsWith(dbName));
            for(Path p : dbPaths) {
                Files.delete(p);
                logger.debug("Deleted file {}", p);
            }
            for (EntityLines el : elMap.values()) {
                EntityPath epath = entityPaths.get(el.getEntityClazz());
                JkFiles.writeFile(epath.getEntityPath(), el.getEntityLines(), false);
                JkFiles.writeFile(epath.getForeignKeysPath(), el.getForeignKeyLines(), false);
                logger.debug("Persisted entity {}", el.getEntityClazz().getName());
            }
            JkFiles.writeFile(sequencePath, sequenceValue+"", false);

        } catch(IOException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private EntityLines readRepoFile(Class<?> clazz) {
        try {
            EntityLines el = new EntityLines(clazz);
            EntityPath epath = entityPaths.get(clazz);

            if(Files.exists(epath.getEntityPath())) {
                List<String> lines = Files.readAllLines(epath.getEntityPath());
                lines.removeIf(StringUtils::isBlank);
                el.getEntityLines().addAll(lines);
            }

            if(Files.exists(epath.getForeignKeysPath())) {
                List<String> lines = Files.readAllLines(epath.getForeignKeysPath());
                lines.removeIf(StringUtils::isBlank);
                el.getForeignKeyLines().addAll(lines);
            }

            return el;

        } catch(IOException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private class EntityPath {
        private Class<?> entityClazz;
        private Path entityPath;
        private Path foreignKeysPath;

        EntityPath(Class<?> entityClazz) {
            this.entityClazz = entityClazz;
            this.entityPath = createRepoPath(entityClazz, true);
            this.foreignKeysPath = createRepoPath(entityClazz, false);
        }

        private Path createRepoPath(Class<?> clazz, boolean isEntity) {
            String fname = dbName + FILENAME_SEP;
            fname += isEntity ? PH_ENTITY : PH_DEPENDENCIES;
            fname += FILENAME_SEP + clazz.getName() + "." + FILENAME_EXT;
            return dbFolder.resolve(fname);
        }

        public Class<?> getEntityClazz() {
            return entityClazz;
        }

        public Path getEntityPath() {
            return entityPath;
        }

        public Path getForeignKeysPath() {
            return foreignKeysPath;
        }
    }

    static class EntityLines {
        private Class<?> entityClazz;
        private List<String> entityLines;
        private List<String> foreignKeyLines;

        EntityLines(Class<?> entityClazz) {
            this.entityClazz = entityClazz;
            this.entityLines = new ArrayList<>();
            this.foreignKeyLines = new ArrayList<>();
        }

        public Class<?> getEntityClazz() {
            return entityClazz;
        }

        public List<String> getEntityLines() {
            return entityLines;
        }

        public List<String> getForeignKeyLines() {
            return foreignKeyLines;
        }
    }
}
