package xxx.joker.libs.javafx;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class JkFxUtil {

	/* TableView utilities */
	public static <T> TableColumn<T, String> createTableColumnString(String header, String bindVarName) {
        TableColumn<T, String> tcol = new TableColumn<>(header);
		setTableCellFactoryString(tcol, bindVarName);
		return tcol;
	}

	public static <T> TableColumn<T, Integer> createTableColumnInteger(String header, String bindVarName) {
		TableColumn<T, Integer> tcol = new TableColumn<>(header);
		setTableCellFactoryInteger(tcol, bindVarName);
		return tcol;
	}

	public static <T, V> TableColumn<T, V> createTableColumn(String header, String bindVarName, Function<V,String> formatFunc, Function<String,V> parseFunc) {
		TableColumn<T, V> tcol = new TableColumn<>(header);
		setTableCellFactory(tcol, bindVarName, formatFunc, parseFunc);
		return tcol;
	}

	public static void setTableCellFactoryString(TableColumn<?, String> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, s -> s, s -> s);
	}

	public static void setTableCellFactoryInteger(TableColumn<?, Integer> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, String::valueOf, Integer::valueOf);
	}

	public static void setTableCellFactoryLong(TableColumn<?, Long> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, String::valueOf, Long::valueOf);
	}

	public static void setTableCellFactoryDouble(TableColumn<?, Double> column, String bindVarName) {
		setTableCellFactory(column, bindVarName, String::valueOf, Double::valueOf);
	}

	public static void setTableCellFactoryLocalDate(TableColumn<?, LocalDate> column, String bindVarName, DateTimeFormatter formatter) {
		setTableCellFactory(column, bindVarName, (LocalDate ldt) -> ldt.format(formatter), (s) -> (LocalDate)formatter.parse(s));
	}

	public static void setTableCellFactoryLocalDateTime(TableColumn<?, LocalDateTime> column, String bindVarName, DateTimeFormatter formatter) {
		setTableCellFactory(column, bindVarName, (LocalDateTime ldt) -> ldt.format(formatter), (s) -> (LocalDateTime)formatter.parse(s));
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
