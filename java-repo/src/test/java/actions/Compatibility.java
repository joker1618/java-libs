package actions;

import org.junit.Test;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.format.csv.CsvConst.*;
import xxx.joker.libs.core.format.csv.CsvPlaceholder;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.repo.JkRepoFile;

import javax.sql.StatementEventListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.format.csv.CsvConst.*;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkStrings.strf;
import static xxx.joker.libs.repo.config.RepoConfig.*;

public class Compatibility {

    Path repoFolder = Paths.get("");
    String dbName = "";


    @Test
    public void modifyFormats() {
        Path repoPath = Paths.get("");

        // Changes 1 - file name -> from 'jkrepo' to 'jkRepo'
        String oldKeyword = "jkrepo";
        List<Path> dbPaths = JkFiles.findFiles(repoPath.resolve(FOLDER_DB), false, p -> p.getFileName().toString().contains(oldKeyword));
        Map<Path, Path> dbPathMap = JkStreams.toMapSingle(dbPaths, p -> p, p -> JkFiles.getParent(p).resolve(p.getFileName().toString().replace(oldKeyword, KEYWORD_JKREPO)));
        dbPathMap.forEach((o,n) -> {
            Path mid = JkFiles.moveSafe(o, Paths.get(o.toString() + ".ren.tmp"));
            JkFiles.move(mid, n);
        });

        // Change 2 - placeholders -> from @_VAL_@ to @VAL@
        List<CsvPlaceholder> phList = toList(SEP_FIELD, SEP_LIST, SEP_MAP_ENTRIES, SEP_KEY_VALUE, TAB, NEWLINE);
        Map<String, String> phMap = new HashMap<>();
        for (CsvPlaceholder csvPh : phList) {
            String newPh = strf("{}0{}", PH_LEVEL, csvPh.getPlaceholder());
            String oldPh = strf("@_{}_@", csvPh.getPlaceholder().replace("@", ""));
            phMap.put(oldPh, newPh);
        }
        for (String newPh : Arrays.asList(TAB.getPlaceholder(), NEWLINE.getPlaceholder(), PH_NULL)) {
            String oldPh = strf("@_{}_@", newPh.replace("@", ""));
            phMap.put(oldPh, newPh);
        }
        List<Path> dbPathsNew = JkFiles.findFiles(repoPath.resolve(FOLDER_DB), false, p -> p.getFileName().toString().contains(KEYWORD_JKREPO));
        for (Path dbPath : dbPathsNew) {
            List<String> lines = JkFiles.readLines(dbPath);
            List<String> newLines = JkStreams.map(lines, l -> {
                for (Map.Entry<String, String> e : phMap.entrySet()) {
                    l = l.replace(e.getKey(), e.getValue());
                }
                return l;
            });
            JkFiles.writeFile(dbPath, newLines);
        }

        // Changes 3 - file name -> use "." instead "#"
        dbPathMap = JkStreams.toMapSingle(dbPathsNew, p -> p, p -> JkFiles.getParent(p).resolve(p.getFileName().toString().replace("#", ".")));
        dbPathMap.forEach(JkFiles::move);

        // Changed 4 - fkey.simple && {}.KEYWORD_JKREPO
        dbPaths = JkFiles.findFiles(repoPath.resolve(FOLDER_DB), false, p -> p.getFileName().toString().contains(KEYWORD_JKREPO));
        for (Path p : dbPaths) {
            String oldName = p.getFileName().toString();
            String newName;
            if(!oldName.contains("fkeys")) {
                String str = oldName.replace("." + KEYWORD_JKREPO, "");
                int idx = str.indexOf(".");
                newName = str.substring(0, idx) + "." + KEYWORD_JKREPO + str.substring(idx);
                Path newPath = JkFiles.getParent(p).resolve(newName);
                JkFiles.move(p, newPath);
            }
        }

        // Change 5 - fkeys file format
        String endFkeysFName = FORMAT_FKEYS_FILENAME.replaceAll("^\\{}", "");
        dbPaths = JkFiles.findFiles(repoPath.resolve(FOLDER_DB), false, p -> p.getFileName().toString().endsWith(endFkeysFName));
        if(dbPaths.size() == 1) {
            Path p = dbPaths.get(0);
            List<String> lines = JkFiles.readLines(p);
            List<String> newLines = JkFiles.readLines(p);
            newLines.add("sourceID|fieldName|idxKey|mapKey|idxValue|value");

            String oldName = p.getFileName().toString();
            String newName;
            if(!oldName.contains("fkeys")) {
                String str = oldName.replace("." + KEYWORD_JKREPO, "");
                int idx = str.indexOf(".");
                newName = str.substring(0, idx) + "." + KEYWORD_JKREPO + str.substring(idx);
                Path newPath = JkFiles.getParent(p).resolve(newName);
                JkFiles.move(p, newPath);
            }
        }


        //
    }

    @Test
    public void fixRepo() {
        // Change  a1 - added size to RepoResource

    }

    private class RepoData extends JkRepoFile {

        protected RepoData(Path repoFolder, String dbName, String... packages) {
            super(repoFolder, dbName, packages);
        }
    }
}

