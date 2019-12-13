package junit.format;

import junit.format.beans.Container;
import junit.format.beans.SimpleObject;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import xxx.joker.libs.core.file.JkEncryption;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.format.JkFormatter;
import xxx.joker.libs.core.runtime.JkReflection;
import xxx.joker.libs.core.test.JkDataTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConsole.displayColl;

public class TestFormat {

    @Test
    public void testContainer() {
        Path csvPath = Paths.get("src/test/resources/format/sobj.csv");


        JkDataTest data = new JkDataTest(666);
        List<SimpleObject> list = Stream.generate(() -> new SimpleObject(data)).limit(5).collect(Collectors.toList());
        list.get(0).setToNullForTest();

        JkFormatter fmt = JkFormatter.get();
        List<String> lines = fmt.formatCsv(list);
        JkFiles.writeFile(csvPath, lines);
        if(!fmt.getErrorsFormat().isEmpty()) {
            display("FORMAT ERRORS:\n{}", fmt.getErrorsFormat());
        }

        Path csvPath2 = Paths.get(csvPath.toString()+"2");
        List<SimpleObject> parsedList = fmt.parseCsv(csvPath, SimpleObject.class);
        JkFiles.writeFile(csvPath2, fmt.formatCsv(parsedList));
        if(!fmt.getErrorsParse().isEmpty()) {
            display("PARSE ERRORS:\n{}", fmt.getErrorsParse());
        }
        if(!fmt.getErrorsFormat().isEmpty()) {
            display("FORMAT ERRORS:\n{}", fmt.getErrorsFormat());
        }


        display("%-10s %s", csvPath.getFileName(), JkEncryption.getMD5(csvPath));
        display("%-10s %s", csvPath2.getFileName(), JkEncryption.getMD5(csvPath2));

        displayColl(parsedList, el -> ToStringBuilder.reflectionToString(el, ToStringStyle.NO_CLASS_NAME_STYLE));
    }
}
