package various;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import xxx.joker.apps.formula1.dataCreator.common.F1Const;
import xxx.joker.apps.formula1.dataCreator.model.F1ModelImpl;
import xxx.joker.apps.formula1.dataCreator.model.entities.F1Race;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Various {

    @Test
    public void tmp() throws Exception {
        display(F1ModelImpl.getInstance().getGranPrixs(2018).get(0).strFull(ToStringStyle.MULTI_LINE_STYLE));
    }

    @Test
    public void checkTimesRange() throws Exception {
        F1ModelImpl.getInstance().getGranPrixs().stream()
                .flatMap(gp -> gp.getRaces().stream())
                .filter(r -> r.getTime() != null)
                .filter(r -> r.getTime().toMillis() < (1000*60*10))
                .map(F1Race::getPrimaryKey)
                .distinct()
                .forEach(System.out::println);
    }

    @Test
    public void checkWeirdCharsInRepoFiles() throws Exception {
        List<Path> files = JkFiles.findFiles(F1Const.DB_FOLDER, false,
                p -> JkFiles.getFileName(p).startsWith(F1Const.DB_NAME + "#"),
                p -> JkFiles.getFileName(p).contains("#jkrepo")
        );
//        files.forEach(p -> display("{}", p));
//        display("###################################");
        files.forEach(f -> {
            int c = 0;
            int e = 0;
            for (String line : JkFiles.readLines(f)) {
                for (char ch : line.toCharArray()) {
                    int b = (int)ch;
                    if(b >= 128 && b <= 165) {
                        e++;
                    } else if(!(b >= 32 && b <= 126)) {
                        c++;
                        display("{} {}", b, ch);
                    }
                }

            }
            display("{}: {}/{}", f, e, c);
        });
    }
}
