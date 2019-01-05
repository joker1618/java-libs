package xxx.joker.libs.javafx;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class JkFxButtonBuilder {

    import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public enum JkButtonType { ADD, DELETE, UPDATE }

    private int width = 25;
    private int height = 25;
    private boolean resizable = false;
    private int padding = 0;
    private Color backgroundColor = Color.TRANSPARENT;
    private int borderWidth = 0;
    private Color borderColor = Color.BLACK;

    public JkFxButtonBuilder() {
    }

    public JkFxButtonBuilder setWidth(int width) {
        this.width = width;
		return this;
    }

    public JkFxButtonBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public JkFxButtonBuilder setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public JkFxButtonBuilder setResizable(boolean resizable) {
        this.resizable = resizable;
		return this;
    }

    public JkFxButtonBuilder setPadding(int padding) {
        this.padding = padding;
		return this;
    }

    public JkFxButtonBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public JkFxButtonBuilder setBackgroundTrasparent() {
        this.backgroundColor = Color.TRANSPARENT;
        return this;
    }

    public JkFxButtonBuilder setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
		return this;
    }

    public JkFxButtonBuilder setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
		return this;
    }

    public JkFxButtonBuilder setBorder(int borderWidth, Color borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
		return this;
    }

    public Button createButton(JkButtonType buttonType) {
        List<String> styles = new ArrayList<>();

        String iconURL;
        switch (buttonType) {
            case ADD:       iconURL = "/icon/add.png";      break;
            case UPDATE:    iconURL = "/icon/update.png";   break;
            case DELETE:    iconURL = "/icon/delete.png";   break;
            default:        iconURL = "";   break;
        }
        if(!iconURL.isEmpty()) {
            styles.add(strf("-fx-background-image: url(\"%s\")", iconURL));
            styles.add(strf("-fx-background-size: %d %d", width, height));
            styles.add("-fx-background-repeat: no-repeat");
            styles.add("-fx-background-position: center");
        }

        styles.add(strf("-fx-background-color: %s", backgroundColor.toString().replaceAll("^0x", "#")));
        styles.add(strf("-fx-border-width: %d", borderWidth));
        styles.add(strf("-fx-border-color: %s", borderColor.toString().replaceAll("^0x", "#")));
        styles.add(strf("-fx-padding: %d", padding));

        if(resizable) {
            styles.add(strf("-fx-min-width: %d", width));
            styles.add(strf("-fx-min-height: %d", height));
        } else {
            int w = width + borderWidth + padding;
            int h = height+ borderWidth + padding;
            styles.add(strf("-fx-min-width: %d", w));
            styles.add(strf("-fx-max-width: %d", w));
            styles.add(strf("-fx-min-height: %d", h));
            styles.add(strf("-fx-max-height: %d", h));
        }

        Button button = new Button();
        button.setStyle(JkStreams.join(styles, ";"));
        return button;
    }
}
