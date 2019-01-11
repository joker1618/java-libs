package xxx.joker.libs.repository.config;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.repository.design.JkEntity;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RepoConfig {

    private static final List<Class<?>> ALLOWED_JAVA_CLASSES = Arrays.asList(
            boolean.class,
            int.class,
            long.class,
            float.class,
            double.class,

            Boolean.class,        Boolean[].class,
            Integer.class,        Integer[].class,
            Long.class,           Long[].class,
            Float.class,          Float[].class,
            Double.class,         Double[].class,
            File.class,           File[].class,
            Path.class,           Path[].class,
            LocalTime.class,      LocalTime[].class,
            LocalDate.class,      LocalDate[].class,
            LocalDateTime.class,  LocalDateTime[].class,
            String.class,         String[].class,

            Pair.class,

            List.class,
            Set.class,
            Map.class
    );

    public static boolean isFieldClassAllowed(Class<?> fieldClazz) {
        return ALLOWED_JAVA_CLASSES.contains(fieldClazz)
                || JkReflection.isInstanceOf(fieldClazz, JkEntity.class)
                || JkReflection.isInstanceOf(fieldClazz, JkEntityFieldCustom.class);
    }
}
