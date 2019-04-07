package xxx.joker.libs.repository.common;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoFieldCustom;
import xxx.joker.libs.repository.engine.FieldWrapper;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RepoCommon {

    public static class Separator {
        public static final String SEP_FIELD = "|";
        public static final String SEP_LIST = ";";

        public static final String PH_SEP_FIELD = "@_PIPE_@";
        public static final String PH_SEP_LIST = "@_SCL_@";
        public static final String PH_TAB = "@_TAB_@";
        public static final String PH_NEWLINE = "@_LF_@";
        public static final String PH_NULL = "@_NUL_@";
    }

    public static boolean isValidType(FieldWrapper fieldWrapper) {
        Class<?> fieldType = fieldWrapper.getFieldType();
        boolean res = ALLOWED_FIELDS.contains(fieldType);
        if(!res) {
            Class<?> sc = fieldType.getSuperclass();
            res = CUSTOM_FIELDS.contains(sc);
        }
        if(!res && ALLOWED_COLLECTIONS.contains(fieldType)) {
            Class<?> elemType = fieldWrapper.getElemType();
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

            JkDuration.class,
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
