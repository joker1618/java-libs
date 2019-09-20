package xxx.joker.libs.core.javafx;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.util.List;

public class JfxControls {

	public static ImageView createImageView(Double fitWidth, Double fitHeight) {
		return createImageView1(null, fitWidth, fitHeight, true);
	}
	public static ImageView createImageView(Double fitWidth, Double fitHeight, boolean preserveRatio) {
		return createImageView1(null, fitWidth, fitHeight, preserveRatio);
	}
	public static ImageView createImageView(Path imgPath, int fitWidth, int fitHeight) {
		return createImageView1(new Image(JkFiles.toURL(imgPath)), (double) fitWidth, (double)fitHeight, true);
	}
	public static ImageView createImageView(Path imgPath, Double fitWidth, Double fitHeight) {
		return createImageView1(new Image(JkFiles.toURL(imgPath)), fitWidth, fitHeight, true);
	}
	public static ImageView createImageView(Image image, int fitWidth, int fitHeight) {
		return createImageView1(image, (double) fitWidth, (double) fitHeight, true);
	}
	public static ImageView createImageView(Image image) {
		return createImageView1(image, null, null, true);
	}
	public static ImageView createImageView(Image image, Double fitWidth, Double fitHeight) {
		return createImageView1(image, fitWidth, fitHeight, true);
	}
	public static ImageView createImageView(Image image, Double fitWidth, Double fitHeight, boolean preserveRatio) {
		return createImageView1(image, fitWidth, fitHeight, preserveRatio);
	}
	private static ImageView createImageView1(Image image, Double fitWidth, Double fitHeight, boolean preserveRatio) {
		ImageView imageView = new ImageView();
		if(image != null)	imageView.setImage(image);
		imageView.setPreserveRatio(preserveRatio);
		if(fitWidth != null)	imageView.setFitWidth(fitWidth);
		if(fitHeight != null)	imageView.setFitHeight(fitHeight);
		return imageView;
	}

	public static HBox createHBox(String styleClasses, Node... nodes) {
		HBox hbox = new HBox(nodes);
		List<String> scList = JkStrings.splitList(styleClasses, " ");
		hbox.getStyleClass().addAll(scList);
		return hbox;
	}
	public static VBox createVBox(String styleClasses, Node... nodes) {
		VBox vbox = new VBox(nodes);
		List<String> scList = JkStrings.splitList(styleClasses, " ");
		vbox.getStyleClass().addAll(scList);
		return vbox;
	}

}
