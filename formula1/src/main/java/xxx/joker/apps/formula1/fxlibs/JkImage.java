package xxx.joker.apps.formula1.fxlibs;

import javafx.scene.image.Image;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class JkImage {

    private Path path;
    private Image image;
    private int width;
    private int height;

    private JkImage() {

    }

    public static JkImage parse(Path imgPath) {
        try {
            JkImage res = new JkImage();
            res.path = imgPath;
            res.image = new Image(JkFiles.toURL(imgPath), true);
            BufferedImage img = ImageIO.read(imgPath.toFile());
            res.width = img.getWidth();
            res.height = img.getHeight();
            return res;

        } catch (IOException ex) {
            throw new JkRuntimeException(ex, "Error parsing image file {}", imgPath);
        }
    }

    public Path getPath() {
        return path;
    }

    public Image getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
