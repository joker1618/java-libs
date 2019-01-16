package xxx.joker.libs.repository.managers;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.config.RepoConfig;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;
import xxx.joker.libs.repository.exceptions.RepoDesignError;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class DesignService {

    private static final Logger logger = LoggerFactory.getLogger(DesignService.class);

    private static final String SEP_FIELD = "##FLD##";
    private static final String SEP_LIST = "##LST##";

    private static final String PH_TAB = "##TAB##";
    private static final String PH_NEWLINE = "##NLF##";


    private TreeMap<Class<?>, TreeMap<Integer, DesignField>> designMap;
    private RepoDataHandler repoDataHandler;

    public DesignService(Collection<Class<?>> classes) {
        this.designMap = parseEntityClasses(classes);
    }

    public TreeMap<Class<?>, Set<JkEntity>> parseLines(RepoLines repoLines) {
        Map<Long, JkEntity> idMap = new HashMap<>();
        Map<Class<?>, List<JkEntity>> dataMap = new HashMap<>();

        // Parse entities lines, no deps
        repoLines.getEntityLines().forEach((c,lines) -> {
            List<JkEntity> elist = JkStreams.map(lines, l -> parseEntityLine(c, l));
            dataMap.put(c, elist);
            List<JkEntity> dups = JkStreams.filter(elist, e -> idMap.containsKey(e.getEntityID()));
            if(dups.isEmpty()) {
                logger.warn("Something went wrong: class={}, ID dups={}", c.getSimpleName(), dups);
            }
            Map<Long, JkEntity> clazzIdMap = JkStreams.toMapSingle(elist, JkEntity::getEntityID);
            idMap.putAll(clazzIdMap);
        });

        // Parse and set entities deps
        List<ForeignKey> fkList = JkStreams.map(repoLines.getDepsLines(), l -> new ForeignKey(JkStrings.splitArr(l, SEP_FIELD)));
        Map<Long, List<ForeignKey>> idFromFKs = JkStreams.toMap(fkList, ForeignKey::getFromID);
        for(long idFrom : idFromFKs.keySet()) {
            JkEntity efrom = idMap.get(idFrom);
            Map<Integer, List<ForeignKey>> idxFields = JkStreams.toMap(idFromFKs.get(idFrom), ForeignKey::getFromFieldIdx);
            for(int idxFrom : idxFields.keySet()) {
                List<JkEntity> deps = JkStreams.map(idxFields.get(idxFrom), fk -> idMap.get(fk.getDepID()));
                DesignField dfield = designMap.get(efrom.getClass()).get(idxFrom);
                if(dfield.isCollection()) {
                    Object o = dfield.isSet() ? HandlerSet.createProxySet(repoDataHandler, deps) : HandlerList.createProxyList(repoDataHandler, deps);
                    dfield.setValue(efrom, o);
                } else if(!deps.isEmpty()) {
                    dfield.setValue(efrom, deps.get(0));
                }
            }
        }

        // Create dataSets using java Proxy
        TreeMap<Class<?>, Set<JkEntity>> toRet = new TreeMap<>();
        for(Class<?> c : dataMap.keySet()) {
            Set<JkEntity> proxySet = HandlerDataSet.createProxySet(repoDataHandler, dataMap.get(c));
            toRet.put(c, proxySet);
        }

        return toRet;
    }

    public RepoLines formatEntities(Map<Class<?>, Set<JkEntity>> entityMap) {
        //todo impl
        return null;
    }

    public List<Class<?>> getEntityClasses() {
        return JkConvert.toArrayList(designMap.keySet());
    }
    public Map<Class<?>, List<DesignField>> getEntityFields() {
        Map<Class<?>, List<DesignField>> entityFields = new HashMap<>();
        for(Class<?> key : designMap.keySet()) {
            List<DesignField> edfields = JkStreams.mapFilter(designMap.get(key).entrySet(), Map.Entry::getValue, DesignField::isFlatJkEntity);
            entityFields.put(key, edfields);
        }
        return entityFields;
    }

    public void setRepoDataHandler(RepoDataHandler repoDataHandler) {
        this.repoDataHandler = repoDataHandler;
    }

    private TreeMap<Class<?>, TreeMap<Integer, DesignField>> parseEntityClasses(Collection<Class<?>> classes) {
        List<String> dups = JkStreams.getDuplicates(JkStreams.map(classes, Class::getSimpleName));
        if(dups != null) {
            throw new RepoDesignError("all entity classes must have different class name. Duplicates: {}", dups);
        }

        TreeMap<Class<?>, TreeMap<Integer, DesignField>> toRet = new TreeMap<>();

        for(Class<?> clazz : classes) {
            List<Field> fields = JkReflection.getFieldsByAnnotation(clazz, JkEntityField.class);
            if(fields.isEmpty()) {
                throw new RepoDesignError("no JkEntityField annotated fields found in entity {}", clazz);
            }

            toRet.put(clazz, new TreeMap<>());

            List<DesignField> dfields = JkStreams.map(fields, DesignField::new);

            List<DesignField> negIdxs = JkStreams.filter(dfields, df -> df.getIdx() < 0);
            if(!negIdxs.isEmpty()) {
                throw new RepoDesignError("class {}: negative 'idx' {}", clazz, negIdxs);
            }
            List<Integer> idxDups = JkStreams.getDuplicates(JkStreams.map(dfields, DesignField::getIdx));
            if(!idxDups.isEmpty()) {
                throw new RepoDesignError("class {}: duplicated 'idx' {}", clazz, idxDups);
            }

            for (DesignField dfield : dfields) {
                String fieldName = dfield.getFieldName();
                Class<?> collType = dfield.getCollectionType();

                if(dfield.isCollection()) {
                    if(collType == Object.class) {
                        throw new RepoDesignError("field {}: collectionType not specified", fieldName);
                    }
                    if(dfield.isSet() && !dfield.isFlatFieldComparable()) {
                        throw new RepoDesignError("field {}: set must have comparable elements", fieldName);
                    }

                } else {
                    if(collType != Object.class) {
                        throw new RepoDesignError("field {}: collection type not allowed", fieldName);
                    }
                }

                Class<?> toCheck = dfield.getFlatFieldType();
                if(!RepoConfig.isFieldClassAllowed(toCheck)) {
                    throw new RepoDesignError("field {}: class type {} not allowed", fieldName, toCheck);
                }

                toRet.get(clazz).put(dfield.getIdx(), dfield);
            }
        }

        return toRet;
    }

    // Skip field of type JkEntity, and list/set of JkEntity
    private JkEntity parseEntityLine(Class<?> elemClazz, String repoLine) {
        JkEntity instance = (JkEntity) JkReflection.createInstanceSafe(elemClazz);
        List<String> row = JkStrings.splitList(repoLine, SEP_FIELD);

        String entityID = row.remove(0);
        instance.setEntityID(JkConvert.toLong(entityID));
        String insTstamp = row.remove(0);
        instance.setInsertTstamp(LocalDateTime.parse(insTstamp, DateTimeFormatter.ISO_DATE_TIME));

        for (Map.Entry<Integer, DesignField> entry : designMap.get(elemClazz).entrySet()) {
            if(!entry.getValue().isFlatJkEntity()) {
                if (entry.getKey() < row.size()) {
                    Object o = fromStringValue(row.get(entry.getKey()), entry.getValue());
                    entry.getValue().setValue(instance, o);
                }
            }
        }

        return instance;
    }
    private Object fromStringValue(String value, DesignField dfield) {
        Object retVal = null;

        if(!dfield.isFlatJkEntity()) {
            // Parse repoLine to correct Object
            if (dfield.isCollection()) {
                Class<?> elemClazz = dfield.getCollectionType();
                List<String> strElems = JkStrings.splitList(value, SEP_LIST);
                List<Object> values = new ArrayList<>();
                values.addAll(JkStreams.map(strElems, elem -> fromStringSingleValue(elem, elemClazz)));
                retVal = dfield.isSet() ? JkConvert.toTreeSet(values) : values;

            } else {
                retVal = fromStringSingleValue(value, dfield.getFieldType());
            }
        }

        return retVal;
    }
    private Object fromStringSingleValue(String value, Class<?> fclazz) {
        Object o = null;

        if (StringUtils.isEmpty(value)) {
            o = fclazz == String.class ? "" : null;
        } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
            o = Boolean.valueOf(value);
        } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
            o = JkConvert.toInt(value);
        } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
            o = JkConvert.toLong(value);
        } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
            o = JkConvert.toFloat(value);
        } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
            o = JkConvert.toDouble(value);
        } else if (fclazz == Path.class) {
            o = Paths.get(value);
        } else if (fclazz == File.class) {
            o = new File(value);
        } else if (fclazz == LocalTime.class) {
            o = LocalTime.parse(value, DateTimeFormatter.ISO_TIME);
        } else if (fclazz == LocalDate.class) {
            o = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        } else if (fclazz == LocalDateTime.class) {
            o = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        } else if(JkReflection.isInstanceOf(fclazz, JkEntityFieldCustom.class)) {
            o = JkReflection.createInstanceSafe(fclazz);
            ((JkEntityFieldCustom)o).setFromString(value);
        } else if(fclazz == String.class) {
            o = value.replaceAll(PH_TAB, "\t").replaceAll(PH_NEWLINE, "\n");
        }

        return o;
    }

    private Object listToSafeObject(List<?> values, DesignField dfield) {
        // fixme change creating proxy list/set
        if(dfield.isSet()) {
            return dfield.isFlatFieldComparable() ? JkConvert.toTreeSet(values) : JkConvert.toHashSet(values);
        }
        return values;
    }
}
