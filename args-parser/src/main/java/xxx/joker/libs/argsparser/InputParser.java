package xxx.joker.libs.argsparser;

import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.exceptions.ParseError;
import xxx.joker.libs.core.util.JkStrings;

import java.util.ArrayList;
import java.util.List;

public interface InputParser {

    <T extends JkAbstractArgs> T parse(String inputLine) throws ParseError;

    <T extends JkAbstractArgs> T parse(String[] inputArgs) throws ParseError;

    static String[] splitArgsLine(String lineArgs) {
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
}
