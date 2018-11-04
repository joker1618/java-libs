package xxx.joker.libs.media;

import java.nio.file.Path;

/**
 * Created by f.barbano on 12/07/2017.
 */
public class JkPictureInfo {

	private Path imagePath;
	private int width;
	private int height;


	protected JkPictureInfo() {
	    
	}

    public Path getImagePath() {
        return imagePath;
    }

    public void setImagePath(Path imagePath) {
        this.imagePath = imagePath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
