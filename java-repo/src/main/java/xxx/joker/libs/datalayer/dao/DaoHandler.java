package xxx.joker.libs.datalayer.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.files.JkZip;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.datalayer.config.RepoConfig;
import xxx.joker.libs.datalayer.config.RepoConfig.CsvSep;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.wrapper.ClazzWrap;
import xxx.joker.libs.datalayer.wrapper.FieldWrap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static xxx.joker.libs.core.utils.JkConvert.toList;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class DaoHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DaoHandler.class);

    private final RepoCtx ctx;


    public DaoHandler(RepoCtx ctx) {
        this.ctx = ctx;
    }

    public List<RepoEntity> loadDataFromFiles() {
        List<RepoEntity> toRet = new ArrayList<>();

        // Read data files, without dependencies
        for (ClazzWrap cw : ctx.getClazzWraps().values()) {
            List<String> lines = readRepoFile(ctx.getEntityDataPath(cw));
            toRet.addAll(cw.parseEntityData(lines));
        }

        // Read dependencies
        String header = RepoConfig.getRepoFileFkeysHeader();
        List<String> lines = readRepoFile(ctx.getForeignKeysPath());
        lines.remove(header);
        List<DaoFK> fkList = JkStreams.map(lines, line -> new DaoFK().parse(line));

        // Set dependencies using simple collection (no Proxy)
        Map<Long, RepoEntity> idMap = JkStreams.toMapSingle(toRet, RepoEntity::getEntityId);
        Map<Long, List<DaoFK>> fkMap = JkStreams.toMap(fkList, DaoFK::getFromID);

        fkMap.forEach((sid, fkl) -> {
            RepoEntity fromEntity = idMap.get(sid);
            fkl.forEach(fk -> {
                RepoEntity depEntity = idMap.get(fk.getDepID());
                ClazzWrap cw = ctx.getClazzWraps().get(fromEntity.getClass());
                FieldWrap fw = cw.getFieldWrap(fk.getFieldName());
                if(fw.isCollection()) {
                    fw.addToColl(fromEntity, depEntity);
                } else {
                    fw.setValue(fromEntity, depEntity);
                }
            });
        });

        return toRet;
    }

    public boolean persistData(Collection<RepoEntity> entities) {
        // Move all existing DB files to a temporary sub folder, so if something goes wrong, there is a backup
        Path bkpFolder = ctx.getDbFolder().resolve(".backupMove");
        List<Path> oldDbFiles = JkFiles.find(ctx.getDbFolder(), false, ctx::isEntityFilePath);
        oldDbFiles.forEach(op -> JkFiles.moveInFolder(op, bkpFolder));

        // Persist entities
        List<RepoEntity> idSorted = JkStreams.sorted(entities, Comparator.comparing(RepoEntity::getEntityId));
        Map<Class<?>, List<RepoEntity>> toFormatMap = JkStreams.toMap(idSorted, RepoEntity::getClass);
        List<DaoFK> fkList = new ArrayList<>();
        toFormatMap.forEach((c,reList) -> {
            if(!reList.isEmpty()) {
                ClazzWrap cw = ctx.getClazzWraps().get(c);
                List<String> dataLines = cw.formatEntityData(reList);
                Path outDataPath = ctx.getEntityDataPath(cw);
                JkFiles.writeFile(outDataPath, dataLines);
                LOG.debug("File persisted: {}", outDataPath);

                List<FieldWrap> fwDeps = cw.getFieldWrapsEntityFlat();
                reList.forEach(re -> {
                    fwDeps.forEach(fw -> {
                        if(fw.isEntity()) {
                            RepoEntity edep = fw.getValueCast(re);
                            if(edep != null) {
                                fkList.add(new DaoFK(re.getEntityId(), edep.getEntityId(), fw.getFieldName()));
                            }
                        } else if(fw.isEntityColl()) {
                            Collection<RepoEntity> coll = fw.getValueCast(re);
                            coll.forEach(
                                edep -> fkList.add(new DaoFK(re.getEntityId(), edep.getEntityId(), fw.getFieldName()))
                            );
                        }
                    });
                });
            }
        });

        if(!fkList.isEmpty()) {
            List<String> fkLines = JkStreams.map(fkList, DaoFK::format);
            fkLines.add(0, RepoConfig.getRepoFileFkeysHeader());
            Path outFkeysPath = ctx.getForeignKeysPath();
            JkFiles.writeFile(outFkeysPath, fkLines);
            LOG.debug("File persisted: {}", outFkeysPath);
        }

        JkFiles.delete(bkpFolder);

        return true;
    }

    private List<String> readRepoFile(Path sourcePath) {
        if (!Files.exists(sourcePath)) {
            return Collections.emptyList();
        } else {
            return JkFiles.readLinesNotBlank(sourcePath);
        }
    }


}
