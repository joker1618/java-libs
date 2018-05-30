package xxx.joker.libs.javalibs.javafx;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Window;

public class JkFxUtil {

	public static Window getWindow(Event e) {
		return ((Node)e.getSource()).getScene().getWindow();
	}

}
