package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.RepoEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.repository.common.RepoCommon.Separator.*;

class RepoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(RepoDAO.class);

    private static final String EXT_DATA_FILE = "data";
    private static final String EXT_DEPS_FILE = "fkeys";
    private static final String EXT_CLASS_DESCR_FILE = "descr";

    private static final String SEP_DESCR = ":";

    private Path dbFolder;
    private String dbName;
    private List<ClazzWrapper> clazzWrappers;

    RepoDAO(Path dbFolder, String dbName, List<ClazzWrapper> clazzWrappers) {
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.clazzWrappers = clazzWrappers;
    }

    public List<RepoDTO> readRepoData() {
        List<RepoDTO> toRet = new ArrayList<>();

        for(ClazzWrapper rc : clazzWrappers) {
            RepoDTO dto = new RepoDTO(rc.getEClazz());
            toRet.add(dto);

            // 1. Read file .descr
            List<String> descrList = loadDescr(rc);
            if(descrList != null) {
                // 2. Read file .data
                dto.setEntities(loadData(rc, descrList));
                // 3. Read file .fkeys
                dto.setForeignKeys(loadForeignKeys(rc, descrList));
            }
        }

        return toRet;
    }

    public void saveRepoData(List<RepoDTO> dtoList) {
        // Format repo data
        Map<Path, List<String>> formatData = new HashMap<>();
        for(RepoDTO dto : dtoList) {
            if(!dto.getEntities().isEmpty()) {
                ClazzWrapper rc = ClazzWrapper.wrap(dto.getEClazz());
                List<String> descrList = new ArrayList<>();
                List<String> dataLines = new ArrayList<>();
                for (RepoEntity edto : dto.getEntities()) {
                    List<Pair<String, String>> pairs = rc.formatEntity(edto);
                    dataLines.add(JkStreams.join(pairs, SEP_FIELD, Pair::getValue));
                    if(descrList.isEmpty()) {
                        descrList.addAll(JkStreams.map(pairs, Pair::getKey));
                    }
                }
                formatData.put(filePathDescr(rc), descrList);
                formatData.put(filePathData(rc), dataLines);


                if(!dto.getForeignKeys().isEmpty()) {
                    List<String> fkLines = new ArrayList<>();
                    for (RepoFK fk : dto.getForeignKeys()) {
                        Integer findex = descrList.indexOf(fk.getFieldName());
                        fkLines.add(strf("{}{}{}{}{}", fk.getFromID(), SEP_FIELD, findex, SEP_FIELD, fk.getDepID()));
                    }
                    formatData.put(filePathForeignKeys(rc), fkLines);
                }
            }
        }

        // Delete all existing repo files
        List<Path> existingRepoFiles = JkFiles.findFiles(dbFolder, false, this::isRepoEntityFile);
        existingRepoFiles.forEach(JkFiles::delete);

        // Persist repo data
        formatData.forEach(JkFiles::writeFile);
    }

    private List<String> loadDescr(ClazzWrapper clazzWrapper) {
        Path fpath = filePathDescr(clazzWrapper);
        if(!Files.exists(fpath))    return null;
        return JkFiles.readLinesNotBlank(fpath);
    }
    private List<RepoEntity> loadData(ClazzWrapper clazzWrapper, List<String> descrList) {
        Path fpath = filePathData(clazzWrapper);
        if(!Files.exists(fpath))    return Collections.emptyList();

        List<String> lines = JkFiles.readLinesNotBlank(fpath);
        List<RepoEntity> toRet = new ArrayList<>();
        for(String l : lines) {
            Map<String, String> strValues = new HashMap<>();
            String[] split = JkStrings.splitArr(l, SEP_FIELD);
            for(int i = 0; i < split.length; i++) {
                String fname = descrList.get(i);
                if(fname != null) {
                    strValues.put(fname, split[i]);
                }
            }
            RepoEntity repoEntity = clazzWrapper.parseEntity(strValues);
            toRet.add(repoEntity);
        }
        return toRet;
    }
    private List<RepoFK> loadForeignKeys(ClazzWrapper clazzWrapper, List<String> descrList) {
        Path fpath = filePathForeignKeys(clazzWrapper);
        if(!Files.exists(fpath))    return Collections.emptyList();

        List<String> lines = JkFiles.readLinesNotBlank(fpath);
        List<RepoFK> toRet = new ArrayList<>();
        for(String l : lines) {
            String[] split = JkStrings.splitArr(l, SEP_FIELD);
            long fromID = Long.valueOf(split[0]);
            int fieldNum = Integer.parseInt(split[1]);
            long depID = Long.valueOf(split[2]);
            String fname = descrList.get(fieldNum);
            toRet.add(new RepoFK(fromID, fname, depID));
        }
        return toRet;
    }

    private Path filePathDescr(ClazzWrapper clazzWrapper) {
        return filePathRepoEntity(clazzWrapper, EXT_CLASS_DESCR_FILE);
    }
    private Path filePathData(ClazzWrapper clazzWrapper) {
        return filePathRepoEntity(clazzWrapper, EXT_DATA_FILE);
    }
    private Path filePathForeignKeys(ClazzWrapper clazzWrapper) {
        return filePathRepoEntity(clazzWrapper, EXT_DEPS_FILE);
    }
    private Path filePathRepoEntity(ClazzWrapper clazzWrapper, String extension) {
        String fname = strf("{}#{}#jkrepo.{}", dbName, clazzWrapper.getEClazz().getName(), extension);
        return dbFolder.resolve(fname);
    }

    private boolean isRepoEntityFile(Path p) {
        if(!Files.isRegularFile(p)) return false;
        return p.getFileName().toString().matches("^"+dbName+"#[^#]*#jkrepo\\.[^.#]+$");
    }

}
