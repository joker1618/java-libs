package xxx.joker.libs.javalibs.html;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by f.barbano on 14/08/2017.
 */
public class JkTextScannerImpl implements JkTextScanner {

	private String originalHtml;
	private StringBuilder buffer;


	public JkTextScannerImpl(String htmlCode) {
		this.originalHtml = htmlCode;
		this.buffer = new StringBuilder(htmlCode);
	}


	@Override
	public boolean startCursorAt(String toFind) {
		return setCursor(true, toFind, false, true);
	}

	@Override
	public boolean startCursorAfter(String toFind) {
		return setCursor(true, toFind, true, true);
	}

	@Override
	public boolean startCursorAtBackward(String toFind) {
		return setCursor(true, toFind, false, false);
	}

	@Override
	public boolean startCursorAfterBackward(String toFind) {
		return setCursor(true, toFind, true, false);
	}

	@Override
	public boolean endCursorAt(String toFind) {
		return setCursor(false, toFind, false, true);
	}

	@Override
	public boolean endCursorAfter(String toFind) {
		return setCursor(false, toFind, true, true);
	}

	@Override
	public boolean endCursorAtBackward(String toFind) {
		return setCursor(false, toFind, false, false);
	}

	@Override
	public boolean endCursorAfterBackward(String toFind) {
		return setCursor(false, toFind, true, false);
	}

	@Override
	public void reset() {
		buffer = new StringBuilder(originalHtml);
	}

	@Override
	public String nextAttrValue(String attrName) {
		int idx = buffer.toString().indexOf(attrName);
		if(idx == -1) {
			return null;
		}

		// Find separator ('=', ':')
		String substr = buffer.substring(idx + attrName.length());
		int idxEquals = substr.indexOf('=');
		int idxColon = substr.indexOf(':');

		if(idxEquals == -1 && idxColon == -1) {
			return null;
		}

		idx = idxColon == -1 ? idxEquals : (idxEquals == -1 ? idxColon : Math.min(idxEquals, idxColon));
		substr = substr.substring(idx+1).trim();

		// Get value ("'" or "\"")
		String attrValue;
		if(substr.charAt(0) == '\'') {
			attrValue = StringUtils.substringBetween(substr, "'");
		} else if(substr.charAt(0) == '"') {
			attrValue = StringUtils.substringBetween(substr, "\"");
		} else {
			int end = substr.indexOf(',');
			attrValue = substr.substring(0, end);
		}

		return attrValue;
	}

	@Override
	public String nextValueBetween(String start, String end) {
		return StringUtils.substringBetween(buffer.toString(), start, end);
	}

	@Override
	public String nextValueUntil(String end) {
		int idx = buffer.indexOf(end);
		return idx == -1 ? buffer.toString() : buffer.substring(0, idx);
	}

	@Override
	public String nextString(int start, int offset) {
		return buffer.substring(start, start + offset);
	}

	@Override
	public String nextString(int offset) {
		return buffer.substring(0, offset);
	}

	@Override
	public boolean isCursorStartWith(String str) {
		return buffer.toString().startsWith(str);
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	private boolean setCursor(boolean setStart, String toFind, boolean cursorAfterToFind, boolean findForward) {
		int idx;
		if(findForward) {
			idx = buffer.toString().indexOf(toFind);
		} else {
			idx = buffer.toString().lastIndexOf(toFind);
		}

		if(idx == -1) {
			return false;
		}

		if(cursorAfterToFind) {
			idx += toFind.length();
		}

		int begin = setStart ? 0 : idx;
		int end = setStart ? idx : buffer.length();
		buffer.delete(begin, end);
		
		return true;
	}
}
