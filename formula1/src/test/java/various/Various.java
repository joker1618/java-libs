package various;

import org.junit.Test;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.apps.formula1.model.entities.F1Race;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Various {

    @Test
    public void var() throws Exception {
        F1ModelImpl.getInstance().getGranPrixs().stream()
                .flatMap(gp -> gp.getRaces().stream())
                .filter(r -> r.getTime() != null)
                .filter(r -> r.getTime().toMillis() < (1000*60*10))
                .map(F1Race::getPrimaryKey)
                .distinct()
                .forEach(System.out::println);
    }
}
