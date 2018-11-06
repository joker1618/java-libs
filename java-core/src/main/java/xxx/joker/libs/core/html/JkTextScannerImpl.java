package xxx.joker.libs.core.html;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.libs.core.utils.JkConverter;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 14/08/2017.
 */
public class JkTextScannerImpl implements JkTextScanner {

    private static final Logger logger = LoggerFactory.getLogger(JkTextScannerImpl.class);

	private String originalHtml;
	private StringBuilder buffer;


	public JkTextScannerImpl(String htmlCode) {
		this.originalHtml = htmlCode;
		this.buffer = new StringBuilder(htmlCode);
	}

    @Override
    public void startCursorAt(int offset) {
	    if(offset > 0) {
	        int end = Math.min(offset, buffer.length());
            buffer.delete(0, end);
        }
    }

    @Override
	public boolean startCursorAt(String... toFind) {
		return setCursorMulti(true, false, true, toFind);
	}

	@Override
	public boolean startCursorAfter(String... toFind) {
        return setCursorMulti(true, true, true, toFind);
	}

	@Override
	public boolean startCursorAtBackward(String... toFind) {
		return setCursorMulti(true, false, false, toFind);
	}

	@Override
	public boolean startCursorAfterBackward(String... toFind) {
		return setCursorMulti(true, true, false, toFind);
	}

    @Override
	public boolean endCursorAt(String... toFind) {
		return setCursorMulti(false, false, true, toFind);
	}

	@Override
	public boolean endCursorAfter(String... toFind) {
		return setCursorMulti(false, true, true, toFind);
	}

	@Override
	public boolean endCursorAtBackward(String... toFind) {
		return setCursorMulti(false, false, false, toFind);
	}

	@Override
	public boolean endCursorAfterBackward(String... toFind) {
		return setCursorMulti(false, true, false, toFind);
	}

	@Override
	public void reset() {
		buffer = new StringBuilder(originalHtml);
	}

	@Override
	public String nextAttrValue(String attrName) {
		int idx = StringUtils.indexOfIgnoreCase(buffer.toString(), attrName);
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
    public Integer nextIntBetween(String start, String end) {
        return JkConverter.stringToInteger(JkStrings.safeTrim(nextValueBetween(start, end)));
    }

    @Override
	public String nextValueUntil(String end) {
		int idx = StringUtils.isEmpty(end) ? -1 : buffer.indexOf(end);
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
    public JkTextScanner subScannerUntil(String end) {
        return new JkTextScannerImpl(nextValueUntil(end));
    }

    @Override
    public JkTextScanner subScannerBetween(String start, String end) {
        String val = nextValueBetween(start, end);
        return val == null ? null : new JkTextScannerImpl(val);
    }

    @Override
	public boolean isCursorStartWith(String str) {
		return buffer.toString().startsWith(str);
	}

    @Override
    public boolean contains(String str) {
        return buffer.toString().contains(str);
    }

    @Override
    public JkHtmlTag nextHtmlTag(String tagName) {
        String fixedString = buffer.toString().replaceAll("<!--(.*?)-->", "");  // remove html comments
        int idx = fixedString.indexOf("<" + tagName);
        if(idx == -1)   return null;

        JkTextScannerImpl scanner = new JkTextScannerImpl(fixedString.substring(idx));

        List<JkHtmlTag> parents = new ArrayList<>();
        String tagStr;
        while((tagStr = scanner.nextTag()) != null) {
            if(tagStr.startsWith("</")) {
                // closure tag
                String tname = tagStr.replaceAll("^</", "").replaceAll(">$", "");
                if(!parents.isEmpty()) {
                    JkHtmlTag removed = parents.remove(0);
                    String pname = removed.getTagName();
                    while (!parents.isEmpty() && !pname.equals(tname)) {
                        logger.warn("Unclosed middle tag {}", pname);
                        removed = parents.remove(0);
                        pname = removed.getTagName();
                    }
                    if(parents.isEmpty()) {
                        return !pname.equals(tname) ? null : removed;
                    }
                    if(removed.getChildren().isEmpty()) {
                        String insideTag = scanner.nextValueUntil(tagStr);
                        removed.setTextInside(insideTag.trim());
                    }
                }

            } else  {
                boolean autoclosed = tagStr.endsWith("/>");
                String content = tagStr.replaceAll("^<", "").replaceAll(">$", "").replaceAll("/$", "").trim();
                int idxsp = content.indexOf(" ");
                String tname = idxsp == -1 ? content : content.substring(0, idxsp);
                JkHtmlTag tag = new JkHtmlTag(tname);
                if(idxsp != -1) {
                    String remain = content.substring(idxsp).trim();
                    JkTextScannerImpl scan = new JkTextScannerImpl(remain);
                    while (scan.contains("=")) {
                        String aname = scan.nextValueUntil("=").trim().replaceAll(".*\\s", "");
                        String aval = scan.nextAttrValue(aname);
                        tag.addAttribute(aname, aval);
                        scan.startCursorAfter(aval);
                    }
                }

                if(parents.isEmpty()) {
                    if(autoclosed) {
                        return tag;
                    }
                    parents.add(tag);
                } else {
                    parents.get(0).getChildren().add(tag);
                    if(!autoclosed) {
                        parents.add(0, tag);
                    }
                }
            }

            scanner.startCursorAfter(tagStr);
        }

        return null;
    }

    private String nextTag() {
        String tagStr = nextValueBetween("<", ">");
        if(tagStr == null)  return null;
        return strf("<%s>", tagStr);
    }


    @Override
	public String toString() {
		return buffer.toString();
	}

    private boolean setCursorMulti(boolean setStart, boolean cursorAfterToFind, boolean findForward, String... toFind) {
        JkTextScannerImpl sc = new JkTextScannerImpl(buffer.toString());
        for(String findStr : toFind) {
            if(!sc.setCursor(setStart, cursorAfterToFind, findForward, findStr)) {
                return false;
            }
        }

        this.buffer = sc.buffer;
        return true;
    }
    private boolean setCursor(boolean setStart, boolean cursorAfterToFind, boolean findForward, String toFind) {
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
