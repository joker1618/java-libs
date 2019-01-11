package xxx.joker.libs.argsparser.service;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.common.Configs;
import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.runtimes.JkReflection;
import xxx.joker.libs.core.utils.JkStreams;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

class ParserCmds {

    private Class<? extends JkCommands> cmdsClass;
    private List<CmdWrapper> cwList;
    private ParserTypes parserTypes;

    public ParserCmds(Class<? extends JkCommands> cmdsClass, ParserTypes parserTypes, ParserArgs parserArgs, boolean checkDesign) throws DesignError {
        this.cmdsClass = cmdsClass;
        this.cwList = new ArrayList<>();
        this.parserTypes = parserTypes;
        init(checkDesign, parserArgs);
    }

    public CmdWrapper retrieveCommand(Collection<ArgWrapper> argsWrappers) {
        int numTypes = parserTypes.getAllTypes().size();
        char[] chars = StringUtils.repeat('0', numTypes).toCharArray();
        argsWrappers.forEach(aw -> chars[aw.getArgType().ordinal()] = '1');
        String inputEvol = new String(chars);

        for(CmdWrapper cw : cwList) {
            if(cw.countIndependentEvolutions() > Configs.MAX_EVOLUTIONS) {
                if(containsEvolutionRequired(cw, inputEvol)) {
                    return cw;
                }
            } else if(cw.getEvolutions().contains(inputEvol)) {
                return cw;
            }
        }

        return null;
    }


    private void init(boolean checkDesign, ParserArgs parserArgs) {
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

            cmd.setEvolutions(computeCmdEvolutions(cmd));

            cwList.add(cmd);
        }

        checkCommandsDuplicates();
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
            Enum<? extends JkArgsTypes> def = cp.getDefault();
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

            if(co.getArgClass() == null) {
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
                if(!aw.getClasses().contains(co.getArgClass())) {
                    String selected = co.getArgClass().getSimpleName();
                    List<String> allowed = JkStreams.map(aw.getClasses(), Class::getSimpleName);
                    throw new DesignError(cmdsClass, "command {}, arg {}: selected class [{}], allowed classes {}", cmdName, aw.getArgType(), selected, allowed);
                }
            }

            if(checkDesign) {
                List<Class<?>> chronoClasses = Arrays.asList(LocalDate.class, LocalTime.class, LocalDateTime.class);
                if(chronoClasses.contains(co.getArgClass())) {
                    if (co.getDateTimeFormatter() == null) {
                        throw new DesignError(cmdsClass, "command {}, arg {}: DateTimeFormatter not specified", cmdName, aw.getArgType());
                    }
                } else if (co.getDateTimeFormatter() != null) {
                    throw new DesignError(cmdsClass, "command {}, arg {}: DateTimeFormatter is not allowed", cmdName, aw.getArgType());
                }
            }
        }
    }

    private void checkCommandsDuplicates() {
        for(int i = 0; i < cwList.size() - 1; i++) {
            List<String> evolsI = cwList.get(i).getEvolutions();
            for( int j = i+1; j < cwList.size(); j++) {
                List<String> evolsJ = cwList.get(j).getEvolutions();
                List<String> dups = JkStreams.filter(evolsI, evolsJ::contains);
                if(!dups.isEmpty()) {
                    throw new DesignError(cmdsClass, "duplicated commands: {}, {}", cwList.get(i).getCmdName(), cwList.get(j).getCmdName());
                }
            }
        }
    }

    private List<String> computeCmdEvolutions(CmdWrapper cmd) {
        boolean onlyRequired = cmd.countIndependentEvolutions() > Configs.MAX_EVOLUTIONS;
        List<CParam> params = onlyRequired ? JkStreams.filter(cmd.getParams(), CParam::isRequired) : cmd.getParams();

        List<char[]> evolutions = new ArrayList<>();
        int numOptions = parserTypes.getAllTypes().size();
        char[] noParamEvol = new char[numOptions];
        for(int i = 0; i < noParamEvol.length; i++)		noParamEvol[i] = '0';
        evolutions.add(noParamEvol);

        Set<Enum<?>> usedOptions = new HashSet<>();

        // Analyze first all params without option dependency
        List<CParam> noDeps = JkStreams.filter(params, cpar -> cpar.getDependOn() == null);
        for(CParam cp : noDeps) {
            List<char[]> tot = new ArrayList<>();
            for (COption co : cp.getOptions()) {
                evolutions.forEach(arr -> tot.add(setUpChar(arr, co)));
                usedOptions.add(co.getArgType());
            }

            // if param is optional, previous evolutions must be holds
            if (!cp.isRequired()) {
                tot.addAll(evolutions);
            }

            evolutions = tot;
        }

        if(!onlyRequired) {
            // Now analyze params with option dependency
            List<CParam> withDeps = JkStreams.filter(params, cpar -> cpar.getDependOn() != null);
            while (!withDeps.isEmpty()) {
                // a param can depends on another param that in turn has an option dependency
                // to avoid problem in computing evolution, a param is analyzed only if his dependency is already analyzed
                int idx = -1;
                for (int i = 0; i < withDeps.size() && idx == -1; i++) {
                    if (usedOptions.contains(withDeps.get(i).getDependOn())) {
                        idx = i;
                    }
                }

                if (idx == -1) {
                    throw new DesignError(cmdsClass, "command {}: wrong option dependencies ({} not found)", cmd.getCmdName(), JkStreams.map(withDeps, CParam::getDependOn));
                }

                CParam removed = withDeps.remove(idx);
                int depIdx = removed.getDependOn().ordinal();
                List<char[]> sourceEvols = JkStreams.filter(evolutions, arr -> arr[depIdx] == '1');
                List<char[]> tot = new ArrayList<>();
                for (COption co : removed.getOptions()) {
                    sourceEvols.forEach(arr -> tot.add(setUpChar(arr, co)));
                    usedOptions.add(co.getArgType());
                }

                if (!removed.isRequired()) {
                    tot.addAll(sourceEvols);
                }

                evolutions.removeIf(arr -> arr[depIdx] == '1');
                evolutions.addAll(tot);
            }
        }

        return JkStreams.map(evolutions, String::new);
    }

    private char[] setUpChar(char[] arr, COption co) {
        int index = co.getArgType().ordinal();
        char[] evol = Arrays.copyOf(arr, arr.length);
        evol[index] = '1';
        return evol;
    }

    private boolean containsEvolutionRequired(CmdWrapper cw, String evolution) {
        for(String str : cw.getEvolutions()) {
            boolean valid = true;
            for(int i = 0; valid && i < str.length(); i++) {
                char charStr = str.charAt(i);
                char charEv = evolution.charAt(i);
                if(charStr == '1' && charEv == '0') {
                    valid = false;
                }
            }
            if(valid) return true;
        }
        return false;
    }

}
