package xxx.joker.libs.core.objects;

public class Area {

    int x;
    int y;
    int width;
    int height;

    public Area() {

    }
    public Area(Pos startPos, int width, int height) {
        this.x = startPos.getColNum();
        this.y = startPos.getRowNum();
        this.width = width;
        this.height = height;
    }
    public Area(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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