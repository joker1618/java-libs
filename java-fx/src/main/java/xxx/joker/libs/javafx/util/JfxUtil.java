package xxx.joker.libs.javafx.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.util.JkStrings;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class JfxUtil {

	public static Window getWindow(Event e) {
		return ((Node)e.getSource()).getScene().getWindow();
	}

	public static Stage getStage(Node node) {
		return (Stage)node.getScene().getWindow();
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

		} catch (Exception ex) {
			throw new JkRuntimeException(ex);
		}
	}

	public static void takeSnapshot(Node node, Path outPath) {
		try {
			WritableImage image = node.snapshot(new SnapshotParameters(), null);
			Files.createDirectories(JkFiles.getParent(outPath));
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", outPath.toFile());
		} catch (IOException e) {
			throw new JkRuntimeException(e);
		}
	}

	public static boolean alertConfirm(String headerFormat, Object... params) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setHeaderText(JkStrings.strf(headerFormat, params));
		Optional<ButtonType> resp = alert.showAndWait();
		return resp.isPresent() && resp.get() == ButtonType.OK;
	}

	public static void alertInfo(String headerFormat, Object... params) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setHeaderText(JkStrings.strf(headerFormat, params));
		alert.showAndWait();
	}
}
