package xxx.joker.libs.javalibs.datamodel.persistence;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.javalibs.datamodel.entity.JkEntity;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;
import xxx.joker.libs.javalibs.utils.JkFiles;
import xxx.joker.libs.javalibs.utils.JkStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static xxx.joker.libs.javalibs.datamodel.persistence.EntityParser.EntityLines;

public class JkPersistor {

    private static final Logger logger = LoggerFactory.getLogger(JkPersistor.class);

    private static final String FILENAME_SEP = "###";
    private static final String FILENAME_EXT = "jkrepo";
    private static final String PH_ENTITY = "ENTITY";
    private static final String PH_DEPENDENCIES = "DEPENDENCIES";

    private final Path dbFolder;
    private final String dbName;
    private final String pkgToScan;

    private final EntityParser entityParser;

    private Map<Class<?>, TreeSet<JkEntity>> dataMap;

    public JkPersistor(Path dbFolder, String dbName, String pkgToScan) {
        logger.info("Creating persistence persistence:  [dbName={}] [dbFolder={}]", dbName, dbFolder);
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.pkgToScan = pkgToScan;
        this.entityParser = new EntityParser(pkgToScan);
        this.dataMap = new HashMap<>();
    }

    public void loadData() {
        logger.info("Loading data from DB [dbName={}] [dbFolder={}]", dbName, dbFolder);
        Map<Class<?>, EntityLines> elMap = JkStreams.toMapSingle(entityParser.getEntityClasses(), c -> c, this::readRepoFile);
        this.dataMap = entityParser.parseData(elMap);
    }

    public void saveData() {
        try {
            logger.info("Saving data to DB [dbName={}] [dbFolder={}]", dbName, dbFolder);
            Map<Class<?>, EntityLines> elMap = entityParser.formatData(dataMap);
            // Delete all existing files
            List<Path> dbPaths = JkFiles.findFiles(dbFolder, false, Files::isRegularFile, p -> JkFiles.getFileName(p).startsWith(dbName));
            for(Path p : dbPaths) {
                Files.delete(p);
                logger.debug("Deleted file {}", p);
            }
            for (EntityLines el : elMap.values()) {
                JkFiles.writeFile(createRepoPath(el.getEntityClazz(), true), el.getEntityLines(), false);
                JkFiles.writeFile(createRepoPath(el.getEntityClazz(), false), el.getDepLines(), false);
                logger.debug("Persisted entity {}", el.getEntityClazz().getName());
            }

        } catch(IOException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    public TreeSet<JkEntity> getData(Class<?> dataClazz) {
        TreeSet<JkEntity> data = dataMap.get(dataClazz);
        if(data == null) {
            throw new JkRuntimeException("Class {} does not belong to package {}", dataClazz.getName(), pkgToScan);
        }
        return data;
    }

    private EntityLines readRepoFile(Class<?> clazz) {
        try {
            EntityLines el = new EntityLines(clazz);

            Path entityPath = createRepoPath(clazz, true);
            if(Files.exists(entityPath)) {
                List<String> lines = Files.readAllLines(entityPath);
                lines.removeIf(StringUtils::isBlank);
                el.getEntityLines().addAll(lines);
            }

            Path depPath = createRepoPath(clazz, false);
            if(Files.exists(depPath)) {
                List<String> lines = Files.readAllLines(depPath);
                lines.removeIf(StringUtils::isBlank);
                el.getDepLines().addAll(lines);
            }

            return el;

        } catch(IOException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private Path createRepoPath(Class<?> clazz, boolean isEntity) {
        String fname = dbName + FILENAME_SEP;
        fname += isEntity ? PH_ENTITY : PH_DEPENDENCIES;
        fname += FILENAME_SEP + clazz.getName() + "." + FILENAME_EXT;
        return dbFolder.resolve(fname);
    }


}
