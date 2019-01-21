package xxx.joker.libs.core.repositoryOLD;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.repositoryOLD.entity.JkEntity;
import xxx.joker.libs.core.repositoryOLD.entity.JkEntityField;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.File;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static xxx.joker.libs.core.utils.JkStrings.strf;

@ToAnalyze
@Deprecated
class JkEntityManager {

    private static final Logger logger = LoggerFactory.getLogger(JkEntityManager.class);

    private static final String DATA_FIELD_SEP = "###FIELD_SEP###";
    private static final String DATA_LIST_SEP = "###LIST_SEP###";
    private static final String PH_TAB = "##TAB##";
    private static final String PH_NEWLINE = "##NEWLINE##";
    
    private Map<Class<?>, Map<Integer, AnnField>> entityFields;
    private AtomicLong sequence;

    public JkEntityManager(List<Class<?>> classes, AtomicLong sequence) {
        this.entityFields = parseEntityClasses(classes);
        this.sequence = sequence;
    }

    private Map<Class<?>, Map<Integer, AnnField>> parseEntityClasses(List<Class<?>> entityClasses) {
        Map<Class<?>, Map<Integer, AnnField>> parsedEntities = new HashMap<>();

        entityClasses.forEach(c -> logger.info("Entity class: {}", c.getName()));

        for(Class<?> clazz : entityClasses) {
            List<Class<?>> dups = JkStreams.filter(parsedEntities.keySet(), c -> c.getSimpleName().equals(clazz.getSimpleName()));
            if(!dups.isEmpty()) {
                // dups has one element only
                throw new IllegalArgumentException(strf("All entity classes must have different class name. Duplicates: {}, {}", clazz.getName(), dups.get(0).getName()));
            }

            if (!parsedEntities.containsKey(clazz)) {
                Map<Integer, AnnField> annMap = new HashMap<>();
                parsedEntities.put(clazz, annMap);

                List<Field> fields = JkReflection.getFieldsByAnnotation(clazz, JkEntityField.class);

                if (!fields.isEmpty()) {
                    List<AnnField> annFields = JkStreams.map(fields, AnnField::new);

                    for (AnnField annField : annFields) {
                        if (annField.getIndex() < 0) {
                            throw new IllegalArgumentException(strf("Negative index not allowed. Field %s", annField.getField().getName()));
                        }
                        if (annMap.containsKey(annField.getIndex())) {
                            throw new IllegalArgumentException(strf("Duplicated index %d", annField.getIndex()));
                        }
                        if (!annField.isCollection()) {
                            if(annField.getCollectionType() != Object.class) {
                                throw new IllegalArgumentException(strf("Collection type must be specified only for List, Set and arrays (index=%d)", annField.getIndex()));
                            }
                        } else {
                            if(annField.getCollectionType() == Object.class) {
                                throw new IllegalArgumentException(strf("Collection type not specified (index=%d)", annField.getIndex()));
                            }
                        }
                        Class<?> fctype = annField.isCollection() ? annField.getCollectionType() : annField.getFieldType();
                        if(!RepoUtil.isClassAllowed(fctype)) {
                            throw new IllegalArgumentException(strf("Field class %s not allowed", fctype.getName()));
                        }
                        annMap.put(annField.getIndex(), annField);
                    }
                }
            }
        }

        return parsedEntities;
    }

    public Map<Class<?>, Set<JkEntity>> parseData(Map<Class<?>, JkPersistenceManager.EntityLines> dataMap) {
        Map<Class<?>, Map<Long, JkEntity>> entityMap = new HashMap<>();

        // Parse design
        for(Class<?> clazz : dataMap.keySet()) {
            List<JkEntity> elist = JkStreams.map(dataMap.get(clazz).getEntityLines(), l -> parseLine(clazz, l));
            entityMap.put(clazz, JkStreams.toMapSingle(elist, JkEntity::getEntityID));
        }

        // Resolve dependencies and fill design objects
        try {
            for (Class<?> fromClazz : dataMap.keySet()) {
                List<ForeignKey> fkOfClass = JkStreams.map(dataMap.get(fromClazz).getForeignKeyLines(), ForeignKey::new);
                Map<Long, List<ForeignKey>> fromPKmap = JkStreams.toMap(fkOfClass, ForeignKey::getFromID);
                for (Long fromID : fromPKmap.keySet()) {
                    JkEntity fromObj = entityMap.get(fromClazz).get(fromID);
                    Map<Integer, List<ForeignKey>> fkIndexMap = JkStreams.toMap(fromPKmap.get(fromID), ForeignKey::getFromFieldIndex);
                    for (int fieldIndex : fkIndexMap.keySet()) {
                        List<ForeignKey> fklist = fkIndexMap.get(fieldIndex);
                        List<JkEntity> elist = JkStreams.mapFilter(fklist, fk -> entityMap.get(fk.getTargetClazz()).get(fk.getTargetID()), Objects::nonNull);
                        if(!elist.isEmpty()) {
                            AnnField annField = entityFields.get(fromClazz).get(fieldIndex);
                            Object objValue = annField.isCollection() ? listToSafeObject(elist, annField) : elist.get(0);
                            annField.setValue(fromObj, objValue);
                        }
                    }
                }
            }

            Map<Class<?>, Set<JkEntity>> toRet = new HashMap<>();
            for(Class<?> c : entityMap.keySet()) {
                HandlerSet handlerSet = new HandlerSet();
                Set<JkEntity> proxySet = (Set<JkEntity>) Proxy.newProxyInstance(TreeSet.class.getClassLoader(), new Class[]{Set.class}, handlerSet);
                proxySet.addAll(entityMap.get(c).values());
                toRet.put(c, proxySet);
            }

            return toRet;

        } catch(IllegalAccessException ex) {
            throw new JkRuntimeException(ex, "Error parsing data");
        }
    }

    public Map<Class<?>, Set<JkEntity>> getDependencies(JkEntity obj) {
        try {
            List<AnnField> depAnnFields = JkStreams.filter(entityFields.get(obj.getClass()).values(), AnnField::isEntityImpl);
            Map<Class<?>, Set<JkEntity>> toRet = new HashMap<>();
            for (AnnField annField : depAnnFields) {
                toRet.putIfAbsent(annField.getEntityClass(), new TreeSet<>());
                Object value = annField.getValue(obj);
                if(value != null) {
                    if (annField.isCollection()) {
                        toRet.get(annField.getEntityClass()).addAll(JkStreams.map((Collection) value, v -> (JkEntity) v));
                    } else {
                        toRet.get(annField.getEntityClass()).add((JkEntity) value);
                    }
                }
            }
            return toRet;

        } catch(IllegalAccessException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private JkEntity parseLine(Class<?> elemClazz, String line) {
        try {
            JkEntity instance = (JkEntity) elemClazz.newInstance();
            List<String> row = JkStrings.splitList(line, DATA_FIELD_SEP);

            String entityID = row.remove(0);
            instance.setEntityID(JkConvert.toLong(entityID));
            String insTstamp = row.remove(0);
            instance.setInsertTstamp(LocalDateTime.parse(insTstamp, DateTimeFormatter.ISO_DATE_TIME));

            for (Map.Entry<Integer, AnnField> entry : entityFields.get(elemClazz).entrySet()) {
                if (entry.getKey() < row.size()) {
                    Object o = fromStringValue(row.get(entry.getKey()), entry.getValue());
                    entry.getValue().setValue(instance, o);
                }
            }

            return instance;

        } catch(ReflectiveOperationException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private Object fromStringValue(String value, AnnField annField) {
        Object retVal;

        Class<?> fclazz = annField.getFieldType();
        if(annField.isCollection()) {
            List<String> strElems = JkStrings.splitList(value, DATA_LIST_SEP);
            Class<?> elemClazz = annField.getCollectionType();

            List<Object> values = new ArrayList<>();
            if(!JkReflection.isInstanceOf(elemClazz, JkEntity.class)) {
                values.addAll(JkStreams.map(strElems, elem -> fromStringSingleValue(elem, elemClazz)));
            }

            retVal = listToSafeObject(values, annField);

        } else if(JkReflection.isInstanceOf(fclazz, JkEntity.class)) {
            retVal = null;

        } else {
            retVal = fromStringSingleValue(value, fclazz);
        }

        return retVal;
    }

    private Object listToSafeObject(List<?> values, AnnField annField) {
        if(annField.isSet()) {
            return annField.isComparable() ? JkConvert.toTreeSet(values) : JkConvert.toHashSet(values);
        }

        return values;
    }

    private Object fromStringSingleValue(String value, Class<?> fclazz) {
        Object o;

        if (StringUtils.isEmpty(value)) {
            o = fclazz == String.class ? "" : null;
        } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
            o = Boolean.valueOf(value);
        } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
            o = JkConvert.toInt(value);
        } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
            o = JkConvert.toLong(value);
        } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
            o = JkConvert.toDouble(value);
        } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
            o = JkConvert.toFloat(value);
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
        } else {
            // String case
            o = value.replaceAll(PH_TAB, "\t").replaceAll(PH_NEWLINE, "\n");
        }

        return o;
    }

    public Map<Class<?>, JkPersistenceManager.EntityLines> formatEntities(Map<Class<?>, Set<JkEntity>> dataMap) {
        try {
            Map<Class<?>, JkPersistenceManager.EntityLines> toRet = new HashMap<>();
            List<ForeignKey> allDeps = new ArrayList<>();
            Map<Class<?>, Map<String, Long>> mapPkId = new HashMap<>();

            for (Class<?> clazz : dataMap.keySet()) {
                toRet.put(clazz, new JkPersistenceManager.EntityLines(clazz));
                mapPkId.put(clazz, new HashMap<>());
                for (JkEntity elem : dataMap.get(clazz)) {
                    mapPkId.get(clazz).put(elem.getPrimaryKey(), elem.getEntityID());
                    Pair<String, List<ForeignKey>> pair = formatEntity(elem);
                    toRet.get(clazz).getEntityLines().add(pair.getKey());
                    allDeps.addAll(pair.getValue());
                }
            }

            for(ForeignKey fk : allDeps) {
                Long depID = mapPkId.get(fk.getTargetClazz()).get(fk.getTargetPK());
                if(depID != null) {
                    Long fromID = mapPkId.get(fk.getFromClazz()).get(fk.getFromPK());
                    fk.setFromID(fromID);
                    fk.setTargetID(depID);
                    toRet.get(fk.getFromClazz()).getForeignKeyLines().add(fk.toRepoLine());
                }
            }

            return toRet;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    private Pair<String, List<ForeignKey>> formatEntity(JkEntity entity) {
        try {
            Map<Integer, AnnField> clazzFields = entityFields.get(entity.getClass());
            List<ForeignKey> depList = new ArrayList<>();

            TreeMap<Integer, String> rowMap = new TreeMap<>();
            for (Integer index : clazzFields.keySet()) {
                AnnField annField = clazzFields.get(index);
                Object value = annField.getValue(entity);
                Pair<String, Set<String>> formattedPair = formatValue(value, annField);
                rowMap.put(index, formattedPair.getKey());
                Set<String> elemFKs = formattedPair.getValue();
                if(!elemFKs.isEmpty()) {
                    Class<?> fkClazz = annField.isCollection() ? annField.getCollectionType() : annField.getFieldType();
                    depList.addAll(JkStreams.map(elemFKs, fk -> new ForeignKey(entity.getClass(), entity.getPrimaryKey(), index, fkClazz, fk)));
                }
            }

            List<String> row = new ArrayList<>();
            row.add(entity.getEntityID()+"");
            row.add(entity.getInsertTstamp().format(DateTimeFormatter.ISO_DATE_TIME));

            int counter = 0;
            for(Integer col : rowMap.keySet()) {
                while (counter < col) {
                    row.add("");
                    counter++;
                }
                row.add(rowMap.get(counter++));
            }

            return Pair.of(JkStreams.join(row, DATA_FIELD_SEP), depList);

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    // return <toStringOfEntity, Set<foreignKeys(PK)>> (key or value)
    private Pair<String, Set<String>> formatValue(Object value, AnnField annField) {
        Class<?> fclazz = annField.getFieldType();

        String strValue = "";
        Set<String> foreignKeys = new HashSet<>();

        if(value != null) {
            if (annField.isCollection()) {
                Class<?> elemClazz = annField.getCollectionType();
                List<?> list = annField.isSet() ? JkConvert.toArrayList((Set<?>) value) : (List<?>) value;
                if (!list.isEmpty()) {
                    if (JkReflection.isInstanceOf(elemClazz, JkEntity.class)) {
                        List<String> fklist = JkStreams.map(list, el -> ((JkEntity) el).getPrimaryKey());
                        foreignKeys.addAll(fklist);
                    } else {
                        strValue = JkStreams.join(list, DATA_LIST_SEP, e -> toStringSingleValue(e, elemClazz));
                    }
                }

            } else {
                if (JkReflection.isInstanceOf(fclazz, JkEntity.class)) {
                    foreignKeys.add(((JkEntity) value).getPrimaryKey());
                } else {
                    strValue = toStringSingleValue(value, fclazz);
                }
            }
        }

        return Pair.of(strValue, foreignKeys);
    }

    private static String toStringSingleValue(Object value, Class<?> fclazz) {
        try {
            String toRet = "";

            if (value == null) {
                toRet = "";
            } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
                toRet = ((Boolean) value) ? "true" : "false";
            } else if (Arrays.asList(File.class, Path.class).contains(fclazz)) {
                toRet = value.toString();
            } else if (fclazz == LocalTime.class) {
                toRet = DateTimeFormatter.ISO_TIME.format((LocalTime) value);
            } else if (fclazz == LocalDate.class) {
                toRet = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
            } else if (fclazz == LocalDateTime.class) {
                toRet = DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) value);
            } else if (!JkReflection.isInstanceOf(fclazz, JkEntity.class)) {
                // String case
                toRet = String.valueOf(value).replaceAll("\t", PH_TAB).replaceAll("\n", PH_NEWLINE);
            }

            return toRet;

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private class AnnField {
        private JkEntityField annot;
        private Field field;

        AnnField(Field field) {
            this.annot = field.getAnnotation(JkEntityField.class);
            this.field = field;
        }

        int getIndex() {
            return annot.index();
        }

        public Field getField() {
            return field;
        }

        Class<?> getFieldType() {
            return field.getType();
        }

        Class<?> getParentClass() {
            return field.getDeclaringClass();
        }

        Class<?> getCollectionType() {
            return annot.collectionType();
        }

        boolean isEntityImpl() {
            Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
            return JkReflection.isInstanceOf(fclazz, JkEntity.class);
        }

        Class<?> getEntityClass() {
            Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
            return !isEntityImpl() ? null : fclazz;
        }

        boolean isCollection() {
            return isList() || isSet();
        }

        boolean isList() {
            return getFieldType() == List.class;
        }

        boolean isSet() {
            return getFieldType() == Set.class;
        }

        boolean isComparable() {
            Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
            return JkReflection.isInstanceOf(fclazz, Comparable.class);
        }

        Object getValue(Object elem) throws IllegalAccessException {
            boolean facc = field.isAccessible();
            field.setAccessible(true);
            Object obj = field.get(elem);
            field.setAccessible(facc);
            return obj;
        }

        void setValue(Object elem, Object value) throws IllegalAccessException {
            boolean facc = field.isAccessible();
            field.setAccessible(true);
            field.set(elem, value);
            field.setAccessible(facc);
        }
    }

    private class ForeignKey {
        private Class<?> fromClazz;
        private Long fromID;
        private String fromPK;
        private int fromFieldIndex;
        private Class<?> targetClazz;
        private Long targetID;
        private String targetPK;

        public ForeignKey(Class<?> fromClazz, String fromPK, int fromFieldIndex, Class<?> targetClazz, String targetPK) {
            this.fromClazz = fromClazz;
            this.fromPK = fromPK;
            this.fromFieldIndex = fromFieldIndex;
            this.targetClazz = targetClazz;
            this.targetPK = targetPK;
        }

        public ForeignKey(String repoLine) {
            try {
                String[] arr = JkStrings.splitArr(repoLine, DATA_FIELD_SEP);
                fromClazz = Class.forName(arr[0]);
                fromID = JkConvert.toLong(arr[1]);
                fromFieldIndex = JkConvert.toInt(arr[2]);
                targetClazz = Class.forName(arr[3]);
                targetID = JkConvert.toLong(arr[4]);
            } catch (Exception e) {
                throw new JkRuntimeException(e);
            }
        }

        public String toRepoLine() {
            return fromClazz.getName()
                    + DATA_FIELD_SEP
                    + fromID
                    + DATA_FIELD_SEP
                    + fromFieldIndex
                    + DATA_FIELD_SEP
                    + targetClazz.getName()
                    + DATA_FIELD_SEP
                    + targetID;
        }

        public Class<?> getFromClazz() {
            return fromClazz;
        }

        public void setFromClazz(Class<?> fromClazz) {
            this.fromClazz = fromClazz;
        }

        public String getFromPK() {
            return fromPK;
        }

        public Long getFromID() {
            return fromID;
        }

        public void setFromID(Long fromID) {
            this.fromID = fromID;
        }

        public Long getTargetID() {
            return targetID;
        }

        public void setTargetID(Long targetID) {
            this.targetID = targetID;
        }

        public void setFromPK(String fromPK) {
            this.fromPK = fromPK;
        }

        public int getFromFieldIndex() {
            return fromFieldIndex;
        }

        public void setFromFieldIndex(int fromFieldIndex) {
            this.fromFieldIndex = fromFieldIndex;
        }

        public Class<?> getTargetClazz() {
            return targetClazz;
        }

        public void setTargetClazz(Class<?> targetClazz) {
            this.targetClazz = targetClazz;
        }

        public String getTargetPK() {
            return targetPK;
        }

        public void setTargetPK(String targetPK) {
            this.targetPK = targetPK;
        }
    }

    private class HandlerSet implements InvocationHandler {
        private final TreeSet<JkEntity> original;

        public HandlerSet() {
            this.original = new TreeSet<>();
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws IllegalAccessException, IllegalArgumentException,
                InvocationTargetException {

            if ("add".equals(method.getName())) {
                if(args[0] == null) {
                    return false;
                }

                synchronized (sequence) {
                    JkEntity e = (JkEntity) args[0];
                    if(e.getEntityID() == null) {
                        e.setEntityID(sequence.get());
                        e.setInsertTstamp(LocalDateTime.now());
                    }
                    if(original.contains(e)) {
                        return false;
                    } else {
                        sequence.getAndIncrement();
                        return method.invoke(original, e);
                    }
                }

            } else if ("addAll".equals(method.getName())) {
                Collection coll = (Collection)args[0];
                List<JkEntity> toAdd = new ArrayList<>();
                for(Object obj : coll) {
                    synchronized (sequence) {
                        JkEntity e = (JkEntity) obj;
                        if (e.getEntityID() == null) {
                            e.setEntityID(sequence.get());
                            e.setInsertTstamp(LocalDateTime.now());
                        }
                        if (!original.contains(e)) {
                            sequence.getAndIncrement();
                            toAdd.add(e);
                        }
                    }
                }

                if(toAdd.isEmpty()) {
                    return false;
                } else {
                    return method.invoke(original, toAdd);
                }
            }

            return method.invoke(original, args);
        }
    }

    
}