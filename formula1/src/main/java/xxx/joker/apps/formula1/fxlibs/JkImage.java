package xxx.joker.apps.formula1.fxlibs;

import javafx.scene.image.Image;
import xxx.joker.apps.formula1.model.entities.F1Resource;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class JkImage {

    private Path path;
    private int width;
    private int height;
    private Image image;
    private String description;

    private JkImage() {

    }

    public static JkImage from(F1Resource resource) {
        JkImage res = new JkImage();
        res.path = resource.getPath();
        res.width = resource.getWidth();
        res.height = resource.getHeight();
        res.description = resource.getKey();
        return res;
    }
    public static JkImage parse(Path imgPath) {
        try {
            JkImage res = new JkImage();
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
