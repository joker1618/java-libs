package xxx.joker.libs.repository.znew;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.design2.RepoField;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class X_RepoClazz {

    private static final Map<Class<?>, X_RepoClazz> CACHE = new HashMap<>();
    private static final ClazzField CF_ENTITY_ID;
    static {
        Field f = JkReflection.getFieldByName(RepoEntity.class, X_RepoConst.FIELD_NAME_REPO_ENTITY_ID);
        CF_ENTITY_ID = new ClazzField(f);
    }

    private final Class<?> eClazz;
    private Map<String, ClazzField> fieldsByName;

    private X_RepoClazz(Class<?> eClazz) {
        this.eClazz = eClazz;
        this.fieldsByName = new HashMap<>();
        checkRepoClazz();
    }

    public static void setEntityID(RepoEntity e, Long eID) {
        CF_ENTITY_ID.setValue(e, eID);
    }

    public static synchronized X_RepoClazz wrap(Class<?> clazz) {
        X_RepoClazz repoClazz = CACHE.get(clazz);
        if(repoClazz == null) {
            repoClazz = new X_RepoClazz(clazz);
            CACHE.put(clazz, repoClazz);
        }
        return repoClazz;
    }

    public RepoEntity parseEntity(Map<String, String> strValues) {
        RepoEntity instance = (RepoEntity) JkReflection.createInstanceSafe(eClazz);
        for(String mapFName : strValues.keySet()) {
            ClazzField cf = fieldsByName.get(mapFName);
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
            if(X_RepoConst.FIELD_NAME_REPO_ENTITY_ID.equals(fname)) {
                toRet.add(0, pair);
            } else {
                toRet.add(pair);
            }
        });
        return toRet;
    }

    public List<ClazzField> getEntityFields() {
        return new ArrayList<>(fieldsByName.values());
    }

    public ClazzField getEntityField(String fieldName) {
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
        fields.forEach(f -> fieldsByName.put(f.getName(), new ClazzField(f)));

        // Check field class type
        fieldsByName.forEach((k,v) -> {
            if(!X_RepoConst.isValidType(v)) {
                throw new RepoError("Invalid field type {}::{}", eClazz.getSimpleName(), k);
            }
        });

        // Add field 'RepoEntity.entityID'
        fieldsByName.put(CF_ENTITY_ID.getFieldName(), CF_ENTITY_ID);
    }

}
