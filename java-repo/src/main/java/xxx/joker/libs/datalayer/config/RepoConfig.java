package xxx.joker.libs.datalayer.config;

import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.wrapper.FieldWrap;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConvert.toList;

public class RepoConfig {

    public static final String PACKAGE_COMMON_ENTITIES = "xxx.joker.libs.datalayer.entities";

    public static final String RESOURCES_FOLDER_NAME = "resources";
    public static final String DB_FOLDER_NAME = "db";

    public static final String[] HEADER_REPO_FKEYS_FIELDS = {"entityId", "fieldName", "depId"};

    public static final String PROP_SEQUENCE = "_config.sequence.id.value";
    public static final String PROP_LAST_COMMIT = "_config.last.commit";

    public static final String DB_JKREPO_KEYWORD = "jkrepo";
    public static final String DB_FILENAME_FORMAT = "{}#{}#" + DB_JKREPO_KEYWORD + ".{}";
    public static final String DB_EXT_DATA_FILE = "data";
    public static final String DB_EXT_DESCR_FILE = "descr";
    public static final String DB_FKEYS_FORMAT = "{}#" + DB_JKREPO_KEYWORD + ".fkeys";

    public static class CsvSep {
        public static final String SEP_FIELD = "|";
        public static final String SEP_LIST = ";";

        public static final String PH_SEP_FIELD = "@_PIPE_@";
        public static final String PH_SEP_LIST = "@_SCL_@";
        public static final String PH_TAB = "@_TAB_@";
        public static final String PH_NEWLINE = "@_LF_@";
        public static final String PH_NULL = "@_NUL_@";
    }

    public static boolean isValidType(FieldWrap fieldWrap) {
        Class<?> fieldType = fieldWrap.getFieldType();
        boolean res = ALLOWED_FIELDS.contains(fieldType);
        if(!res) {
            res = JkReflection.isInstanceOf(fieldType, CUSTOM_FIELDS);
        }
        if(!res && ALLOWED_COLLECTIONS.contains(fieldType)) {
            Class<?> collType = fieldWrap.getCollType();
            res = JkReflection.isInstanceOf(collType, CUSTOM_FIELDS) || ALLOWED_FIELDS.contains(collType);
        }
        return res;
    }

    public static String getRepoFileFkeysHeader() {
        return JkStreams.join(toList(HEADER_REPO_FKEYS_FIELDS), CsvSep.SEP_FIELD);
    }

    private static final List<Class<?>> CUSTOM_FIELDS = Arrays.asList(
            RepoEntity.class,
            JkFormattable.class,
            Enum.class
    );

    private static final List<Class<?>> ALLOWED_FIELDS = Arrays.asList(
            Boolean.class,		boolean.class,
            Integer.class,		int.class,
            Long.class,			long.class,
            Float.class,		float.class,
            Double.class,		double.class,

            LocalTime.class,
            LocalDate.class,
            LocalDateTime.class,

            String.class,
            File.class,
            Path.class
    );

    private static final List<Class<?>> ALLOWED_COLLECTIONS = Arrays.asList(
            List.class,
            Set.class
    );

}
