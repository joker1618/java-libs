package xxx.joker.apps.formula1.fxlibs;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

@Deprecated
public class X_JkFxButtonBuilder {

    @Deprecated
    public enum JkButtonType { ADD, DELETE, UPDATE }

    private int width = 25;
    private int height = 25;
    private boolean resizable = false;
    private int padding = 0;
    private Color backgroundColor = Color.TRANSPARENT;
    private int borderWidth = 0;
    private Color borderColor = Color.BLACK;

    public X_JkFxButtonBuilder() {
    }

    public X_JkFxButtonBuilder setWidth(int width) {
        this.width = width;
		return this;
    }

    public X_JkFxButtonBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public X_JkFxButtonBuilder setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public X_JkFxButtonBuilder setResizable(boolean resizable) {
        this.resizable = resizable;
		return this;
    }

    public X_JkFxButtonBuilder setPadding(int padding) {
        this.padding = padding;
		return this;
    }

    public X_JkFxButtonBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public X_JkFxButtonBuilder setBackgroundTrasparent() {
        this.backgroundColor = Color.TRANSPARENT;
        return this;
    }

    public X_JkFxButtonBuilder setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
		return this;
    }

    public X_JkFxButtonBuilder setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
		return this;
    }

    public X_JkFxButtonBuilder setBorder(int borderWidth, Color borderColor) {
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
		return this;
    }

    public Button createButton(JkButtonType buttonType) {
        List<String> styles = new ArrayList<>();

        String iconURL;
        switch (buttonType) {
            case ADD:       iconURL = "/fxicons/add.png";      break;
            case UPDATE:    iconURL = "/fxicons/update.png";   break;
            case DELETE:    iconURL = "/fxicons/delete.png";   break;
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
