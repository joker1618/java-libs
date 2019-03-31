package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.common.RepoCommon;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ClazzWrapper {

    private static final Map<Class<?>, ClazzWrapper> CACHE = new HashMap<>();
    private static final FieldWrapper CF_ENTITY_ID;
    static {
        Field f = JkReflection.getFieldByName(RepoEntity.class, RepoCommon.FIELD_NAME_REPO_ENTITY_ID);
        CF_ENTITY_ID = new FieldWrapper(f);
    }

    private final Class<?> eClazz;
    private Map<String, FieldWrapper> fieldsByName;

    private ClazzWrapper(Class<?> eClazz) {
        this.eClazz = eClazz;
        this.fieldsByName = new HashMap<>();
        checkRepoClazz();
    }

    public static void setEntityID(RepoEntity e, Long eID) {
        CF_ENTITY_ID.setValue(e, eID);
    }

    public static synchronized ClazzWrapper wrap(Class<?> clazz) {
        ClazzWrapper clazzWrapper = CACHE.get(clazz);
        if(clazzWrapper == null) {
            clazzWrapper = new ClazzWrapper(clazz);
            CACHE.put(clazz, clazzWrapper);
        }
        return clazzWrapper;
    }

    public RepoEntity parseEntity(Map<String, String> strValues) {
        RepoEntity instance = (RepoEntity) JkReflection.createInstanceSafe(eClazz);
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
        fieldsByName.forEach((fname,cf) -> {
            Pair<String, String> pair = Pair.of(fname, cf.formatValue(e));
            if(RepoCommon.FIELD_NAME_REPO_ENTITY_ID.equals(fname)) {
                toRet.add(0, pair);
            } else {
                toRet.add(pair);
            }
        });
        return toRet;
    }

    public List<FieldWrapper> getEntityFields() {
        return new ArrayList<>(fieldsByName.values());
    }

    public FieldWrapper getEntityField(String fieldName) {
        return fieldsByName.get(fieldName);
    }

    public Class<?> getEClazz() {
        return eClazz;
    }

    private void checkRepoClazz() {
        // At least 1 RepoField present
        List<Field> fields = JkReflection.getFieldsByAnnotation(eClazz, RepoField.class);
        if(fields.isEmpty()) {
            throw new RepoError("No fields annotated with '@{}' found in class {}", RepoField.class.getSimpleName(), eClazz.getSimpleName());
        }
        fields.forEach(f -> fieldsByName.put(f.getName(), new FieldWrapper(f)));

        // Check field class type
        fieldsByName.forEach((k,v) -> {
            if(!RepoCommon.isValidType(v.getFieldType())) {
                throw new RepoError("Invalid field type {}::{}", eClazz.getSimpleName(), k);
            }
        });

        // Add field 'RepoEntity.entityID'
        fieldsByName.put(CF_ENTITY_ID.getFieldName(), CF_ENTITY_ID);
    }

}