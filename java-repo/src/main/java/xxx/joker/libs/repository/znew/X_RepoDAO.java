package xxx.joker.libs.repository.znew;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.files.JkZip;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design2.RepoEntity;

import javax.management.AttributeList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.repository.znew.X_RepoConst.Separator.*;

class X_RepoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(X_RepoDAO.class);

    private static final String EXT_DATA_FILE = "data";
    private static final String EXT_DEPS_FILE = "fkeys";
    private static final String EXT_CLASS_DESCR_FILE = "descr";

    private static final String SEP_DESCR = ":";

    private Path dbFolder;
    private String dbName;
    private List<X_RepoClazz> repoClazzes;

    X_RepoDAO(Path dbFolder, String dbName, List<X_RepoClazz> repoClazzes) {
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.repoClazzes = repoClazzes;
    }

    public List<X_RepoEntityDTO> readRepoData() {
        List<X_RepoEntityDTO> toRet = new ArrayList<>();

        for(X_RepoClazz rc : repoClazzes) {
            X_RepoEntityDTO dto = new X_RepoEntityDTO(rc.getEClazz());
            toRet.add(dto);

            // 1. Read file .descr
            Map<Integer, String> descrMap = loadDescr(rc);
            if(descrMap != null) {
                // 2. Read file .data
                dto.setEntities(loadData(rc, descrMap));
                // 3. Read file .fkeys
                dto.setForeignKeys(loadForeignKeys(rc, descrMap));
            }
        }

        return toRet;
    }


    public void saveRepoData(List<X_RepoEntityDTO> dtoList) {
        // Get all existing repo files
        List<Path> dbFiles = JkFiles.findFiles(dbFolder, false, this::isRepoEntityFile);

        // Create lock file
        Path repoLockPath = filePathRepoLock();
        JkFiles.writeFile(repoLockPath, "");

        // Backup existing repo files
        backupRepoFiles(dbFiles);

        // Delete existing repo files
        dbFiles.forEach(JkFiles::delete);

        // Persist repo data
        for(X_RepoEntityDTO dto : dtoList) {
            if(!dto.getEntities().isEmpty()) {
                X_RepoClazz rc = X_RepoClazz.wrap(dto.getEClazz());
                TreeMap<String, Integer> descrMap = new TreeMap<>();
                List<String> dataLines = new ArrayList<>();
                for (RepoEntity edto : dto.getEntities()) {
                    List<Pair<String, String>> pairs = rc.formatEntity(edto);
                    dataLines.add(JkStreams.join(pairs, SEP_FIELD, Pair::getValue));
                    if(descrMap.isEmpty()) {
                        List<String> fnames = JkStreams.map(pairs, Pair::getKey);
                        for(int i = 0; i < fnames.size(); i++) {
                            descrMap.put(fnames.get(i), i);
                        }
                    }
                }
                JkFiles.writeFile(filePathDescr(rc), JkConvert.toArrayList(descrMap.keySet()));
                JkFiles.writeFile(filePathData(rc), dataLines);

                List<String> fkLines = new ArrayList<>();
                for (X_RepoFK fk : dto.getForeignKeys()) {
                    Integer findex = descrMap.get(fk.getFieldName());
                    fkLines.add(strf("{}{}{}{}{}", fk.getFromID(), SEP_FIELD, findex, SEP_FIELD, fk.getDepID()));
                }
                JkFiles.writeFile(filePathForeignKeys(rc), fkLines);
            }
        }

        // Delete lock file
        JkFiles.delete(repoLockPath);
    }

    private void backupRepoFiles(List<Path> dbFiles) {
        String zipName = strf("{}.backup.{}.zip", dbName, System.currentTimeMillis());
        JkZip.zipFiles(dbFolder.resolve(zipName), dbFiles);
        List<Path> oldBackupFiles = JkFiles.findFiles(dbFolder, false, p -> p.getFileName().toString().startsWith(dbName + ".backup") && !p.getFileName().toString().equals(zipName));
        oldBackupFiles.forEach(JkFiles::delete);
    }

    private Map<Integer, String> loadDescr(X_RepoClazz repoClazz) {
        Path fpath = filePathDescr(repoClazz);
        if(!Files.exists(fpath))    return null;

        List<String> lines = JkFiles.readLinesNotBlank(fpath);
        return JkStreams.toMapSingle(
                lines,
                l -> JkConvert.toInt(l.split(SEP_DESCR)[0]),
                l -> l.split(SEP_DESCR)[1]
        );
    }
    private void saveDescr(X_RepoClazz rc, RepoEntity e) {
        List<String> fnames = JkStreams.map(rc.formatEntity(e), Pair::getKey);
        List<String> lines = new ArrayList<>();
        for(int i = 0; i < fnames.size(); i++) {
            lines.add(strf("{}{}{}", i, SEP_DESCR, fnames.get(i)));
        }
        JkFiles.writeFile(filePathDescr(rc), lines);
    }

    private List<RepoEntity> loadData(X_RepoClazz repoClazz, Map<Integer, String> descrMap) {
        Path fpath = filePathData(repoClazz);
        if(!Files.exists(fpath))    return Collections.emptyList();

        List<String> lines = JkFiles.readLinesNotBlank(fpath);
        List<RepoEntity> toRet = new ArrayList<>();
        for(String l : lines) {
            Map<String, String> strValues = new HashMap<>();
            String[] split = JkStrings.splitArr(l, SEP_FIELD);
            for(int i = 0; i < split.length; i++) {
                String fname = descrMap.get(i);
                if(fname != null) {
                    strValues.put(fname, split[i]);
                }
            }
            RepoEntity repoEntity = repoClazz.parseEntity(strValues);
            toRet.add(repoEntity);
        }
        return toRet;
    }
    private void saveData(X_RepoClazz rc, List<RepoEntity> elist) {
        List<String> lines = new ArrayList<>();
        for (RepoEntity e : elist) {
            lines.add(JkStreams.join(rc.formatEntity(e), SEP_FIELD, Pair::getValue));
        }
        JkFiles.writeFile(filePathData(rc), lines);
    }

    private List<X_RepoFK> loadForeignKeys(X_RepoClazz repoClazz, Map<Integer, String> descrMap) {
        Path fpath = filePathForeignKeys(repoClazz);
        if(!Files.exists(fpath))    return Collections.emptyList();

        List<String> lines = JkFiles.readLinesNotBlank(fpath);
        List<X_RepoFK> toRet = new ArrayList<>();
        for(String l : lines) {
            String[] split = JkStrings.splitArr(l, SEP_FIELD);
            long fromID = Long.valueOf(split[0]);
            int fieldNum = Integer.parseInt(split[1]);
            long depID = Long.valueOf(split[2]);
            String fname = descrMap.get(fieldNum);
            toRet.add(new X_RepoFK(fromID, fname, depID));
        }
        return toRet;
    }

    private Path filePathRepoLock() {
        return dbFolder.resolve(dbName+".jkrepo.lock");
    }
    private Path filePathDescr(X_RepoClazz repoClazz) {
        return filePathRepoEntity(repoClazz, EXT_CLASS_DESCR_FILE);
    }
    private Path filePathData(X_RepoClazz repoClazz) {
        return filePathRepoEntity(repoClazz, EXT_DATA_FILE);
    }
    private Path filePathForeignKeys(X_RepoClazz repoClazz) {
        return filePathRepoEntity(repoClazz, EXT_DEPS_FILE);
    }
    private Path filePathRepoEntity(X_RepoClazz repoClazz, String extension) {
        String fname = strf("{}#{}#jkrepo.{}", dbName, repoClazz.getEClazz().getName(), extension);
        return dbFolder.resolve(fname);
    }

    private boolean isRepoEntityFile(Path p) {
        if(!Files.isRegularFile(p)) return false;
        return p.getFileName().toString().matches("^"+dbName+"#[^#]*#jkrepo\\.[^.#]+$");
    }
}
