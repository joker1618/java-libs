package xxx.joker.libs.repository.util;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.RepoEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RepoUtil {

    /**
     * @param fieldsToDisplay strings in the form 'nation date ...'.
     *                        Keywords allowed: eid, epk, etm
     * @return
     */
    public static String formatEntities(Collection<? extends RepoEntity> entities, String... fieldsToDisplay) {
        try {
            List<String> fieldNames = getFieldNames(fieldsToDisplay);
            List<String> lines = new ArrayList<>();

            for (RepoEntity e : entities) {
                StringBuilder sb = new StringBuilder();

                if(fieldNames.isEmpty()) {
                    fieldNames.add("epk");
                    // retrieve all fields recursively
                    Class<?> sourceClazz = e.getClass();
                    while(sourceClazz != null) {
                        List<String> fnames = JkStreams.map(JkConvert.toList(sourceClazz.getDeclaredFields()), Field::getName);
                        fieldNames.addAll(fnames);
                        sourceClazz = sourceClazz.getSuperclass();
                    }
                }

                for (String fname : fieldNames) {
                    if (sb.length() > 0) sb.append("|");

                    if (StringUtils.equalsAnyIgnoreCase(fname, "eid", "entityID")) {
                        Object fval = JkReflection.getFieldValue(e, "entityID");
                        if(fval == null)  sb.append("NULL");
                        else              sb.append(fval);
                    } else if (fname.equalsIgnoreCase("epk")) {
                        Method method = e.getClass().getMethod("getPrimaryKey");
                        sb.append((String) method.invoke(e));
                    } else if (StringUtils.equalsAnyIgnoreCase(fname, "etm", "creationTm")) {
                        Object fval = JkReflection.getFieldValue(e, "creationTm");
                        if(fval == null)  sb.append("NULL");
                        else              sb.append(((JkFormattable)fval).format());
                    } else {
                        Object fval = JkReflection.getFieldValue(e, fname);
                        if(fval == null) {
                            sb.append("NULL");
                        } else if(JkReflection.isInstanceOf(fval.getClass(), Collection.class)) {
                            sb.append(((Collection)fval).size());
                        } else if(JkReflection.isInstanceOf(fval.getClass(), RepoEntity.class)) {
                            sb.append(((RepoEntity)fval).strShort());
                        } else if(JkReflection.isInstanceOf(fval.getClass(), JkFormattable.class)) {
                            sb.append(((JkFormattable)fval).format());
                        } else if(fval.getClass() == Path.class) {
                            sb.append(((JkFormattable)fval).format());
                        } else {
                            sb.append(fval);
                        }
                    }
                }
                lines.add(sb.toString());
            }

            if(!fieldNames.isEmpty()) {
                String header = JkStreams.join(fieldNames, "|", RepoUtil::createStringHeader);
                lines.add(0, header);
            }

            return JkOutput.columnsView(lines);

        } catch (Exception ex) {
            throw new JkRuntimeException(ex);
        }
    }

    private static String createStringHeader(String str) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if(c >= 'A' && c <= 'Z') {
                if(i > 0) {
                    char o = str.charAt(i-1);
                    if(o >= 'a' && o <= 'z') {
                        sb.append(" ");
                    }
                }
            }
            sb.append(c);
        }
        String res = sb.toString().replace("_", " ").replaceAll(" +", " ").trim();
        return res.toUpperCase();
    }

    private static List<String> getFieldNames(String... fieldNames) {
        List<String> toRet = new ArrayList<>();
        for (String fstr : fieldNames) {
            String trimmed = fstr.replaceAll(" +", " ").trim();
            List<String> tlist = JkStrings.splitList(trimmed, " ");
            toRet.addAll(tlist);
        }
        return toRet;
    }
}
