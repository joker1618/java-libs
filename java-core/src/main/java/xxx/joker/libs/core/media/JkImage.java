package xxx.joker.libs.core.media;

import javafx.scene.image.Image;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkImage implements JkFormattable {

    private static final String FIELD_SEP = "**";

    private Path path;
    private int width;
    private int height;

    private Image fxImage;

    public JkImage() {

    }

    @Override
    public String format() {
        return strf("{}{}{}{}{}", JkEnvironment.relativizeAppsPath(path), FIELD_SEP, width, FIELD_SEP, height);
    }

    @Override
    public JkImage parse(String str) {
        String[] split = JkStrings.splitArr(str, FIELD_SEP);
        setPath(JkEnvironment.toAbsoluteAppsPath(split[0]));
        setWidth(Integer.valueOf(split[1]));
        setHeight(Integer.valueOf(split[2]));
        return this;
    }

    public Image toFxImage() {
        if(fxImage == null) {
            fxImage = new Image(JkFiles.toURL(path));
        }
        return fxImage;
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
    public double getRatio() {
        return  (double) width / height;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }

}
