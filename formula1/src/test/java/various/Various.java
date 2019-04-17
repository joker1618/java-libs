package various;

import org.junit.Test;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;
import xxx.joker.libs.core.files.JkFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Various {

    @Test
    public void tmp() throws Exception {
        F1ModelImpl.getInstance().getGranPrixs(2018).forEach(gp -> display(gp.strFull()));
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
