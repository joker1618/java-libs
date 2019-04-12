package various;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Various {

    @Test
    public void testImageIO() throws IOException {
        BufferedImage img = ImageIO.read(new File("C:\\Users\\fede\\.appsFolder\\formula1\\images\\icons\\flags\\Australia.png"));
        int width = img.getWidth();
        int height = img.getHeight();
        display(width+"x"+height);
    }
}
