package xxx.joker.libs.core.objects;

public class Range {

    private int start;
    private int end;

    private Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public static Range ofBounds(int start, int end) {
        return new Range(start, end);
    }
    public static Range ofLength(int start, int length) {
        return new Range(start, start + length);
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return end - start;
    }

    public void setLength(int length) {
        setEnd(start + length);
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
