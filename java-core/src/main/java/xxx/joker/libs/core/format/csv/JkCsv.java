package xxx.joker.libs.core.format.csv;

import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.util.JkStrings;
import xxx.joker.libs.core.util.JkStruct;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static xxx.joker.libs.core.format.csv.CsvConst.NEWLINE;
import static xxx.joker.libs.core.format.csv.CsvConst.SEP_FIELD;
import static xxx.joker.libs.core.lambda.JkStreams.join;
import static xxx.joker.libs.core.lambda.JkStreams.map;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkStrings.splitList;

public class JkCsv {

    private List<String> header;
    private List<JkCsvRow> data;

    public JkCsv() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public JkCsv(String csvText) {
        this(csvText, SEP_FIELD.getSeparator());
    }
    public JkCsv(String csvText, String fieldSep) {
        List<String> lines = splitList(csvText, NEWLINE.getSeparator());
        this.header = new ArrayList<>();
        this.data = new ArrayList<>();
        if(!lines.isEmpty()) {
            this.header = splitList(lines.remove(0), fieldSep);
            this.data = map(lines, line -> new JkCsvRow(header, splitList(line, fieldSep)));
        }
    }

    public JkCsv(List<String> header, List<List<String>> dataLines) {
        this.header = new ArrayList<>(header);
        this.data = map(dataLines, line -> new JkCsvRow(header, line));
    }

    private void resetChanges() {
        data.forEach(JkCsvRow::resetChanges);
    }

    public static JkCsv readFile(Path p) {
        return readFile(p, SEP_FIELD.getSeparator());
    }
    public static JkCsv readFile(Path p, String fieldSep) {
        List<String> lines = JkFiles.readLinesNotBlank(p);
        JkCsv csv = new JkCsv();
        if(!lines.isEmpty()) {
            csv.header.addAll(splitList(lines.remove(0), fieldSep));
            csv.data.addAll(map(lines, line -> new JkCsvRow(csv.header, splitList(line, fieldSep))));
        }
        return csv;
    }

    public List<String> getHeader() {
        return header;
    }

    public List<JkCsvRow> getData() {
        return data;
    }

    public void persist(Path outPath) {
        List<String> lines = new ArrayList<>();
        lines.add(join(header, SEP_FIELD.getSeparator()));
        data.forEach(cr -> lines.add(join(cr.getCurrentData(), SEP_FIELD.getSeparator())));
        JkFiles.writeFile(outPath, lines);
    }

    public void removeCol(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header, colName);
        if(index != -1) {
            data.forEach(cr -> cr.removeCol(colName));
        }
    }
}
