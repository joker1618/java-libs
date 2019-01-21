package trymodel;

import org.junit.Test;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class TestModel {

    static Path DB_FOLDER = Paths.get("C:\\Users\\f.barbano\\IdeaProjects\\LIBS\\java-libs\\java-repo\\src\\test\\resources\\db");

    @Test
    public void removeInsertTstamp() {
        List<Path> files = JkFiles.findFiles(DB_FOLDER, false, p -> p.toString().endsWith("jkrepo"));
        for(Path file : files) {
            List<String> lines = new ArrayList<>();
            for(String line : JkFiles.readLines(file)) {
                int idxA = line.indexOf("##FLD##");
                int idxB = line.substring(idxA+7).indexOf("##FLD##") + idxA+7;
                lines.add(line.substring(0, idxA)+line.substring(idxB));
            }
            JkFiles.writeFile(Paths.get(file.toString()+"_fair"), lines, true);
        }
    }
}
