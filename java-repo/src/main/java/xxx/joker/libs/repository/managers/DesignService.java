package xxx.joker.libs.repository.managers;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.config.JkRepoConfig;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoFieldCustom;
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

import static xxx.joker.libs.core.utils.JkStrings.strf;

class DesignService {

    private static final Logger logger = LoggerFactory.getLogger(DesignService.class);

    private static final DateTimeFormatter DTF_TIME = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter DTF_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DTF_DATETIME = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final String SEP_FIELD = "##FLD##";
    private static final String SEP_LIST = "##LST##";

    private static final String PH_TAB = "@@TAB@@";
    private static final String PH_NEWLINE = "@@LF@@";
    private static final String PH_NULL = "@@NULL@@";


    private Map<Class<?>, TreeMap<Integer, DesignField>> designMap;

    public DesignService(Collection<Class<?>> classes) {
        this.designMap = parseEntityClasses(classes);
    }

    public void parseLines(RepoLines repoLines, RepoHandler repoHandler) {
        Map<Long, JkEntity> idMap = new HashMap<>();
        Map<Class<?>, List<JkEntity>> dataMap = new HashMap<>();

        // Parse entities lines, no deps
        repoLines.getEntityLines().forEach((c, lines) -> {
            List<JkEntity> elist = JkStreams.map(lines, l -> parseEntityLine(c, l));
            dataMap.put(c, elist);
            List<JkEntity> dups = JkStreams.filter(elist, e -> idMap.containsKey(e.getEntityID()));
            if (!dups.isEmpty()) {
                logger.warn("Something went wrong: class={}, ID dups={}", c.getSimpleName(), dups);
            }
            Map<Long, JkEntity> clazzIdMap = JkStreams.toMapSingle(elist, JkEntity::getEntityID);
            idMap.putAll(clazzIdMap);
        });

        // Parse FK and set entities deps
        List<ForeignKey> fkList = JkStreams.map(repoLines.getFkLines(), this::parseForeignKey);
        Map<Long, List<ForeignKey>> idFromFKs = JkStreams.toMap(fkList, ForeignKey::getFromID);

        repoHandler.initHandler(dataMap, idMap, idFromFKs, designMap);
    }

    public RepoLines formatEntities(RepoHandler repoHandler) {
        RepoLines repoLines = new RepoLines();

        TreeMap<Class<?>, List<String>> repoMapLines = repoLines.getEntityLines();
        List<String> fkLines = repoLines.getFkLines();

        Map<Long, Pair<JkEntity, List<ForeignKey>>> dataMap = repoHandler.getDataAndForeignKeys();
        for(long id : dataMap.keySet()) {
            JkEntity e = dataMap.get(id).getKey();
            String line = formatEntityInstance(e);
            repoMapLines.putIfAbsent(e.getClass(), new ArrayList<>());
            repoMapLines.get(e.getClass()).add(line);
            List<String> fks = JkStreams.map(dataMap.get(id).getValue(), this::formatForeignKey);
            fkLines.addAll(fks);
        }

        designMap.keySet().forEach(c -> repoMapLines.putIfAbsent(c, new ArrayList<>()));

        return repoLines;
    }

    private Map<Class<?>, TreeMap<Integer, DesignField>> parseEntityClasses(Collection<Class<?>> classes) {
        List<String> dups = JkStreams.getDuplicates(JkStreams.map(classes, Class::getSimpleName));
        if (!dups.isEmpty()) {
            throw new RepoDesignError("all entity classes must have different class name. Duplicates: {}", dups);
        }

        Map<Class<?>, TreeMap<Integer, DesignField>> toRet = new HashMap<>();

        for (Class<?> clazz : classes) {
            List<Field> fields = JkReflection.getFieldsByAnnotation(clazz, JkEntityField.class);
            if (fields.isEmpty()) {
                throw new RepoDesignError("no JkEntityField annotated fields found in entity {}", clazz);
            }

            toRet.put(clazz, new TreeMap<>());

            List<DesignField> dfields = JkStreams.map(fields, DesignField::new);

            List<DesignField> negIdxs = JkStreams.filter(dfields, df -> df.getAnnotIdx() < 0);
            if (!negIdxs.isEmpty()) {
                throw new RepoDesignError("class {}: negative 'idx' {}", clazz, negIdxs);
            }
            List<Integer> idxDups = JkStreams.getDuplicates(JkStreams.map(dfields, DesignField::getAnnotIdx));
            if (!idxDups.isEmpty()) {
                throw new RepoDesignError("class {}: duplicated 'idx' {}", clazz, idxDups);
            }

            for (DesignField dfield : dfields) {
                String fieldName = dfield.getFieldName();

                if (dfield.isSet() && !dfield.isFlatFieldComparable()) {
                    throw new RepoDesignError("field {}: set must have comparable elements", fieldName);
                }

                Class<?> toCheck = dfield.getFlatFieldType();
                if (!JkRepoConfig.isFieldClassAllowed(toCheck)) {
                    throw new RepoDesignError("field {}: class type {} not allowed", fieldName, toCheck);
                }

                toRet.get(clazz).put(dfield.getAnnotIdx(), dfield);
            }
        }

        return toRet;
    }

    // Skip field of flat type = JkEntity
    private JkEntity parseEntityLine(Class<?> elemClazz, String repoLine) {
        JkEntity instance = (JkEntity) JkReflection.createInstanceSafe(elemClazz);
        List<String> row = JkStrings.splitList(repoLine, SEP_FIELD);

        String entityID = row.remove(0);
        instance.setEntityID((Long) parseSingleValue(entityID, Long.class));
        String insTstamp = row.remove(0);
        instance.setInsertTstamp((LocalDateTime) parseSingleValue(insTstamp, LocalDateTime.class));

        for (Map.Entry<Integer, DesignField> entry : designMap.get(elemClazz).entrySet()) {
            DesignField dfield = entry.getValue();
            if (!dfield.isFlatJkEntity()) {
                if (entry.getKey() < row.size()) {
                    Object o = parseValue(row.get(entry.getKey()), dfield);
                    dfield.setValue(instance, o);
                }
            }
        }

        return instance;
    }
    private Object parseValue(String value, DesignField dfield) {
        Object retVal;

        if (dfield.isCollection()) {
            List<Object> values = new ArrayList<>();
            List<String> strElems = JkStrings.splitList(value, SEP_LIST);
            Class<?> elemClazz = dfield.getCollectionType();
            values.addAll(JkStreams.map(strElems, elem -> parseSingleValue(elem, elemClazz)));
            if(dfield.isSet()) {
                retVal = dfield.isFlatFieldComparable() ? JkConvert.toTreeSet(values) : JkConvert.toHashSet(values);
            } else {
                retVal = values;
            }

        } else {
            retVal = parseSingleValue(value, dfield.getFieldType());
        }

        return retVal;
    }
    private Object parseSingleValue(String value, Class<?> fclazz) {
        Object o;

        if (value.equals(PH_NULL)) {
            o = null;
        } else if (anyMatch(fclazz, boolean.class, Boolean.class)) {
            o = Boolean.valueOf(value);
        } else if (anyMatch(fclazz, int.class, Integer.class)) {
            o = JkConvert.toInt(value);
        } else if (anyMatch(fclazz, long.class, Long.class)) {
            o = JkConvert.toLong(value);
        } else if (anyMatch(fclazz, float.class, Float.class)) {
            o = JkConvert.toFloat(value);
        } else if (anyMatch(fclazz, double.class, Double.class)) {
            o = JkConvert.toDouble(value);
        } else if (fclazz == Path.class) {
            o = Paths.get(value);
        } else if (fclazz == File.class) {
            o = new File(value);
        } else if (fclazz == LocalTime.class) {
            o = LocalTime.parse(value, DTF_TIME);
        } else if (fclazz == LocalDate.class) {
            o = LocalDate.parse(value, DTF_DATE);
        } else if (fclazz == LocalDateTime.class) {
            o = LocalDateTime.parse(value, DTF_DATETIME);
        } else if (JkReflection.isInstanceOf(fclazz, JkRepoFieldCustom.class)) {
            o = JkReflection.createInstanceSafe(fclazz);
            ((JkRepoFieldCustom) o).parseString(value);
        } else if (fclazz == String.class) {
            o = value.replaceAll(PH_TAB, "\t").replaceAll(PH_NEWLINE, "\n");
        } else {
            throw new RepoDesignError("String parsing not implemented for: class = {}, value = {}", fclazz, value);
        }

        return o;
    }
    private ForeignKey parseForeignKey(String fkLine) {
        String[] line = JkStrings.splitArr(fkLine, SEP_FIELD);
        long fromID = JkConvert.toLong(line[0]);
        int fromFieldIdx = JkConvert.toInt(line[1]);
        long depID = JkConvert.toLong(line[2]);
        return new ForeignKey(fromID, fromFieldIdx, depID);
    }

    private String formatEntityInstance(JkEntity entity) {
        TreeMap<Integer, DesignField> dfMap = designMap.get(entity.getClass());

        int numCols = dfMap.keySet().stream().mapToInt(i -> i).max().orElse(-1) + 1;
        String[] row = new String[numCols];
        for(int i = 0; i < row.length; i++)     row[i] = "";

        for(int findex : dfMap.keySet()) {
            DesignField dfield = dfMap.get(findex);
            if (!dfield.isFlatJkEntity()) {
                Object value = dfield.getValue(entity);
                String strEntity = formatValue(value, dfield);
                row[findex] = strEntity;
            }
        }

        List<String> finalRow = new ArrayList<>();
        finalRow.add(formatSimpleValue(entity.getEntityID(), Long.class));
        finalRow.add(formatSimpleValue(entity.getInsertTstamp(), LocalDateTime.class));
        finalRow.addAll(Arrays.asList(row));

        return JkStreams.join(finalRow, SEP_FIELD);
    }
    private String formatValue(Object value, DesignField dfield) {
        Class<?> flatFieldType = dfield.getFlatFieldType();

        String strValue;
        if (dfield.isCollection()) {
            List<?> list = dfield.isSet() ? JkConvert.toArrayList((Set<?>) value) : (List<?>) value;
            strValue = JkStreams.join(list, SEP_LIST, e -> formatSimpleValue(e, flatFieldType));
        } else {
            strValue = formatSimpleValue(value, flatFieldType);
        }

        return strValue;
    }
    private String formatSimpleValue(Object value, Class<?> fclazz) {
        String toRet;

        if (value == null) {
            toRet = PH_NULL;
        } else if (anyMatch(fclazz, boolean.class, Boolean.class)) {
            toRet = ((Boolean) value) ? "true" : "false";
        } else if (anyMatch(fclazz, File.class, Path.class)) {
            toRet = value.toString();
        } else if (fclazz == LocalTime.class) {
            toRet = DateTimeFormatter.ISO_TIME.format((LocalTime) value);
        } else if (fclazz == LocalDate.class) {
            toRet = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
        } else if (fclazz == LocalDateTime.class) {
            toRet = DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) value);
        } else if (anyMatch(fclazz, int.class, Integer.class, long.class, Long.class, float.class, Float.class, double.class, Double.class)) {
            toRet = String.valueOf(value);
        } else if (fclazz == String.class) {
            toRet = String.valueOf(value).replaceAll("\t", PH_TAB).replaceAll("\n", PH_NEWLINE);
        } else if (JkReflection.isInstanceOf(fclazz, JkRepoFieldCustom.class)) {
            toRet = ((JkRepoFieldCustom)value).formatField();
        } else {
            throw new RepoDesignError("Object formatting not implemented for: class = {}, value = {}", fclazz, value);
        }

        return toRet;
    }
    private String formatForeignKey(ForeignKey fk) {
        return strf("{}{}{}{}{}", fk.getFromID(), SEP_FIELD, fk.getFromFieldIdx(), SEP_FIELD, fk.getDepID());
    }
    
    private boolean anyMatch(Class<?> toFind, Class<?>... elems) {
        for(Class<?> c : elems) {
            if(c == toFind) return true;
        }
        return false;
    }

}