package xxx.joker.libs.javalibs.datamodel.persistence;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.javalibs.datamodel.entity.JkEntity;
import xxx.joker.libs.javalibs.datamodel.entity.JkEntityField;
import xxx.joker.libs.javalibs.exception.JkRuntimeException;
import xxx.joker.libs.javalibs.utils.JkConverter;
import xxx.joker.libs.javalibs.utils.JkReflection;
import xxx.joker.libs.javalibs.utils.JkStreams;
import xxx.joker.libs.javalibs.utils.JkStrings;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

class EntityParser {

    private static final Logger logger = LoggerFactory.getLogger(EntityParser.class);

    private static final String DATA_FIELD_SEP = "###FIELD_SEP###";
    private static final String DATA_LIST_SEP = "###LIST_SEP###";
    private static final String PH_TAB = "##TAB##";
    private static final String PH_NEWLINE = "##NEWLINE##";
    
    private Map<Class<?>, Map<Integer, AnnField>> entityFields;

    protected EntityParser(String pkgToScan) {
        this.entityFields = parseEntityClasses(pkgToScan);
    }

    private Map<Class<?>, Map<Integer, AnnField>> parseEntityClasses(String pkgToScan) {
        Map<Class<?>, Map<Integer, AnnField>> parsedEntities = new HashMap<>();

        logger.info("Scanning package {}", pkgToScan);
        List<Class<?>> entityClasses = retrieveEntityClasses(pkgToScan);

        for(Class<?> clazz : entityClasses) {
            if (!parsedEntities.containsKey(clazz)) {
                logger.info("Entity class {}", clazz.getName());

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

    private List<Class<?>> retrieveEntityClasses(String pkgToScan) {
        List<Class<?>> classes = JkReflection.findClasses(pkgToScan);
        classes.removeIf(c -> !JkReflection.isOfType(c, JkEntity.class));
        return classes;
    }
    
    public List<Class<?>> getEntityClasses() {
        return JkConverter.toArrayList(entityFields.keySet());
    }
    
    public Map<Class<?>, List<JkEntity>> parseData(Map<Class<?>, EntityLines> csvDataMap) {
        Map<Class<?>, Map<String, JkEntity>> entityMap = new HashMap<>();

        for(Class<?> clazz : csvDataMap.keySet()) {
            List<JkEntity> elist = JkStreams.map(csvDataMap.get(clazz).getEntityLines(), l -> parseElem(clazz, l));
            entityMap.put(clazz, JkStreams.toMapSingle(elist, JkEntity::getPrimaryKey));
        }

        try {
            for (Class<?> fromClazz : csvDataMap.keySet()) {
                List<ForeignKey> fkOfClass = JkStreams.map(csvDataMap.get(fromClazz).getDepLines(), ForeignKey::new);
                Map<String, List<ForeignKey>> fromPKmap = JkStreams.toMap(fkOfClass, ForeignKey::getFromPK);
                for (String fromPK : fromPKmap.keySet()) {
                    JkEntity fromObj = entityMap.get(fromClazz).get(fromPK);
                    Map<Integer, List<ForeignKey>> fkIndexMap = JkStreams.toMap(fromPKmap.get(fromPK), ForeignKey::getFromFieldIndex);
                    for (int index : fkIndexMap.keySet()) {
                        List<ForeignKey> fklist = fkIndexMap.get(index);
                        List<JkEntity> elist = JkStreams.mapAndFilter(fklist, fk -> entityMap.get(fk.getTargetClazz()).get(fk.getTargetPK()), Objects::nonNull);
                        AnnField annField = entityFields.get(fromClazz).get(index);
                        Object objValue = listToSafeObject(elist, annField);
                        annField.setValue(fromObj, objValue);
                    }
                }
            }

            Map<Class<?>, List<JkEntity>> toRet = new HashMap<>();
            for(Class<?> c : entityMap.keySet()) {
                toRet.put(c, JkStreams.distinctSorted(entityMap.get(c).values()));
            }

            return toRet;

        } catch(IllegalAccessException ex) {
            throw new JkRuntimeException(ex, "Error parsing data");
        }
    }

    public Map<Class<?>, EntityLines> formatData(Map<Class<?>, List<JkEntity>> dataMap) {
        Map<Class<?>, EntityLines> toRet = new HashMap<>();
        for (Class<?> clazz : dataMap.keySet()) {
            EntityLines el = formatEntityClass(clazz, dataMap.get(clazz));
            toRet.put(clazz, el);
        }
        return toRet;
    }

    private JkEntity parseElem(Class<?> elemClazz, String line) {
        try {
            Object instance = elemClazz.newInstance();
            List<String> row = JkStrings.splitFieldsList(line, DATA_FIELD_SEP);
            for (Map.Entry<Integer, AnnField> entry : entityFields.get(elemClazz).entrySet()) {
                if (entry.getKey() < row.size()) {
                    Object o = fromStringValue(row.get(entry.getKey()), entry.getValue());
                    entry.getValue().setValue(instance, o);
                }
            }
            
            return (JkEntity)instance;

        } catch(ReflectiveOperationException ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private Object fromStringValue(String value, AnnField annField) {
        Object retVal;

        Class<?> fclazz = annField.getFieldType();
        if(annField.isCollection()) {
            List<String> strElems = JkStrings.splitFieldsList(value, DATA_LIST_SEP);
            Class<?> elemClazz = annField.getCollectionType();

            List<Object> values = new ArrayList<>();
            if(!JkReflection.isOfType(elemClazz, JkEntity.class)) {
                values.addAll(JkStreams.map(strElems, elem -> fromStringSingleValue(elem, elemClazz)));
            }

            retVal = listToSafeObject(values, annField);

        } else if(JkReflection.isOfType(fclazz, JkEntity.class)) {
            retVal = null;

        } else {
            retVal = fromStringSingleValue(value, fclazz);
        }

        return retVal;
    }

    private Object listToSafeObject(List<?> values, AnnField annField) {
        if(annField.isSet()) {
            return annField.isComparable() ? JkConverter.toTreeSet(values) : JkConverter.toHashSet(values);
        }

        if(annField.isComparable()) {
            Collections.sort((List<Comparable>)values);
        }
        return values;
    }

    private Object fromStringSingleValue(String value, Class<?> fclazz) {
        Object o;

        if (StringUtils.isEmpty(value)) {
            o = fclazz == String.class ? "" : null;
        } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
            o = Boolean.valueOf(value);
        } else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
            o = Boolean.valueOf(value);
        } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
            o = JkConverter.stringToInteger(value);
        } else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
            o = JkConverter.stringToInteger(value);
        } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
            o = JkConverter.stringToLong(value);
        } else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
            o = JkConverter.stringToLong(value);
        } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
            o = JkConverter.stringToDouble(value);
        } else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
            o = JkConverter.stringToDouble(value);
        } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
            o = JkConverter.stringToFloat(value);
        } else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
            o = JkConverter.stringToFloat(value);
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
            o = value.replaceAll(PH_TAB, "\t").replaceAll(PH_NEWLINE, "\n");
        }

        return o;
    }

    private EntityLines formatEntityClass(Class<?> clazz, Collection<JkEntity> dataList) {
        try {
            EntityLines toRet = new EntityLines(clazz);
            Map<Integer, AnnField> clazzFields = entityFields.get(clazz);
            int numFields = clazzFields.keySet().stream().mapToInt(i->i).max().orElse(-1) + 1;
            
            for(JkEntity elem : JkStreams.sorted(dataList)) {
                List<String> row = Stream.generate(() -> "").limit(numFields).collect(Collectors.toList());
                for (Integer index : clazzFields.keySet()) {
                    AnnField annField = clazzFields.get(index);
                    Object value = annField.getValue(elem);
                    Pair<String, Set<String>> formattedPair = formatValue(value, annField);
                    row.set(index, formattedPair.getKey());
                    Set<String> elemFKs = formattedPair.getValue();
                    if(!elemFKs.isEmpty()) {
                        Class<?> fkClazz = annField.isCollection() ? annField.getCollectionType() : annField.getFieldType();
                        List<String> fkLines = formattedPair.getValue().stream()
                                .sorted()
                                .map(fk -> new ForeignKey(clazz, elem.getPrimaryKey(), index, fkClazz, fk))
                                .map(ForeignKey::toRepoLine)
                                .collect(Collectors.toList());
                        toRet.getDepLines().addAll(fkLines);
                    }
                }
                toRet.getEntityLines().add(JkStreams.join(row, DATA_FIELD_SEP));
            }

            return toRet;

        } catch (Exception e) {
            throw new JkRuntimeException(e);
        }
    }

    // return <toStringOfEntity, Set<foreignKeys>> (key or value)
    private Pair<String, Set<String>> formatValue(Object value, AnnField annField) {
        Class<?> fclazz = annField.getFieldType();
        
        String strValue = "";
        Set<String> foreignKeys = new HashSet<>();

        if(value != null) {
            if (annField.isCollection()) {
                Class<?> elemClazz = annField.getCollectionType();
                List<?> list = annField.isSet() ? JkConverter.toArrayList((Set<?>) value) : (List<?>) value;
                if (!list.isEmpty()) {
                    if (JkReflection.isOfType(elemClazz, JkEntity.class)) {
                        List<String> fklist = JkStreams.map(list, el -> ((JkEntity) el).getPrimaryKey());
                        foreignKeys.addAll(fklist);
                    } else {
                        strValue = JkStreams.join(list, DATA_LIST_SEP, e -> toStringSingleValue(e, elemClazz));
                    }
                }

            } else {
                strValue = toStringSingleValue(value, fclazz);
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
            } else if (!JkReflection.isOfType(fclazz, JkEntity.class)) {
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
            return JkReflection.isOfType(fclazz, Comparable.class);
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
        private String fromPK;
        private int fromFieldIndex;
        private Class<?> targetClazz;
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
                String[] arr = JkStrings.splitAllFields(repoLine, DATA_FIELD_SEP);
                fromClazz = Class.forName(arr[0]);
                fromPK = arr[1];
                fromFieldIndex = JkConverter.stringToInteger(arr[2]);
                targetClazz = Class.forName(arr[3]);
                targetPK = arr[4];
            } catch (Exception e) {
                throw new JkRuntimeException(e);
            }
        }

        public String toRepoLine() {
            return fromClazz.getName()
                    + DATA_FIELD_SEP
                    + fromPK
                    + DATA_FIELD_SEP
                    + fromFieldIndex
                    + DATA_FIELD_SEP
                    + targetClazz.getName()
                    + DATA_FIELD_SEP
                    + targetPK;
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

    public static class EntityLines {
        private Class<?> entityClazz;
        private List<String> entityLines;
        private List<String> depLines;

        protected EntityLines(Class<?> entityClazz) {
            this.entityClazz = entityClazz;
            this.entityLines = new ArrayList<>();
            this.depLines = new ArrayList<>();
        }

        public Class<?> getEntityClazz() {
            return entityClazz;
        }

        public List<String> getEntityLines() {
            return entityLines;
        }

        public List<String> getDepLines() {
            return depLines;
        }

        public void merge(EntityLines el) {
            if(el.getEntityClazz() != entityClazz) {
                throw new JkRuntimeException("Class entity mismatch  (%s != %s)", entityClazz, el.getEntityClazz());
            }
            this.entityLines.addAll(el.getEntityLines());
            this.depLines.addAll(el.getDepLines());
        }
    }
    
}
