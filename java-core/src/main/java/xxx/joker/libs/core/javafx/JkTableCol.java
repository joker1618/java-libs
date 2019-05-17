package xxx.joker.libs.core.javafx;

import javafx.scene.control.TableColumn;

import java.util.function.Function;

public class JkTableCol<T, V> extends TableColumn<T, V> {

    private String varName;
    private Function<T, V> extractor;
    private Function<V, String> strFunc;
    private boolean autoSize;
    private int prefWidth = -1;

    public JkTableCol() {

    }

}
