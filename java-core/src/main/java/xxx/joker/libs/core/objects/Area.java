package xxx.joker.libs.core.objects;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Area {

    int x;
    int y;
    int width;
    int height;

    public Area() {

    }
    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return strf("[{}:{} - {}:{}]", x, y, width, height);
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
	public int getY() {
        return y;
    }
	public void setY(int y) {
        this.y = y;
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
	public int getEndX() {
        return getX() + getWidth();
    }
    public int getEndY() {
        return getY() + getHeight();
    }
}
