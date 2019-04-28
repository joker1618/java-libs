package xxx.joker.apps.formula1.fxlibs;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class JfxUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JfxUtil.class);

	public static ImageView createImageView(int fitWidth, int fitHeight) {
		return createImageView(null, fitWidth, fitHeight);
	}
	public static ImageView createImageView(Image image, int fitWidth, int fitHeight) {
		ImageView imageView = new ImageView();
		if(image != null)	imageView.setImage(image);
		imageView.setFitWidth(fitWidth);
		imageView.setFitHeight(fitHeight);
		imageView.setPreserveRatio(true);
		return imageView;
	}

	public static Window getWindow(Event e) {
		return ((Node)e.getSource()).getScene().getWindow();
	}

	public static Stage getStage(Event e) {
		return (Stage)getWindow(e);
	}

	public static <T extends Pane> T getChildren(Pane root, int... childrenIndexes) {
		Pane tmp = root;
		for(int idx : childrenIndexes) {
			tmp = (Pane) tmp.getChildren().get(idx);
		}
		return (T) tmp;
	}
}
