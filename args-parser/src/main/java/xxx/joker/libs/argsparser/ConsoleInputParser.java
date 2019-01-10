package xxx.joker.libs.argsparser;

import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.argsparser.exceptions.ParseError;
import xxx.joker.libs.argsparser.service.CmdWrapper;
import xxx.joker.libs.argsparser.service.DesignService;
import xxx.joker.libs.argsparser.service.DesignServiceImpl;
import xxx.joker.libs.core.utils.JkStrings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ConsoleInputParser implements InputParser {

    private DesignService designService;

    public ConsoleInputParser(Class<? extends JkAbstractArgs> argsClass,
                              Class<? extends JkArgsNames> argsNamesClass,
                              Class<? extends JkCommands> cmdsClass)
                              throws DesignError {

        designService = new DesignServiceImpl(argsClass, argsNamesClass, cmdsClass);
    }



    @Override
    public <T extends JkAbstractArgs> T parse(String inputLine) throws ParseError {
        String[] args = splitArgsLine(inputLine);
        return parse(args);
    }

    @Override
    public <T extends JkAbstractArgs> T parse(String[] inputArgs) throws ParseError {
        /* 1. Find input command */
        CmdWrapper selCmd = retrieveInputCommand(inputArgs);

        /* 2. Create new instance of args class */
        T argsInstance = createArgsInstace();

        /* 3. Set selected command */


        /* 4. Set all arguments */


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

    private CmdWrapper retrieveInputCommand(String[] args) {
        // todo impl
        return null;
    }

    private <T extends JkAbstractArgs> T createArgsInstace() {
        //todo impl
        return null;
    }

    private void setFieldValue(JkAbstractArgs iargs, Field field, Object value) {
        //todo impl

    }
}
