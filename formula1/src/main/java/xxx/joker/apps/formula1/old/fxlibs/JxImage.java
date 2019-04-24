package xxx.joker.apps.formula1.old.fxlibs;

import javafx.scene.image.Image;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public class JxImage {

    private Path path;
    private int width;
    private int height;
    private Image image;
    private String description;

    private JxImage() {

    }

    public static JxImage parse(Path imgPath) {
        try {
            JxImage res = new JxImage();
            res.path = imgPath;
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Image getImage() {
        if(image == null) {
            String localUrl = JkFiles.toURL(path);
            image = new Image(localUrl, true);
        }
        return image;
    }

    public double getRatio() {
        return  (double) width / height;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
