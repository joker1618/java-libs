package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkCreator;
import xxx.joker.libs.core.utils.JkReflection;
import xxx.joker.libs.core.utils.JkStreams;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.*;

class ParserCmds {

    private Class<? extends JkCommands> cmdsClass;
    private Map<String, CmdWrapper> cmdsMap;

    public ParserCmds(Class<? extends JkCommands> cmdsClass, ParserTypes parserTypes, ParserArgs parserArgs, boolean checkDesign) throws DesignError {
        this.cmdsClass = cmdsClass;
        this.cmdsMap = new TreeMap<>();
        init(checkDesign, parserTypes, parserArgs);
    }

    private void init(boolean checkDesign, ParserTypes parserTypes, ParserArgs parserArgs) {
        List<Field> fields = JkReflection.getFieldsByAnnotation(cmdsClass, JkCmdElem.class);
        if (checkDesign && fields.isEmpty()) {
            throw new DesignError(cmdsClass, "no fields annotated with @JkCmdElem");
        }

        for(Field field : fields) {
            if (checkDesign && !field.isEnumConstant()) {
                throw new DesignError(cmdsClass, "field [{}] is not an enum", field.getName());
            }

            Enum<?> enumCmd = JkReflection.getEnumByName(cmdsClass, field.getName());

            CmdWrapper cmd = new CmdWrapper((JkCommands) enumCmd);

            if(checkDesign) {
                checkParamListSize(cmd);
                checkOptionsUniqueness(cmd);
                checkParamDependency(cmd);
                checkDefaults(cmd);
            }

            setAndCheckOptionClass(cmd, parserArgs, checkDesign);

            cmdsMap.put(cmd.getCmdName(), cmd);
        }

        computeCmdEvolutions(cmdsMap.values(), parserTypes.getAllTypes().size());
    }

    private void checkParamListSize(CmdWrapper cmd) {
        List<CParam> params = cmd.getParams();

        // must exists at least one cmdParam
        if(params.isEmpty()) {
            throw new DesignError(cmdsClass, "command {}: no parameters", cmd.getCmdName());
        }

        // all params must be at least one option
        params.forEach(cp -> {
            if(cp.getOptions().isEmpty()) {
                throw new DesignError(cmdsClass, "command {}: one param has no options", cmd.getCmdName());
            }
        });

        int numEmpty = JkStreams.filter(params, p -> p.getOptions().isEmpty()).size();
        if(numEmpty > 0) {
            throw new DesignError(cmdsClass, "command {}: {} params have no options", cmd.getCmdName(), numEmpty);
        }
    }

    private void checkOptionsUniqueness(CmdWrapper cmd) {
        List<COption> dups = JkStreams.getDuplicates(cmd.getOptions(), Comparator.comparing(COption::getArgName));
        if (!dups.isEmpty()) {
            List<String> dupNames = JkStreams.map(dups, COption::getArgName);
            throw new DesignError(cmdsClass, "command {}: options {} duplicated", cmd.getCmdName(), dupNames);
        }
    }

    /**
     * IMPORTANT: transitive dependencies are not checked,
     * so is up to the user to avoid circular dependencies
     */
    private void checkParamDependency(CmdWrapper cmd) {
        List<CParam> withDeps = JkStreams.filter(cmd.getParams(), param -> param.getDependOn() != null);
        for(CParam cp : withDeps) {
            Enum<? extends JkArgsTypes> dependOn = cp.getDependOn();
            // Dependency must refer to a COption that belong to another CParam
            boolean empty = JkStreams.filter(cp.getOptions(), co -> co.getArgType() == dependOn).isEmpty();
            if(!empty) {
                throw new DesignError(cmdsClass, "command {}: auto-dependency {} found", cmd.getCmdName(), dependOn.name());
            }
            // Dependency must exists in options
            boolean found = !JkStreams.filter(cmd.getOptions(), co -> !cp.getOptions().contains(co) && co.getArgType() == dependOn).isEmpty();
            if(!found) {
                throw new DesignError(cmdsClass, "command {}: dependency {} not found", cmd.getCmdName(), dependOn.name());
            }
        }
    }

    private void checkDefaults(CmdWrapper cmd) {
        List<CParam> withDefault = JkStreams.filter(cmd.getParams(), param -> param.getDefault() != null);
        for(CParam cp : withDefault) {
            Enum<? extends JkArgsTypes> def = cp.getDependOn();
            // Default must belong to the CParam or not exists at all in cmd options
            boolean isOK = !JkStreams.filter(cp.getOptions(), co -> co.getArgType() == def).isEmpty();
            isOK |= JkStreams.filter(cmd.getOptions(), co -> co.getArgType() == def).isEmpty();
            if(!isOK) {
                throw new DesignError(cmdsClass, "command {}: default value must belong to the same CParam or not exists at all", cmd.getCmdName(), def.name());
            }
        }
    }

    private void setAndCheckOptionClass(CmdWrapper cmd, ParserArgs parserArgs, boolean checkDesign) {
        String cmdName = cmd.getCmdName();
        List<COption> options = cmd.getOptions();

        for(int i = 0; i < options.size(); i++) {
            COption co = options.get(i);
            ArgWrapper aw = parserArgs.getArgWrapper(co.getArgName());

            Class<?> optType = aw.getField().getType();

            if(co.getOptionClass() == null) {
                // class not specified: option must have empty 'classes' (so field type class != Object.class)
                // option class will be option field type
                if(checkDesign && optType == Object.class) {
                    throw new DesignError(cmdsClass, "command {}, arg {}: class not specified", cmdName, aw.getArgType());
                }
                co.setOptionClass(optType);

            } else if(checkDesign) {
                // class specified: must be included in option 'classes' (so field type class == Object.class)
                if(optType != Object.class) {
                    throw new DesignError(cmdsClass, "command {}, arg {}: class cannot be selected for a {} field", cmdName, aw.getArgType(), optType);
                }
                if(!aw.getClasses().contains(co.getOptionClass())) {
                    String selected = co.getOptionClass().getSimpleName();
                    List<String> allowed = JkStreams.map(aw.getClasses(), Class::getSimpleName);
                    throw new DesignError(cmdsClass, "command {}, arg {}: selected class [{}], allowed classes {}", cmdName, aw.getArgType(), selected, allowed);
                }
            }

            if(checkDesign) {
                List<Class<?>> chronoClasses = Arrays.asList(LocalDate.class, LocalTime.class, LocalDateTime.class);
                if(chronoClasses.contains(co.getOptionClass())) {
                    if (co.getDateTimeFormatter() == null) {
                        throw new DesignError(cmdsClass, "command {}, arg {}: DateTimeFormatter not specified", cmdName, aw.getArgType());
                    }
                } else if (co.getDateTimeFormatter() != null) {
                    throw new DesignError(cmdsClass, "command {}, arg {}: DateTimeFormatter is not allowed", cmdName, aw.getArgType());
                }
            }
        }
    }

    private void computeCmdEvolutions(Collection<CmdWrapper> cmdWrappers, int numArgsTypes) {
        ParamEvolutor paramEvolutor = new ParamEvolutor(numArgsTypes);

        // Compute all evolutions for each command
        Map<String, Set<String>> evolMap = new TreeMap<>();
        for(CmdWrapper cw : cmdWrappers) {
            evolMap.put(cw.getCmdName(), paramEvolutor.computeEvolutions(cw.getParams()));
        }

        // Check evolutions uniqueness
        List<String> keys = JkConvert.toArrayList(evolMap.keySet());
        for(int i = 0; i < keys.size() - 1; i++) {
            for(int j = i + 1; j < keys.size(); j++) {
                Set<String> setI = evolMap.get(keys.get(i));
                Set<String> setJ = evolMap.get(keys.get(j));
                boolean dup = !JkStreams.filter(setI, setJ::contains).isEmpty();
                if(dup) {
                    throw new DesignError(cmdsClass, "duplicated commands: {}, {}", keys.get(i), keys.get(j));
                }
            }
        }

        // Set evolutions to wrappers
        for(CmdWrapper cw : cmdWrappers) {
            cw.setEvolutions(evolMap.get(cw.getCmdName()));
        }
    }

    private static class ParamEvolutor {
        int numTypes;

        ParamEvolutor(int numArgsTypes) {
            numTypes = numArgsTypes;
        }

        Set<String> computeEvolutions(List<CParam> params) {
            if(params.isEmpty()) {
                return JkCreator.newTreeSet(StringUtils.repeat('0', numTypes));
            }

            int[] counters = new int[params.size()];
            int[] maxArr = new int[params.size()];

            List<Integer> maxs = JkStreams.map(params, par -> par.getOptions().size());
            for(int i = 0; i < maxs.size(); i++) {
                maxArr[i] = maxs.get(i);
            }

            List<char[]> evols = new ArrayList<>();
            boolean go = true;
            while(go) {
                evols.add(createEvol(counters, params));
                go = incr(counters, maxArr);
            }

            return new TreeSet<>(JkStreams.map(evols, String::new));
        }

        char[] createEvol(int[] counters, List<CParam> params) {
            char[] evol = StringUtils.repeat('0', numTypes).toCharArray();
            for(int i = 0; i < params.size(); i++) {
                COption co = params.get(i).getOptions().get(counters[i]);
                evol[co.getArgType().ordinal()] = '1';
            }
            return evol;
        }

        boolean incr(int[] counters, int[] maxs) {
            int cnum = counters.length - 1;
            while(cnum >= 0) {
                int cval = counters[cnum];
                int cmax = maxs[cnum];
                if(cval == cmax - 1) {
                    counters[cnum] = 0;
                    cnum--;
                } else {
                    counters[cnum] = cval + 1;
                    break;
                }
            }
            return cnum >= 0;
        }
    }

}
