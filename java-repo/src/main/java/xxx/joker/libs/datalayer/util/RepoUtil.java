package xxx.joker.libs.datalayer.util;

import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.format.JkFormatter;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.runtimes.JkRuntime;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.entities.RepoUri;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoUtil {

    private static JkFormatter csvParser = JkFormatter.get();
    static {
        // csvParser configs
        csvParser.setClassFormat(JkDateTime.class, d -> d.format("yyyyMMdd_HHmmss"));
        csvParser.setClassFormat(LocalDateTime.class, d -> JkDateTime.of(d).format("yyyyMMdd_HHmmss"));
        csvParser.setClassFormat(RepoUri.class, u -> strf("uri[{}]", u.getEntityId()));
        csvParser.setInstanceFormat(RepoEntity.class, RepoEntity::strMini);
        csvParser.setInstanceFormat(RepoEntity.class, RepoEntity::strMini);
    }

    public static String toStringEntities(Collection<? extends RepoEntity> coll) {
        if(coll.isEmpty())  return "";
        List<String> collLines = csvParser.formatCsv(coll);
        RepoEntity repoEntity = JkConvert.toList(coll).get(0);
        return strf("*** {} ({}) ***\n{}", repoEntity.getClass(), coll.size(), JkOutput.columnsView(collLines));
    }


    public static boolean isOfClass(Class<?> toFind, Class<?>... elems) {
        for(Class<?> c : elems) {
            if(c == toFind) return true;
        }
        return false;
    }

    public static List<Class<?>> scanPackages(Class<?> launcherClazz, String... pkgsArr) {
        Set<Class<?>> classes = new HashSet<>();

        List<String> pkgsToScan = JkConvert.toList(pkgsArr);
        pkgsToScan.forEach(pkg -> classes.addAll(JkRuntime.findClasses(launcherClazz, pkg)));
        classes.removeIf(c -> !JkReflection.isInstanceOf(c, RepoEntity.class));
        classes.removeIf(c -> Modifier.isAbstract(c.getModifiers()));
        classes.removeIf(c -> Modifier.isInterface(c.getModifiers()));

        return JkConvert.toList(classes);
    }
}
