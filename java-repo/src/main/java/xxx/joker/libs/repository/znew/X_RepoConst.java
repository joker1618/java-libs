package xxx.joker.libs.repository.znew;

import xxx.joker.libs.repository.design2.RepoEntity;
import xxx.joker.libs.repository.design2.RepoFieldCustom;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import static xxx.joker.libs.repository.znew.X_RepoConst.Separator.*;

public class X_RepoConst {

    public static final String FIELD_NAME_REPO_ENTITY_ID = "entityID";

    public static class Separator {
        public static final String SEP_FIELD = "|";
        public static final String SEP_LIST = ";";

        public static final String PH_SEP_FIELD = "@_PIPE_@";
        public static final String PH_SEP_LIST = "@_SCL_@";
        public static final String PH_TAB = "@_TAB_@";
        public static final String PH_NEWLINE = "@_LF_@";
        public static final String PH_NULL = "@_NUL_@";
    }

    public static String escapeString(String value, boolean fullEscape) {
        if(value == null) {
            return PH_NULL;
        }
        String res = value.replace(SEP_LIST, PH_SEP_LIST);
        res = res.replace(SEP_FIELD, PH_SEP_FIELD);
        if(fullEscape) {
            res = res.replaceAll("\t", PH_TAB);
            res = res.replaceAll("\n", PH_NEWLINE);
        }
        return res;
    }

    public static String unescapeString(String value, boolean fullEscape) {
        if(PH_NULL.equals(value)) {
            return null;
        }
        String res = value.replace(PH_SEP_LIST, SEP_LIST);
        res = res.replace(PH_SEP_FIELD, SEP_FIELD);
        if(fullEscape) {
            res = res.replace(PH_TAB, "\t");
            res = res.replace(PH_NEWLINE, "\n");
        }
        return res;
    }

    public static boolean isValidType(ClazzField field) {
        boolean res = ALLOWED_FIELDS.contains(field.getFieldType());
        if(!res) {
            Class<?> sc = field.getFieldType().getSuperclass();
            res = CUSTOM_FIELDS.contains(sc);
        }
        if(!res && ALLOWED_COLLECTIONS.contains(field.getFieldType())) {
            Class<?> elemType = field.getElemType();
            res = CUSTOM_FIELDS.contains(elemType.getSuperclass()) || ALLOWED_FIELDS.contains(elemType);
        }
        return res;
    }


    private static final List<Class<?>> CUSTOM_FIELDS = Arrays.asList(
            RepoEntity.class,
            RepoFieldCustom.class
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
