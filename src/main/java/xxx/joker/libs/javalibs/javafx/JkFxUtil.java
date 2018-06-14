package xxx.joker.libs.javalibs.javafx;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class JkFxUtil {

	/* TableView utilities */
	public static void setTableCellFactoryString(TableColumn<?, String> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, s -> s, s -> s);
	}

	public static void setTableCellFactoryInteger(TableColumn<?, Integer> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, String::valueOf, Integer::valueOf);
	}

	public static void setTableCellFactoryLocalDate(TableColumn<?, LocalDate> column, String bindVarName, DateTimeFormatter formatter) {
		setTableCellFactory(column, bindVarName, (LocalDate ldt) -> ldt.format(formatter), (s) -> (LocalDate)formatter.parse(s));
	}

	public static void setTableCellFactoryLocalDateTime(TableColumn<?, LocalDateTime> column, String bindVarName, DateTimeFormatter formatter) {
		setTableCellFactory(column, bindVarName, (LocalDateTime ldt) -> ldt.format(formatter), (s) -> (LocalDateTime)formatter.parse(s));
	}

	public static <V> void setTableCellFactory(TableColumn<?, V> column, String bindVarName, Function<V, String> toStringFunc) {
		setTableCellFactory(column, bindVarName, toStringFunc, s -> null);
	}

	public static <V> void setTableCellFactory(TableColumn<?, V> column, String bindVarName, Function<V, String> toStringFunc, Function<String, V> fromStringFunc) {
		if(StringUtils.isNotBlank(bindVarName)) {
			setTableCellValueBinding(column, bindVarName);
		}

		column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<V>() {
			@Override
			public String toString(V object) {
				return toStringFunc.apply(object);
			}

			@Override
			public V fromString(String string) {
				return fromStringFunc.apply(string);
			}
		}));
	}

	public static <V> void setTableCellValueBinding(TableColumn<?, V> column, String bindVarName) {
		column.setCellValueFactory(new PropertyValueFactory<>(bindVarName));
	}


	public static Window getWindow(Event e) {
		return ((Node)e.getSource()).getScene().getWindow();
	}

	public static <T extends Pane> T getChildren(Pane root, int... childrenIndexes) {
		Pane tmp = root;
		for(int idx : childrenIndexes) {
			tmp = (Pane) root.getChildren().get(idx);
		}
		return (T) tmp;
	}
}
