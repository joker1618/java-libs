package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.tests.JkChecks;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.runtimes.JkReflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class ParserArgs {

    private Class<? extends JkAbstractArgs> argsClass;
    private boolean ignoreCaseArgs;
    private List<ArgWrapper> argList;

    public ParserArgs(Class<? extends JkAbstractArgs> argsClass, ParserTypes parserTypes, boolean checkDesign, boolean ignoreCaseArgs) throws DesignError {
        this.argsClass = argsClass;
        this.ignoreCaseArgs = ignoreCaseArgs;
        this.argList = new ArrayList<>();
        init(checkDesign, parserTypes);
    }

    public ArgWrapper getArgWrapper(String nameAlias) {
        List<ArgWrapper> filter = JkStreams.filter(argList, aw -> aw.getArgName().equalsIgnoreCase(nameAlias) || JkChecks.containsIgnoreCase(aw.getAliases(), nameAlias));
        return filter.isEmpty() ? null : filter.get(0);
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
            argList.add(argWrapper);
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
            throw new DesignError(argsClass, "field {}, argName is blank", field.getName());
        }
        if(annName.contains(" ")) {
            throw new DesignError(argsClass, "field {}, argName contains spaces [{}]", field.getName(), annName);
        }
        annAliases.forEach(alias -> {
            if(StringUtils.isBlank(alias)) {
                throw new DesignError(argsClass, "field {}, alias is blank", field.getName());
            }
            if(alias.contains(" ")) {
                throw new DesignError(argsClass, "field {}, alias contains spaces [{}]", field.getName(), alias);
            }
        });

        // argName and aliases must be unique
        if (!allNameAlias.add(annName)) {
            throw new DesignError(argsClass, "argName {} duplicated", annName);
        } else {
            annAliases.forEach(alias -> {
                if (!allNameAlias.add(alias)) {
                    throw new DesignError(argsClass, "alias {} duplicated", alias);
                }
            });
        }
    }

    private void checkClassCoherence(Field field, JkArg annot) {
        if (annot.classes().length == 0) {
            // no annotation classes -> class is the field type (must be supported)
            if (!Configs.SUPPORTED_CLASSES.contains(field.getType())) {
                throw new DesignError(argsClass,
                        "field {}: type {} not allowed",
                        field.getName(),
                        field.getType().getSimpleName()
                );
            }

        } else {
            List<Class<?>> annotClasses = JkConvert.toArrayList(annot.classes());

            // annotation classes found --> field type must be Object
            if (field.getType() != Object.class) {
                throw new DesignError(argsClass,
                        "field {} must be Object, because classes are specified in his JkArg annotation",
                        field.getName()
                );
            }

            // annotation classes must be supported
            annotClasses.removeIf(Configs.SUPPORTED_CLASSES::contains);
            if (!annotClasses.isEmpty()) {
                throw new DesignError(argsClass,
                        "field {}: annotation class {} not allowed",
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
            throw new DesignError(argsClass, "no enum with argName equals to [{}] found in class {}", annot.argName(), argTypeClass);
        }
    }

}
