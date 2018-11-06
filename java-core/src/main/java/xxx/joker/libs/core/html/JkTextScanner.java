package xxx.joker.libs.core.html;

/**
 * Created by f.barbano on 14/08/2017.
 */
public interface JkTextScanner {

	void startCursorAt(int offset);
	boolean startCursorAt(String... toFind);
	boolean startCursorAfter(String... toFind);
	boolean startCursorAtBackward(String... toFind);
	boolean startCursorAfterBackward(String... toFind);

	boolean endCursorAt(String... toFind);
	boolean endCursorAfter(String... toFind);
	boolean endCursorAtBackward(String... toFind);
	boolean endCursorAfterBackward(String... toFind);

	void reset();

	String nextAttrValue(String attrName);
	String nextValueBetween(String start, String end);
	Integer nextIntBetween(String start, String end);
	String nextValueUntil(String end);
	String nextString(int start, int offset);
	String nextString(int offset);

	JkTextScanner subScannerUntil(String end);
	JkTextScanner subScannerBetween(String start, String end);

	boolean isCursorStartWith(String str);
	boolean contains(String str);

	JkHtmlTag nextHtmlTag(String tagName);

	String toString();

}
