package xxx.joker.libs.datalayer.util;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.datalayer.config.RepoCtx;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.wrapper.ClazzWrap;
import xxx.joker.libs.datalayer.wrapper.FieldWrap;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.runtimes.JkReflection.isInstanceOf;
import static xxx.joker.libs.core.runtimes.JkReflection.isOfClass;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class MigrateToHibernate {

    public static void createJavaFiles(Path destFolder, RepoCtx ctx) {
        List<String> daoNames = new ArrayList<>();

        for (ClazzWrap cw : ctx.getClazzWraps().values()) {
            String cnameSimple = cw.getEClazz().getSimpleName();

            List<String> newLines = new ArrayList<>();
            newLines.add(strf("package {};", destFolder.getFileName()));
            newLines.add("");
            newLines.add("import javax.persistence.*;");
            newLines.add("import java.io.Serializable;");
            newLines.add("import java.util.*;");
            newLines.add("import java.time.*;");
            newLines.add("");
            newLines.add("@Entity");
            newLines.add(strf("public class {} extends JpaEntity implements Serializable {", cnameSimple));
            newLines.add("");
            newLines.add("@Id");
            newLines.add("@GeneratedValue");
            newLines.add("private Long jpaID;");
            newLines.add("private String repoPK;");
            newLines.add("");

            for (FieldWrap fw : cw.getFieldWraps()) {
                if(fw.isEntity()) {
                    newLines.add("@ManyToOne");
                } else if(fw.isEntityColl()) {
                    newLines.add("@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})");
                } else if(fw.isCollection()) {
                    newLines.add("@ElementCollection");
                }
                newLines.add(strf("private {} {};", fixFieldType(fw), fw.getFieldName()));
            }

            newLines.add("");

            newLines.add(strf("public Long getJpaID() {"));
            newLines.add(strf("return jpaID;"));
            newLines.add(strf("}"));

            newLines.add(strf("public String getRepoPK() {"));
            newLines.add(strf("return repoPK;"));
            newLines.add(strf("}"));
//            newLines.add(strf("public void setRepoPK(String repoPK) {"));
//            newLines.add(strf("this.repoPK = repoPK;"));
//            newLines.add(strf("}"));

            for (FieldWrap fw : cw.getFieldWraps()) {
                String ftype = fixFieldType(fw);
                String fname = fw.getFieldName();
                newLines.add(strf("public {} get{}() {", ftype, StringUtils.capitalize(fname)));
                newLines.add(strf("return {};", fname));
                newLines.add(strf("}"));
                newLines.add(strf("public void set{}({} {}) {", StringUtils.capitalize(fname), ftype, fname));
                newLines.add(strf("this.{} = {};", fname, fname));
                newLines.add(strf("}"));
            }
            newLines.add(strf("}"));
            JkFiles.writeFile(destFolder.resolve(strf("{}.java", cnameSimple)), newLines);

            String str = strf("package {};\n\n", destFolder.getFileName());
            str += strf("import org.springframework.data.jpa.repository.*;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "@Repository\n" +
                    "public interface {}Dao extends JpaRepository<{}, Long> {\n" +
                    "\n" +
                    "}", cnameSimple, cnameSimple);
            String daoName = strf("{}Dao", cnameSimple);
            daoNames.add(daoName);
            JkFiles.writeFile(destFolder.resolve(strf("{}.java", daoName)), str);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("public abstract class AbstractJpaModel {\n\n");
        daoNames.forEach(dn -> sb.append(strf("@Autowired\nprotected {} {}\n\n", dn, dn.substring(0, 1).toLowerCase()+dn.substring(1))));
        sb.append("}");

        JkFiles.writeFile(destFolder.resolve("AbstractJpaModel.java"), sb.toString());


        String str = strf("package {};\n\n", destFolder.getFileName());
        str += "import org.apache.commons.lang3.builder.ToStringBuilder;\n" +
                "import org.apache.commons.lang3.builder.ToStringStyle;\n" +
                "\n" +
                "public abstract class JpaEntity {\n" +
                "\n" +
                "    @Override\n" +
                "    public String toString() {\n" +
                "        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);\n" +
                "    }\n" +
                "\n" +
                "}";
        JkFiles.writeFile(destFolder.resolve("JpaEntity.java"), str);
    }
    private static String fixFieldType(FieldWrap fw) {
        if(fw.isCollection()) {
            return strf("{}<{}>", fw.getFieldType().getSimpleName(), fixFieldType(fw.getCollType()));
        } else {
            return fixFieldType(fw.getFieldType());
        }
    }
    private static String fixFieldType(Class<?> clazz) {
        if(isOfClass(clazz, JkDateTime.class)) {
            clazz = LocalDateTime.class;
        } else if(isOfClass(clazz, Path.class, File.class)) {
            clazz = String.class;
        } else if(isInstanceOf(clazz, JkFormattable.class, Enum.class)) {
            clazz = String.class;
        }
        return clazz.getSimpleName();
    }

    public static <T> T cloneRepoEntity(RepoEntity source, Class<T> targetClass) {
        T cloned = JkReflection.copyFields(source, targetClass);
        Field fieldRepoPK = JkReflection.getFieldByName(cloned.getClass(), "repoPK");
        if(fieldRepoPK != null) {
            JkReflection.setFieldValue(cloned, fieldRepoPK, source.getPrimaryKey());
        }
        return cloned;
    }
}
