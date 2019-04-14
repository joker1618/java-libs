package xxx.joker.libs.core.media;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.objects.Dim;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class JkMedia {

    public static Dim getImageDim(Path imgPath) {
        try {
            BufferedImage img = ImageIO.read(imgPath.toFile());
            return new Dim(img.getWidth(), img.getHeight());
        } catch (IOException ex) {
            throw new JkRuntimeException(ex, "Error parsing image file {}", imgPath);
        }
    }

}
