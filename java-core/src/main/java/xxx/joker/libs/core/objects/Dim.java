package xxx.joker.libs.core.objects;

import org.omg.CORBA.PUBLIC_MEMBER;
import xxx.joker.libs.core.utils.JkStrings;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Dim {

    private int width;
    private int height;

    public Dim(String str) {
        String[] split = JkStrings.splitArr(str, "x");
        this.width = Integer.valueOf(split[0]);
        this.height = Integer.valueOf(split[1]);
    }
    public Dim(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return strf("{}x{}", width, height);
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
}
