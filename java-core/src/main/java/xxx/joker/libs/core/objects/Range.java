package xxx.joker.libs.core.objects;

import static xxx.joker.libs.core.utils.JkStrings.strf;

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

    @Override
    public String toString() {
        return strf("{}-{} ({})", start, end, getLength());
    }

    public Range shiftStart(int numShift) {
        return new Range(start+numShift, end+numShift);
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
