package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.RepoEntity;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.repository.config.RepoConfig.Separator.SEP_FIELD;

class RepoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(RepoDAO.class);

    private static final String EXT_DATA_FILE = "data";
    private static final String EXT_DEPS_FILE = "fkeys";
    private static final String EXT_CLASS_DESCR_FILE = "descr";

    private static final String SEP_DESCR = ":";

    protected Path dbFolder;
    protected String dbName;
    protected List<ClazzWrapper> clazzWrappers;

    RepoDAO(Path dbFolder, String dbName, List<ClazzWrapper> clazzWrappers) {
        this.dbFolder = dbFolder;
        this.dbName = dbName;
        this.clazzWrappers = clazzWrappers;
    }

    public List<RepoDTO> readRepoData() {
        JkTimer timer = new JkTimer();
        List<RepoDTO> toRet = new ArrayList<>();

        for(ClazzWrapper rc : clazzWrappers) {
            RepoDTO dto = new RepoDTO(rc.getEClazz());
            toRet.add(dto);

            // 1. Read file .descr
            List<String> descrList = loadFieldsDescr(rc);
            if(descrList != null) {
                // 2. Read file .data
                dto.setEntities(loadData(rc, descrList));
                // 3. Read file .fkeys
                dto.setForeignKeys(loadForeignKeys(rc, descrList));
            }
        }

        LOG.debug("Repo readed in {}", timer.toStringElapsed());

        return toRet;
    }

    public void saveRepoData(List<RepoDTO> dtoList) {
        // Format repo data
        Map<Path, List<String>> formatData = new HashMap<>();

        for(RepoDTO dto : dtoList) {
            List<String> descrList = new ArrayList<>();
            List<String> fkLines = new ArrayList<>();
            List<String> dataLines = new ArrayList<>();

            ClazzWrapper rc = ClazzWrapper.get(dto.getEClazz());

            formatData.put(filePathDescr(rc), descrList);
            formatData.put(filePathData(rc), dataLines);
            formatData.put(filePathForeignKeys(rc), fkLines);

            if(!dto.getEntities().isEmpty()) {
                List<String> fnames = new ArrayList<>();

                for (RepoEntity edto : dto.getEntities()) {
                    List<Pair<String, String>> pairs = rc.formatEntity(edto);
                    dataLines.add(JkStreams.join(pairs, SEP_FIELD, Pair::getValue));
                    if(descrList.isEmpty()) {
                        descrList.addAll(JkStreams.map(pairs, p -> strf("{}{}{}", p.getKey(), SEP_DESCR, rc.getEntityField(p.getKey()).getFieldType().getName())));
                        fnames.addAll(JkStreams.map(pairs, Pair::getKey));
                    }
                }

                if(!dto.getForeignKeys().isEmpty()) {
                    for (RepoFK fk : dto.getForeignKeys()) {
                        Integer findex = fnames.indexOf(fk.getFieldName());
                        fkLines.add(strf("{}{}{}{}{}", fk.getFromID(), SEP_FIELD, findex, SEP_FIELD, fk.getDepID()));
                    }
                }
            }
        }

        // Delete all existing repo files
        formatData.keySet().forEach(JkFiles::delete);

        // Persist repo data (non-empty)
        formatData.forEach((p,l) -> {
            if(!l.isEmpty()) {
                saveRepoFile(p, l);
            }
        });
    }


    private List<String> loadFieldsDescr(ClazzWrapper clazzWrapper) {
        Path fpath = filePathDescr(clazzWrapper);
        List<String> lines = readRepoFile(fpath);
        return JkStreams.map(lines, l -> JkStrings.splitArr(l, SEP_DESCR)[0]);
    }
    private List<RepoEntity> loadData(ClazzWrapper clazzWrapper, List<String> descrList) {
        Path fpath = filePathData(clazzWrapper);
        List<String> lines = readRepoFile(fpath);
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
        List<String> lines = readRepoFile(fpath);
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

    protected List<String> readRepoFile(Path sourcePath) {
        if(!Files.exists(sourcePath)) {
            return Collections.emptyList();
        } else {
            return JkFiles.readLinesNotBlank(sourcePath);
        }
    }
    protected void saveRepoFile(Path outputPath, List<String> lines) {
        JkFiles.writeFile(outputPath, lines);
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

}
