package xxx.joker.libs.argsparser;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;
import xxx.joker.libs.argsparser.service.ArgWrapper;
import xxx.joker.libs.argsparser.service.CmdWrapper;
import xxx.joker.libs.argsparser.service.DesignService;
import xxx.joker.libs.argsparser.service.DesignServiceImpl;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.runtime.JkReflection;
import xxx.joker.libs.core.test.JkTests;
import xxx.joker.libs.core.util.JkConvert;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ConsoleInputParser implements InputParser {

    private DesignService designService;
    private Class<? extends JkAbstractArgs> argsClass;

    public ConsoleInputParser(Class<? extends JkAbstractArgs> argsClass,
                              Class<? extends JkArgsTypes> argsNamesClass,
                              Class<? extends JkCommands> cmdsClass)
                              throws DesignError {

        this.argsClass = argsClass;
        this.designService = new DesignServiceImpl(argsClass, argsNamesClass, cmdsClass, false);
    }
    public ConsoleInputParser(Class<? extends JkAbstractArgs> argsClass,
                              Class<? extends JkArgsTypes> argsNamesClass,
                              Class<? extends JkCommands> cmdsClass,
                              boolean ignoreCaseArgs)
                              throws DesignError {

        this.argsClass = argsClass;
        this.designService = new DesignServiceImpl(argsClass, argsNamesClass, cmdsClass, ignoreCaseArgs);
    }



    @Override
    public <T extends JkAbstractArgs> T parse(String inputLine) throws ParseError {
        String[] args = InputParser.splitArgsLine(inputLine);
        return parse(args);
    }

    @Override
    public <T extends JkAbstractArgs> T parse(String[] inputArgs) throws ParseError {
        /* 0. Parse input args */
        Map<ArgWrapper, List<String>> argsMap = inputArgsToMap(inputArgs);

        /* 1. Find input command */
        CmdWrapper selCmd = designService.retrieveCommand(argsMap.keySet());
        if(selCmd == null) {
            throw new ParseError("No command found for input: {}", Arrays.toString(inputArgs));
        }

        /* 2. Create new instance of args class */
        T argsInstance = createArgsInstance();

        /* 3. Set selected command */
        argsInstance.setSelectedCommand(selCmd.getCmd());

        /* 4. Set all arguments on arg instance */
        argsMap.forEach((aw, values) -> {
            parseShellInput(argsInstance, selCmd, aw, values);
        });

        List<CParam> defList = JkStreams.filter(selCmd.getParams(), cp -> cp.getDefault() != null);
        List<JkArgsTypes> found = JkStreams.map(argsMap.keySet(), ArgWrapper::getArgType);
        defList.removeIf(cp -> !JkStreams.mapFilter(cp.getOptions(), co -> (JkArgsTypes)co.getArgType(), found::contains).isEmpty());
        defList.forEach(def -> {
            ArgWrapper aw = designService.getArgByNameAlias(def.getDefault().getArgName());
            parseShellInput(argsInstance, selCmd, aw, Collections.emptyList());
        });

        return argsInstance;
    }


    private <T extends JkAbstractArgs> T createArgsInstance() {
        try {
             return (T) argsClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ParseError("Unable to create instance of {}", argsClass);
        }
    }

    private void parseShellInput(JkAbstractArgs ai, CmdWrapper cw, ArgWrapper aw, List<String> values) {
        COption co = cw.getOption(aw.getArgType());
        Class<?> argClass = co.getArgClass();

        // check values cardinality
        if(!argClass.isArray()) {
            if(argClass == boolean.class) {
                if(!values.isEmpty()) {
                    throw new ParseError(cw, aw, "values not allowed for boolean arg");
                }
            } else if(values.size() != 1) {
                throw new ParseError(cw, aw, "only one value allowed for {} args (found {} values)", argClass.getName(), values.size());
            }
        }

        Object fieldValue;
        if(argClass == boolean.class) {
            fieldValue = true;

        } else {
            Predicate<String[]> classCheck;
            Function<String[], Object[]> classConverter;
            DateTimeFormatter dtf = co.getDateTimeFormatter();

            if(argClass == Integer.class || argClass == Integer[].class) {
                classCheck = JkTests::areInts;
                classConverter = JkConvert::toInts;
            } else if(argClass == Double.class || argClass == Double[].class) {
                classCheck = JkTests::areDoubles;
                classConverter = JkConvert::toDoubles;
            } else if(argClass == Long.class || argClass == Long[].class) {
                classCheck = JkTests::areLongs;
                classConverter = JkConvert::toLongs;
            } else if(argClass == Path.class || argClass == Path[].class) {
                classCheck = arr -> true;
                classConverter = JkFiles::toPaths;
            } else if(argClass == LocalDate.class || argClass == LocalDate[].class) {
                classCheck = sarr -> JkDates.areDates(sarr, dtf);
                classConverter = sarr -> JkStreams.map(Arrays.asList(sarr), s -> LocalDate.parse(s, dtf)).toArray();
            } else if(argClass == LocalTime.class || argClass == LocalTime[].class) {
                classCheck = sarr -> JkDates.areTimes(sarr, dtf);
                classConverter = sarr -> JkStreams.map(Arrays.asList(sarr), s -> LocalTime.parse(s, dtf)).toArray();
            } else if(argClass == LocalDateTime.class || argClass == LocalDateTime[].class) {
                classCheck = sarr -> JkDates.areDateTimes(sarr, dtf);
                classConverter = sarr -> JkStreams.map(Arrays.asList(sarr), s -> LocalDateTime.parse(s, dtf)).toArray();
            } else if(argClass == String.class || argClass == String[].class) {
                classCheck = arr -> true;
                classConverter = arr -> arr;
            } else {
                throw new ParseError(cw, aw, "wrong expected class {}", argClass);
            }

            try {
                fieldValue = parseInputValues(co, values, classCheck, classConverter);
            } catch(ParseError err) {
                throw new ParseError(cw, aw, err.getMessage());
            }
        }

        // Set value to instance field
        try {
            JkReflection.setFieldValue(ai, aw.getField(), fieldValue);
        } catch (JkRuntimeException e) {
            throw new ParseError(cw, aw, "field {}: error setting value ({})", aw.getField().getName(), fieldValue);
        }
    }

    private Object parseInputValues(COption co, List<String> values, Predicate<String[]> classCheck, Function<String[], Object[]> classConverter) {
        String[] valuesArr = values.toArray(new String[0]);

        // 1. Checks before
        String[] tmp1 = valuesArr;
        co.getChecksBefore().forEach(check -> {
            String errorMex = check.apply(tmp1);
            if(StringUtils.isNotBlank(errorMex)) {
                throw new ParseError(errorMex);
            }
        });

        // 2. Transform before
        if(co.getTransformBefore() != null) {
            valuesArr = co.getTransformBefore().apply(valuesArr);
        }

        // 3. Checks middle
        String[] tmp2 = valuesArr;
        co.getChecksMiddle().forEach(check -> {
            String errorMex = check.apply(tmp2);
            if(StringUtils.isNotBlank(errorMex)) {
                throw new ParseError(errorMex);
            }
        });

        // 4. Cast values to the right class
        if(!classCheck.test(valuesArr)) {
            throw new ParseError("unable to cast values to {}", co.getArgClass());
        }
        Object[] objArr = classConverter.apply(valuesArr);

        // 5. Transform after
        if(co.getTransformAfter() != null) {
            objArr = co.getTransformAfter().apply(objArr);
        }

        // 5. Checks after
        Object[] tmp3 = objArr;
        co.getChecksAfter().forEach(check -> {
            String errorMex = check.apply(tmp3);
            if(StringUtils.isNotBlank(errorMex)) {
                throw new ParseError(errorMex);
            }
        });

        return co.getArgClass().isArray() ? objArr : objArr[0];
    }

    private Map<ArgWrapper, List<String>> inputArgsToMap(String[] inputArgs) {
        Map<ArgWrapper, List<String>> map = new HashMap<>();

        if(inputArgs.length > 0) {
            ArgWrapper optWrapper = null;
            List<String> values = null;

            for (int i = 0; i < inputArgs.length; i++) {
                String elem = inputArgs[i];
                ArgWrapper arg = designService.getArgByNameAlias(elem);
                if (arg == null) {
                    if (i == 0) {
                        throw new ParseError("First arg is not an option");
                    }
                    values.add(elem);
                } else {
                    if (i > 0) {
                        if (map.containsKey(arg)) {
                            throw new ParseError("Option [{}] duplicated", elem);
                        }
                        map.put(optWrapper, values);
                    }
                    optWrapper = arg;
                    values = new ArrayList<>();
                }
            }

            map.put(optWrapper, values);
        }

        return map;
    }

}
