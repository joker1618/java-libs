package xxx.joker.libs.datalayer.wrapper;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.datalayer.config.RepoConfig;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.design.EntityCreationTm;
import xxx.joker.libs.datalayer.design.EntityID;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;
import xxx.joker.libs.datalayer.exceptions.RepoError;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import static xxx.joker.libs.datalayer.config.RepoConfig.CsvSep.SEP_FIELD;

public class ClazzWrap {

    private final Class<?> eClazz;
    private final LinkedHashMap<String, FieldWrap> fieldByNameMap;
    private FieldWrap entityIdFieldWrap;
    private FieldWrap creationTmFieldWrap;

    public ClazzWrap(Class<?> eClazz) {
        this.eClazz = eClazz;
        this.fieldByNameMap = new LinkedHashMap<>();
        initClazz();
    }

    public List<RepoEntity> parseEntityData(List<String> lines, RepoCtx ctx) {
        List<RepoEntity> elist = new ArrayList<>();
        if(!lines.isEmpty()) {
            List<String> header = JkStrings.splitList(lines.get(0), SEP_FIELD);
            for(int i = 1; i < lines.size(); i++) {
                RepoEntity e = (RepoEntity) JkReflection.createInstance(eClazz);
                String[] row = JkStrings.splitArr(lines.get(i), SEP_FIELD);
                fieldByNameMap.forEach((fn,fw) -> {
                    if(!fw.isEntityFlat()) {
                        int col = header.indexOf(fn);
                        if (col != -1) {
                            fw.parseAndSetValue(e, row[col]);
                        }
                    }
                    fw.fillDefaultValues(e);
                });
                elist.add(e);
            }
        }
        fieldByNameMap.values().stream().filter(FieldWrap::isRepoResourcePath).forEach(fw ->
            elist.forEach(e -> fw.setValue(e, ctx.getResourcesFolder().resolve((Path)fw.getValue(e))))
        );
        return elist;
    }

    public List<String> formatEntityData(List<RepoEntity> entities, RepoCtx ctx) {
        List<String> toRet = new ArrayList<>();

        // Print primary key (_epk):
        // - after the entityId   (entityId|_epk)
        // - after the creationTm if this come just after entityId (entityId|creationTm|_epk)
        List<String> fnames = JkConvert.toList(fieldByNameMap.keySet());
        int idxId = fnames.indexOf(entityIdFieldWrap.getFieldName());
        int idxTm = fnames.indexOf(creationTmFieldWrap.getFieldName());
        int epkIndex = 1 + (idxTm == idxId + 1 ? idxTm : idxId);
        fnames.add(epkIndex, "_epk");
        String header = JkStreams.join(fnames, SEP_FIELD);
        toRet.add(header);

        Stream<RepoEntity> sorted = entities.stream().sorted(Comparator.comparing(RepoEntity::getEntityId));
        sorted.forEach(e -> {
            List<String> rowValues = JkStreams.map(fieldByNameMap.values(), fw -> {
                if (!fw.isRepoResourcePath()) {
                    return fw.formatValue(e);
                } else {
                    Path absPath = Paths.get(fw.formatValue(e)).toAbsolutePath();
                    return ctx.getResourcesFolder().relativize(absPath).toString();
                }
            });
            rowValues.add(epkIndex, e.getPrimaryKey());
            toRet.add(JkStreams.join(rowValues, SEP_FIELD));
        });

        return toRet;
    }

    public void initEntityFields(RepoEntity e) {
        getFieldWraps().forEach(fw -> fw.fillDefaultValues(e));
    }

    public List<FieldWrap> getFieldWraps() {
        return new ArrayList<>(fieldByNameMap.values());
    }
    public List<FieldWrap> getFieldWraps(Class<?> flatClazz) {
        return JkStreams.filter(fieldByNameMap.values(), fw -> fw.getFieldTypeFlat() == flatClazz);
    }
    public FieldWrap getFieldWrap(String fieldName) {
        return fieldByNameMap.get(fieldName);
    }

    public List<FieldWrap> getFieldWrapsEntityFlat() {
        return JkStreams.filter(fieldByNameMap.values(), FieldWrap::isEntityFlat);
    }
    public List<FieldWrap> getCollFieldWrapsEntity() {
        return JkStreams.filter(fieldByNameMap.values(), FieldWrap::isCollection, FieldWrap::isEntityColl);
    }

    public Class<?> getEClazz() {
        return eClazz;
    }

    private void initClazz() {
        // exactly 1 field @EntityID
        List<Field> fields = fields = JkReflection.getFieldsByAnnotation(eClazz, EntityID.class);
        if(fields.size() != 1) {
            throw new RepoError("Must be present exactly one field annotated with @EntityID. [class={}]", eClazz.getSimpleName());
        }
        entityIdFieldWrap = new FieldWrap(fields.get(0));

        // exactly 1 field @EntityCreationTm
        fields = JkReflection.getFieldsByAnnotation(eClazz, EntityCreationTm.class);
        if(fields.size() != 1) {
            throw new RepoError("Must be present exactly one field annotated with @EntityCreationTm. [class={}]", eClazz.getSimpleName());
        }
        creationTmFieldWrap = new FieldWrap(fields.get(0));

        // Add field entityID and creationTm
        fieldByNameMap.put(entityIdFieldWrap.getFieldName(), entityIdFieldWrap);
        fieldByNameMap.put(creationTmFieldWrap.getFieldName(), creationTmFieldWrap);

        // Add all @RepoField fields
        fields = JkReflection.getFieldsByAnnotation(eClazz, RepoField.class);
        if(fields.isEmpty()) {
            throw new RepoError("No fields annotated with '@RepoField' found in class {}", eClazz.getSimpleName());
        }
        fields.forEach(ff -> fieldByNameMap.put(ff.getName(), new FieldWrap(ff)));

        // Check field class type
        fieldByNameMap.forEach((k, v) -> {
            if(!RepoConfig.isValidType(v)) {
                throw new RepoError("Invalid field type [class={}, field={}, type={}]",
                        eClazz.getSimpleName(), k, v.getFieldType().getSimpleName()
                );
            }
            if(v.isFinal()) {
                throw new RepoError("Final field not allowed [class={}, field={}]", eClazz.getSimpleName(), k);
            }
        });
    }
}


