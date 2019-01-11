package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.argsparser.design.annotations.JkArgType;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkReflection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class ParserTypes {

    private static final Logger logger = LoggerFactory.getLogger(ParserTypes.class);

    private Class<? extends JkArgsTypes> argsNamesClass;
    private Map<String, JkArgsTypes> argsNamesMap;

    public ParserTypes(Class<? extends JkArgsTypes> argsNamesClass, boolean checkDesign) throws DesignError {
        this.argsNamesClass = argsNamesClass;
        this.argsNamesMap = new TreeMap<>();
        init(checkDesign);
    }

    public JkArgsTypes getByArgName(String argName) {
        return argsNamesMap.get(argName);
    }

    public Class<? extends JkArgsTypes> getArgsNamesClass() {
        return argsNamesClass;
    }

    public List<JkArgsTypes> getAllTypes() {
        return JkConvert.toArrayList(argsNamesMap.values());
    }

    private void init(boolean checkDesign) {
        List<Field> fields = JkReflection.getFieldsByAnnotation(argsNamesClass, JkArgType.class);
        
        for(Field field : fields) {
            // check if field is an Enum
            if(checkDesign && !field.isEnumConstant()) {
                throw new DesignError(argsNamesClass, "field {} is not an enum", field.getName());
            }

            Enum<?> enumOptName = JkReflection.getEnumByName(argsNamesClass, field.getName());
            JkArgsTypes argName = (JkArgsTypes) enumOptName;

            if(checkDesign) {
                if(StringUtils.isBlank(argName.getArgName())) {
                    throw new DesignError(argsNamesClass, "argName [{}] is blank", argName);
                }
                if(argName.getArgName().contains(" ")) {
                    throw new DesignError(argsNamesClass, "argName [{}] contains spaces", argName);
                }
                // check if option argName is duplicated
                if(argsNamesMap.containsKey(argName.getArgName())) {
                    throw new DesignError(argsNamesClass, "argName [{}] duplicated", argName);
                }
            }

            argsNamesMap.put(argName.getArgName(), argName);
        }
    }
}
