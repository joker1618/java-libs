package xxx.joker.libs.argsparser;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;
import xxx.joker.libs.argsparser.service.ArgWrapper;
import xxx.joker.libs.argsparser.service.CmdWrapper;
import xxx.joker.libs.argsparser.service.DesignService;
import xxx.joker.libs.argsparser.service.DesignServiceImpl;
import xxx.joker.libs.core.datetime.JkTimes;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.tests.JkChecks;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.lang.reflect.Field;
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
                              Class<? extends JkCommands> cmdsClass,
                              boolean ignoreCaseArgs)
                              throws DesignError {

        this.argsClass = argsClass;
        this.designService = new DesignServiceImpl(argsClass, argsNamesClass, cmdsClass, ignoreCaseArgs);
    }



    @Override
    public <T extends JkAbstractArgs> T parse(String inputLine) throws ParseError {
        String[] args = splitArgsLine(inputLine);
        return parse(args);
    }

    @Override
    public <T extends JkAbstractArgs> T parse(String[] inputArgs) throws ParseError {
        /* 0. Parse input args */
        Map<ArgWrapper, List<String>> argsMap = inputArgsToMap(inputArgs);

        /* 1. Find input command */
        CmdWrapper selCmd = designService.retrieveCommand(argsMap.keySet());

        /* 2. Create new instance of args class */
        T argsInstance = createArgsInstance();

        /* 3. Set selected command */
        argsInstance.setSelectedCommand(selCmd.getCmd());

        /* 4. Set all arguments on arg instance */
        argsMap.forEach((aw, values) -> {
            parseShellInput(argsInstance, selCmd, aw, values);
        });

        return argsInstance;
    }


    // Parse and split line into String[], like shell do
    private String[] splitArgsLine(String lineArgs) {
        String str = JkStrings.safeTrim(lineArgs);
        if(str.isEmpty())   return new String[0];

        List<String> argList = new ArrayList<>();

        while(!str.isEmpty()) {
            if(str.charAt(0) == '"' || str.charAt(0) == '\'') {
                int idx = -1;
                int skip = 1;
                while(idx == -1) {
                    int indexFound = str.substring(skip).indexOf(str.charAt(0));
                    if(indexFound == -1) {
                        return null;
                    }

                    if(indexFound == 0 || str.charAt(skip+indexFound-1) != '\\') {
                        idx = skip + indexFound;
                    } else {
                        skip += indexFound + 1;
                    }
                }
                argList.add(str.substring(1, idx).replace("\\\"", "\"").replace("\\'", "'"));
                str = str.substring(idx+1).trim();

            } else {
                int idx = str.indexOf(' ');
                if(idx == -1) {
                    idx = str.length();
                }
                argList.add(str.substring(0, idx));
                str = str.substring(idx).trim();
            }
        }

        return argList.toArray(new String[0]);
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
                throw new ParseError(cw, aw, "only one value allowed for {} args (found {} values)", argClass, values.size());
            }
        }

        Object fieldValue;
        if(argClass == boolean.class) {
            fieldValue = true;

        } else {
            Predicate<String[]> classCheck;
            Function<String[], Object[]> classConverter;
            DateTimeFormatter dtf = co.getDateTimeFormatter();

            if(argClass == Integer.class || argClass == int[].class) {
                classCheck = JkChecks::areInts;
                classConverter = JkConvert::toInts;
            } else if(argClass == Double.class || argClass == double[].class) {
                classCheck = JkChecks::areDoubles;
                classConverter = JkConvert::toDoubles;
            } else if(argClass == Long.class || argClass == long[].class) {
                classCheck = JkChecks::areLongs;
                classConverter = JkConvert::toLongs;
            } else if(argClass == Path.class || argClass == Path[].class) {
                classCheck = arr -> true;
                classConverter = JkFiles::toPaths;
            } else if(argClass == LocalDate.class || argClass == LocalDate[].class) {
                classCheck = sarr -> JkTimes.areLocalDates(sarr, dtf);
                classConverter = sarr -> JkStreams.map(Arrays.asList(sarr), s -> LocalDate.parse(s, dtf)).toArray();
            } else if(argClass == LocalTime.class || argClass == LocalTime[].class) {
                classCheck = sarr -> JkTimes.areLocalTimes(sarr, dtf);
                classConverter = sarr -> JkStreams.map(Arrays.asList(sarr), s -> LocalTime.parse(s, dtf)).toArray();
            } else if(argClass == LocalDateTime.class || argClass == LocalDateTime[].class) {
                classCheck = sarr -> JkTimes.areLocalDateTimes(sarr, dtf);
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
                throw new ParseError(cw, aw, err.getErrorMex());
            }
        }

        setFieldValue(ai, aw.getField(), fieldValue);
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

    private void setFieldValue(JkAbstractArgs ai, Field field, Object value) {
        try {
            if(field.isAccessible()) {
                field.set(ai, value);
            } else {
                field.setAccessible(true);
                field.set(ai, value);
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new ParseError("Error while set value ({}) for field ({})", value, field.getName());
        }
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
