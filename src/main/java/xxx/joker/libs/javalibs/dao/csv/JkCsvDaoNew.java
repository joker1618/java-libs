package xxx.joker.libs.javalibs.dao.csv;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import xxx.joker.libs.javalibs.utils.JkFiles;
import xxx.joker.libs.javalibs.utils.JkReflection;
import xxx.joker.libs.javalibs.utils.JkStreams;

import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

public class JkCsvDaoNew {

	private static final String LINE_SEP = StringUtils.LF;
//	private static final CsvSep FIELD_SEP = new CsvSep(";", ";");
	private static final CsvSep FIELD_SEP = new CsvSep("###FIELD_SEP###", ";");
//	private static final CsvSep LIST_SEP = new CsvSep(",", ",");
	private static final CsvSep LIST_SEP = new CsvSep("###LIST_SEP###", ",");

	private static final String PLACEHOLDER_TAB = "##TAB##";
	private static final String PLACEHOLDER_NEWLINE = "##NEWLINE##";

	private static final String DB_FILE_SUFFIX = ".jkdb";

	private final Path daoFolder;
	private Map<Class<? extends CsvElement>, DaoElem> daoParsers;

    public JkCsvDaoNew(Path dataFolder, String dbName) throws IOException, ClassNotFoundException {
        this.daoFolder = dataFolder.resolve(dbName);

        this.daoParsers = new HashMap<>();
        List<Path> daoFiles = JkFiles.findFiles(daoFolder, false, path -> path.toString().endsWith(DB_FILE_SUFFIX));
        for(Path path : daoFiles) {
            String clazzName = path.getFileName().toString().replace(DB_FILE_SUFFIX, "");
            DaoElem daoElem = new DaoElem(Class.forName(clazzName));
            daoParsers.put(daoElem.daoClass, daoElem);
        }

        List<Class<?>> otherClasses = daoParsers.values().stream()
                .flatMap(d -> d.getForeignKeys().stream())
                .filter(c -> !daoParsers.keySet().contains(c))
                .collect(Collectors.toList());

        for(Class<?> c : otherClasses) {
            DaoElem daoElem = new DaoElem(c);
            daoParsers.put(daoElem.daoClass, daoElem);
        }
    }



    private static class DaoElem {

        private Class<? extends CsvElement> daoClass;
        private Map<Integer, AnnField> fieldMap;
        private Set<Class<?>> foreignKeys;
        private int numFields;

        public DaoElem(Class<?> daoClass) throws InvalidClassException {
            this.daoClass = (Class<? extends CsvElement>) daoClass;

            if(!isImplOf(daoClass, CsvElement.class)) {
                throw new InvalidClassException(strf("Class %s must implements CsvElement", daoClass));
            }

            List<Field> fields = JkReflection.getFieldsByAnnotation(daoClass, CsvField.class);

            this.fieldMap = new HashMap<>();
            this.foreignKeys = new HashSet<>();

            for (Field f : fields) {
                CsvField csvField = f.getAnnotation(CsvField.class);
                if (csvField.index() < 0) {
                    throw new IllegalArgumentException(strf("Negative index not allowed. Field %s", f.getName()));
                }
                if (fieldMap.containsKey(csvField.index())) {
                    throw new IllegalArgumentException(strf("Duplicated index %d", csvField.index()));
                }

                boolean isCsvElem = isImplOf(f.getType(), CsvElement.class);
                if(!DaoUtil.allowedClasses.contains(f.getType()) && !isCsvElem) {
                    throw new InvalidClassException(strf("Class %s not allowed", daoClass));
                }
                if(isCsvElem) {
                    foreignKeys.add(f.getType());
                }

                fieldMap.put(csvField.index(), new AnnField(csvField, f));
                numFields = Math.max(numFields, csvField.index()+1);
            }
        }

        private boolean isImplOf(Class<?> clazz, Class<?> expected) {
            List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
            return interfaces.contains(expected);
        }

        public Class<? extends CsvElement> getDaoClass() {
            return daoClass;
        }

        public Map<Integer, AnnField> getFieldMap() {
            return fieldMap;
        }

        public Set<Class<?>> getForeignKeys() {
            return foreignKeys;
        }

        public int getNumFields() {
            return numFields;
        }
    }



    private static class AnnField {
        CsvField ann;
        Field field;

        AnnField(CsvField ann, Field field) {
            this.ann = ann;
            this.field = field;
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

    private static class CsvSep {
        String safeSep;
        String simpleSep;

        CsvSep(String safeSep, String simpleSep) {
            this.safeSep = safeSep;
            this.simpleSep = simpleSep;
        }
    }


}
