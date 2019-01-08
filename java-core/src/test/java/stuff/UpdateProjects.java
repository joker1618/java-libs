package stuff;

import org.junit.Test;
import xxx.joker.libs.core.files.JkFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class UpdateProjects {

    @Test
    public void update() throws IOException {
        Path appFolder = Paths.get("C:\\Users\\feder\\IdeaProjects\\APPS\\f1-manager");

        List<Path> files = JkFiles.findFiles(appFolder, true, f -> f.toString().endsWith(".java"));

        for(Path file : files) {
            List<String> lines = Files.readAllLines(file);
            List<String> newLines = new ArrayList<>();
            int eqLines = 0;
            for(String s : lines) {
                if(s.contains("xxx.joker.libs.javalibs.media")) {
                    newLines.add(s.replace("xxx.joker.libs.javalibs.media", "xxx.joker.libs.media"));

                } else if(s.contains("xxx.joker.libs.javalibs.excel")) {
                    newLines.add(s.replace("xxx.joker.libs.javalibs.excel", "xxx.joker.libs.excel"));

                } else if(s.contains("xxx.joker.libs.javalibs.language")) {
                    newLines.add(s.replace("xxx.joker.libs.javalibs.language", "xxx.joker.libs.language"));

                } else if(s.contains("xxx.joker.libs.javalibs.javafx")) {
                    newLines.add(s.replace("xxx.joker.libs.javalibs.javafx", "xxx.joker.libs.javafx"));

                } else if(s.contains("xxx.joker.libs.javalibs")) {
                    newLines.add(s.replace("xxx.joker.libs.javalibs", "xxx.joker.libs.core"));

                } else {
                    newLines.add(s);
                    eqLines++;
                }
            }

            if(eqLines < lines.size()) {
                JkFiles.writeFile(file, newLines, true);
                display("File %s modified", file);
            } else {
                display("File %s unchanged", file);
            }
        }

    }
}
