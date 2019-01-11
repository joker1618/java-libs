package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkStreams;

import java.lang.reflect.Field;
import java.util.*;

class ParserArgs {

    private Class<? extends JkAbstractArgs> argsClass;
    private boolean ignoreCaseArgs;
    private Map<String, ArgWrapper> argsMap;

    public ParserArgs(Class<? extends JkAbstractArgs> argsClass, ParserTypes parserTypes, boolean checkDesign, boolean ignoreCaseArgs) throws DesignError {
        this.argsClass = argsClass;
        this.ignoreCaseArgs = ignoreCaseArgs;
        this.argsMap = new HashMap<>();
        init(checkDesign, parserTypes);
    }

    public ArgWrapper getArgWrapper(String argName) {
        return argsMap.get(argName);
    }

    private void init(boolean checkDesign, ParserTypes parserTypes) {
        List<Field> annFields = JkReflection.getFieldsByAnnotation(argsClass, JkArg.class);
        Set<String> allNameAlias = new TreeSet<>();
        for(Field field : annFields) {
            JkArg annot = field.getAnnotation(JkArg.class);
            if(checkDesign) {
                checkNameAliasUniqueness(allNameAlias, field, annot);
                checkClassCoherence(field, annot);
                checkRelatedOptName(parserTypes, annot);
            }
            JkArgsTypes optName = parserTypes.getByArgName(annot.argName());
            ArgWrapper argWrapper = new ArgWrapper(field, optName);
            argsMap.put(argWrapper.getArgName(), argWrapper);
        }
    }

    private void checkNameAliasUniqueness(Set<String> allNameAlias, Field field, JkArg annot) {
        String annName = annot.argName();
        List<String> annAliases = JkConvert.toArrayList(annot.aliases());

        if(ignoreCaseArgs) {
            annName = annName.toLowerCase();
            annAliases = JkStreams.map(annAliases, String::toLowerCase);
        }

        // No spaces in argName and aliases
        if(StringUtils.isBlank(annName)) {
            throw new DesignError(argsClass, "field %s, argName is blank", field.getName());
        }
        if(annName.contains(" ")) {
            throw new DesignError(argsClass, "field %s, argName contains spaces [%s]", field.getName(), annName);
        }
        annAliases.forEach(alias -> {
            if(StringUtils.isBlank(alias)) {
                throw new DesignError(argsClass, "field %s, alias is blank", field.getName());
            }
            if(alias.contains(" ")) {
                throw new DesignError(argsClass, "field %s, alias contains spaces [%s]", field.getName(), alias);
            }
        });

        // argName and aliases must be unique
        if (!allNameAlias.add(annName)) {
            throw new DesignError(argsClass, "argName \"%s\" duplicated", annName);
        } else {
            annAliases.forEach(alias -> {
                if (!allNameAlias.add(alias)) {
                    throw new DesignError(argsClass, "alias \"%s\" duplicated", alias);
                }
            });
        }
    }

    private void checkClassCoherence(Field field, JkArg annot) {
        if (annot.classes().length == 0) {
            // no annotation classes -> class is the field type (must be supported)
            if (!Configs.SUPPORTED_CLASSES.contains(field.getType())) {
                throw new DesignError(argsClass,
                        "field \"%s\": type \"%s\" not allowed",
                        field.getName(),
                        field.getType().getSimpleName()
                );
            }

        } else {
            List<Class<?>> annotClasses = JkConvert.toArrayList(annot.classes());

            // annotation classes found --> field type must be Object
            if (field.getType() != Object.class) {
                throw new DesignError(argsClass,
                        "field %s must be Object, because classes are specified in his JkArg annotation",
                        field.getName()
                );
            }

            // annotation classes must be supported
            annotClasses.removeIf(Configs.SUPPORTED_CLASSES::contains);
            if (!annotClasses.isEmpty()) {
                throw new DesignError(argsClass,
                        "field \"%s\": annotation class %s not allowed",
                        field.getName(),
                        JkStreams.map(annotClasses, Class::getSimpleName)
                );
            }
        }
    }

    private void checkRelatedOptName(ParserTypes parserTypes, JkArg annot) {
        // check if exists an enum field in JkArgsTypes with argName equals to annot.argName
        if(parserTypes.getByArgName(annot.argName()) == null) {
            String argTypeClass = parserTypes.getArgsNamesClass().getSimpleName();
            throw new DesignError(argsClass, "no enum with argName equals to [%s] found in class %s", annot.argName(), argTypeClass);
        }
    }

}
