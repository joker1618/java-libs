package xxx.joker.apps.formula1.model;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.lang.reflect.Field;
import java.util.*;

public class F1ModelChecker {

    public static <T extends RepoEntity> Map<T, List<String>> checkNullEmptyFields(Collection<T> coll) {
        Map<T, List<String>> toRet = new HashMap<>();

        List<Field> fields = null;
        for (T e : coll) {
            List<String> nullFields = new ArrayList<>();

            if(fields == null) {
                fields = JkReflection.getFieldsByAnnotation(e.getClass(), RepoField.class);
            }

            for (Field field : fields) {
                Object fval = JkReflection.getFieldValue(e, field);
                if(fval == null) {
                    nullFields.add(field.getName());

                } else {

                    if(JkReflection.isInstanceOf(fval.getClass(), String.class)) {
                        if(StringUtils.isBlank(String.valueOf(fval))) {
                            nullFields.add(field.getName());
                        }

                    } else if(JkReflection.isInstanceOf(fval.getClass(), JkFormattable.class)) {
                        if(StringUtils.isBlank(((JkFormattable)fval).format())) {
                            nullFields.add(field.getName());
                        }

                    } else if(JkReflection.isInstanceOf(fval.getClass(), Collection.class)) {
                        Collection fvalColl = (Collection) fval;
                        if(fvalColl.isEmpty()) {
                            nullFields.add(field.getName());
                        }
                    }
                }
            }

            if(!nullFields.isEmpty()) {
                toRet.put(e, nullFields);
            }
        }

        return toRet;
    }



}
