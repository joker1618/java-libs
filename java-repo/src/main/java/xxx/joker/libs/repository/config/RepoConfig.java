package xxx.joker.libs.repository.config;

import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class RepoConfig {

    private static final List<Class<?>> ALLOWED_JAVA_CLASSES = Arrays.asList(
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

    public static boolean isFieldClassAllowed(Class<?> fieldClazz) {
        return ALLOWED_JAVA_CLASSES.contains(fieldClazz)
                || JkReflection.isInstanceOf(fieldClazz, JkEntity.class)
                || JkReflection.isInstanceOf(fieldClazz, JkEntityFieldCustom.class);
    }
}
