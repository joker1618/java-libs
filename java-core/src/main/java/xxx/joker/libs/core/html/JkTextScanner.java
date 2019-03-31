package xxx.joker.libs.core.html;

public interface JkTextScanner {

	void startAt(int offset);
	boolean startAt(String... toFind);
	boolean startAfter(String... toFind);
	boolean startAtLast(String... toFind);
	boolean startAfterLast(String... toFind);

	boolean endAt(String... toFind);
	boolean endAfter(String... toFind);
	boolean endAtLast(String... toFind);
	boolean endAfterLast(String... toFind);

	void reset();

	String nextAttrValue(String attrName);
	String nextStringBetween(String start, String end);
	Integer nextIntBetween(String start, String end);
	String nextStringUntil(String end);
	String nextString(int start, int offset);
	String nextString(int offset);

	JkTextScanner subScannerUntil(String end);
	JkTextScanner subScannerBetween(String start, String end);

	boolean isCursorStartWith(String str);
	boolean contains(String str);

	JkHtmlTag nextHtmlTag(String tagName);

}
