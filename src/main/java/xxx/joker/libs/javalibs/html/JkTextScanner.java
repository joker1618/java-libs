package xxx.joker.libs.javalibs.html;

/**
 * Created by f.barbano on 14/08/2017.
 */
public interface JkTextScanner {

	boolean startCursorAt(String toFind);
	boolean startCursorAfter(String toFind);
	boolean startCursorAtBackward(String toFind);
	boolean startCursorAfterBackward(String toFind);

	boolean endCursorAt(String toFind);
	boolean endCursorAfter(String toFind);
	boolean endCursorAtBackward(String toFind);
	boolean endCursorAfterBackward(String toFind);

	void reset();

	String nextAttrValue(String attrName);
	String nextValueBetween(String start, String end);
	String nextString(int start, int offset);
	String nextString(int offset);

	boolean isCursorStartWith(String str);

}
