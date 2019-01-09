package stuff;

import org.junit.Test;
import xxx.joker.libs.core.ToAnalyze;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

@ToAnalyze
@Deprecated
public class Various {

    @Test
    public void testStringArgsSplit() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("C:\\Users\\f.barbano\\IdeaProjects\\LIBS\\java-libs\\args-parser\\src\\test\\resources\\file.txt"));
        lines.forEach(line -> {
            display("\nSTRING: |%s|", line);
            String[] args = splitString(line);
            for(int i = 0; i < args.length; i++) {
                display("ARG[%d] = |%s|", i, args[i]);
            }
        });
    }

    private String[] splitString(String inputStr) {
        String str = JkStrings.safeTrim(inputStr);
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
