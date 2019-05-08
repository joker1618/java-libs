package xxx.joker.libs.core.javafx;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;

public class JfxUtil {

	public static ImageView createImageView(Integer fitWidth, Integer fitHeight) {
		return createImageView1(null, fitWidth, fitHeight);
	}
	public static ImageView createImageView(Path imgPath, Integer fitWidth, Integer fitHeight) {
		return createImageView(new Image(JkFiles.toURL(imgPath)), fitWidth, fitHeight);
	}
	public static ImageView createImageView(Image image, Integer fitWidth, Integer fitHeight) {
		return createImageView1(image, fitWidth, fitHeight);
	}
	private static ImageView createImageView1(Image image, Integer fitWidth, Integer fitHeight) {
		ImageView imageView = new ImageView();
		if(image != null)	imageView.setImage(image);
		if(fitWidth != null)	imageView.setFitWidth(fitWidth);
		if(fitHeight != null)	imageView.setFitHeight(fitHeight);
		imageView.setPreserveRatio(true);
		return imageView;
	}

	public static Window getWindow(Event e) {
		return ((Node)e.getSource()).getScene().getWindow();
	}

	public static Stage getStage(Event e) {
		return (Stage)getWindow(e);
	}

	public static <T extends Node> T getChildren(Node root, int... childrenIndexes) {
		try {
			Node tmp = root;
			for (int idx : childrenIndexes) {
				tmp = ((Pane)tmp).getChildren().get(idx);
			}
			return (T) tmp;

		} catch (ClassCastException ex) {
			throw new JkRuntimeException(ex);
		}
	}
}
