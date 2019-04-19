package xxx.joker.libs.repository.util;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.RepoEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RepoUtil {

    /**
     *  // todo put method in core libs
     * @param fieldNames string in the form 'nation|date|...' or 'nation date ...'.
     *                   Keywords allowed: eid, epk
     * @return
     */
    public static String formatEntities(Collection<? extends RepoEntity> entities, String fieldNames) {
        try {
            String hsep = fieldNames.contains("|") ? "|" : " ";
            List<String> split = JkStrings.splitList(fieldNames, hsep);
            List<String> lines = new ArrayList<>();
            for (RepoEntity e : entities) {
                StringBuilder sb = new StringBuilder();
                for (String fname : split) {
                    if (sb.length() > 0) sb.append("|");
                    if (fname.equalsIgnoreCase("eid")) {
                        Object fval = JkReflection.getFieldValue(e, "entityID");
                        if(fval == null)  sb.append("NULL");
                        else              sb.append(fval);
                    } else if (fname.equalsIgnoreCase("epk")) {
                        Method method = e.getClass().getMethod("getPrimaryKey");
                        sb.append((String) method.invoke(e));
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
                        } else {
                            sb.append(fval);
                        }
                    }
                }
                lines.add(sb.toString());
            }
            String header = JkStreams.join(split, "|", RepoUtil::createStringHeader);
            lines.add(0, header);
            return JkOutput.columnsView(lines, 2);

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

}
