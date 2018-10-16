package xxx.joker.libs.javalibs.datamodel;

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
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.datamodel.JkPersistenceManager.EntityLines;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

class JkEM2 {

    private static final Logger logger = LoggerFactory.getLogger(JkEM2.class);

    private static final String DATA_FIELD_SEP = "###FIELD_SEP###";
    private static final String DATA_LIST_SEP = "###LIST_SEP###";
    private static final String PH_TAB = "##TAB##";
    private static final String PH_NEWLINE = "##NEWLINE##";

    private Map<Class<?>, Map<Integer, AnnField>> entityFields;
    private Map<Class<?>, Map<String, Long>> idmap;

    public JkEM2(String pkgToScan) {
        this.entityFields = parseEntityClasses(pkgToScan);
    }

    private Map<Class<?>, Map<Integer, AnnField>> parseEntityClasses(String pkgToScan) {
        Map<Class<?>, Map<Integer, AnnField>> parsedEntities = new HashMap<>();

        logger.info("Scanning package {}", pkgToScan);
        List<Class<?>> entityClasses = retrieveEntityClasses(pkgToScan);

        if(entityClasses.isEmpty()) {
            throw new JkRuntimeException("No JkEntity class found in package {}", pkgToScan);
        }

        logger.info("{} JkEntity class found in package {}", entityClasses.size(), pkgToScan);
        entityClasses.forEach(c -> logger.info("Entity class: {}", c.getName()));

        for(Class<?> clazz : entityClasses) {
            List<Class<?>> dups = JkStreams.filter(parsedEntities.keySet(), c -> c.getSimpleName().equals(clazz.getSimpleName()));
            if(!dups.isEmpty()) {
                // dups has one element only
                throw new IllegalArgumentException(strf("All entity classes must have different class name. Duplicates: {}, {}", clazz.getName(), dups.get(0).getName()));
            }

            if (!parsedEntities.containsKey(clazz)) {
                logger.debug("Entity class {}", clazz.getName());

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
    
    public Map<Class<?>, EntityLines> formatData(Map<Class<?>, TreeSet<JkEntity>> dataMap) {
        Map<Class<?>, EntityLines> toRet = new HashMap<>();
        idmap = new HashMap<>();
        AtomicLong idseq = new AtomicLong(0L);
        for (Class<?> clazz : dataMap.keySet()) {
            idmap.put(clazz, new HashMap<>());
            dataMap.get(clazz).forEach(e -> idmap.get(clazz).put(e.getPrimaryKey(), idseq.getAndIncrement()));
        }
        for (Class<?> clazz : dataMap.keySet()) {
            EntityLines el = formatEntityClass(clazz, dataMap.get(clazz));
            toRet.put(clazz, el);
        }
        return toRet;
    }

    private EntityLines formatEntityClass(Class<?> clazz, Collection<JkEntity> dataList) {
        try {
            EntityLines toRet = new EntityLines(clazz);
            Map<Integer, AnnField> clazzFields = entityFields.get(clazz);
            int numFields = clazzFields.keySet().stream().mapToInt(i->i).max().orElse(-1) + 1;
            String strTs = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            for(JkEntity elem : JkStreams.distinctSorted(dataList)) {
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
                                .distinct()
                                .map(fk -> new ForeignKey(clazz, elem.getPrimaryKey(), index, fkClazz, fk))
                                .map(ForeignKey::toRepoLine)
                                .collect(Collectors.toList());
                        toRet.getForeignKeyLines().addAll(fkLines);
                    }
                }
                row.add(idmap.get(elem.getClass()).get(elem.getPrimaryKey())+"");
                row.add(strTs);
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
                if (JkReflection.isOfType(fclazz, JkEntity.class)) {
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

        boolean isEntityImpl() {
            Class<?> fclazz = isCollection() ? getCollectionType() : getFieldType();
            return JkReflection.isOfType(fclazz, JkEntity.class);
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
                    + idmap.get(fromClazz).get(fromPK)
                    + DATA_FIELD_SEP
                    + fromFieldIndex
                    + DATA_FIELD_SEP
                    + targetClazz.getName()
                    + DATA_FIELD_SEP
                    + idmap.get(targetClazz).get(targetPK);
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


    
}
