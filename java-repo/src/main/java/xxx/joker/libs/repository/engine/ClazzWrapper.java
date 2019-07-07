package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.config.RepoConfig;
import xxx.joker.libs.repository.config.RepoCtx;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoEntityCreationTm;
import xxx.joker.libs.repository.design.RepoEntityID;
import xxx.joker.libs.repository.design.RepoField;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ClazzWrapper {

    private final Map<Class<?>, List<FieldWrapper>> REFERENCES;

    private final RepoCtx ctx;
    private final Class<?> eClazz;
    private Map<String, FieldWrapper> fieldsByName;

    public ClazzWrapper(Class<?> eClazz, RepoCtx ctx) {
        this.eClazz = eClazz;
        this.ctx = ctx;
        this.fieldsByName = new LinkedHashMap<>();
        initClazz();

        List<FieldWrapper> fwList = ctx.getEClasses().values().stream()
                .flatMap(cw -> cw.getEntityFields().stream())
                .filter(fw -> fw.typeOfFlat(getEClazz()))
                .collect(Collectors.toList());
        REFERENCES = JkStreams.toMap(fwList, fw -> fw.getField().getDeclaringClass());
    }

    public RepoEntity parseEntity(Map<String, String> strValues) {
        RepoEntity instance = (RepoEntity) JkReflection.createInstance(eClazz);
        for(String mapFName : strValues.keySet()) {
            FieldWrapper cf = fieldsByName.get(mapFName);
            if(cf != null) {
                cf.parseAndSetValue(instance, strValues.get(mapFName));
            }
        }
        return instance;
    }

    public List<Pair<String,String>> formatEntity(RepoEntity e) {
        List<Pair<String,String>> toRet = new ArrayList<>();
        fieldsByName.forEach((fname,cf) -> toRet.add(Pair.of(fname, cf.formatValue(e))));
        return toRet;
    }

    public List<FieldWrapper> getEntityFields() {
        return new ArrayList<>(fieldsByName.values());
    }

    public FieldWrapper getEntityField(String fieldName) {
        return fieldsByName.get(fieldName);
    }

    public Map<Class<?>, List<FieldWrapper>> getReferenceFields() {
        return REFERENCES;
    }

    public Class<?> getEClazz() {
        return eClazz;
    }

    private void initClazz() {
        // At least 1 RepoField present
        List<Field> fields = JkReflection.getFieldsByAnnotation(eClazz, RepoField.class);
        if(fields.isEmpty()) {
            throw new RepoError("No fields annotated with '@{}' found in class {}", RepoField.class.getSimpleName(), eClazz.getSimpleName());
        }

        Field f = JkReflection.getFieldByAnnotation(RepoEntity.class, RepoEntityID.class);
        FieldWrapper fwID = new FieldWrapper(f, ctx);
        Field f2 = JkReflection.getFieldByAnnotation(RepoEntity.class, RepoEntityCreationTm.class);
        FieldWrapper fwTm = new FieldWrapper(f2, ctx);

        // Add field 'RepoEntity.entityID'
        fieldsByName.put(fwID.getFieldName(), fwID);
        // Add all @RepoField fields
        fields.forEach(ff -> fieldsByName.put(ff.getName(), new FieldWrapper(ff, ctx)));
        // Add field 'RepoEntity.creationTm'
        fieldsByName.put(fwTm.getFieldName(), fwTm);

        // Check field class type
        fieldsByName.forEach((k,v) -> {
            if(!RepoConfig.isValidType(v)) {
                throw new RepoError("Invalid field type {}::{}", eClazz.getSimpleName(), k);
            }
        });
    }

}


