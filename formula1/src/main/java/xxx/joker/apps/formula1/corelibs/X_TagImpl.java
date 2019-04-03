package xxx.joker.apps.formula1.corelibs;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.objects.Range;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class X_TagImpl implements X_Tag {

    private X_Tag parent;
    private String tagName;
    private int startPos;
    private int endPos;
    private boolean autoClosed;
    private Map<String, String> attributes;

    private X_TagImpl() {
        this.attributes = new HashMap<>();
    }
    private X_TagImpl(String tagName) {
        this.tagName = tagName;
        this.attributes = new HashMap<>();
    }

    public static X_Tag parse(String html, String tagName, String startString) {
        X_TextScanner scanner = new X_TextScannerImpl(html, true);

        if(StringUtils.isNotEmpty(startString)) {
            if(!scanner.startAt(startString)) {
                return null;
            }
        }

        String toFind = "<";
        if(StringUtils.isNotBlank(startString)) {
            toFind += tagName;
        }

        if(!scanner.startAt(toFind)) {
            return null;
        }

        scanner.rebaseOrigText();


        X_TagImpl tag = null;

        while(scanner.startAt(toFind)) {
            if(tag == null) {
                tag = new X_TagImpl(tagName);
            }
        }

    }

    private static X_Tag parseHtml(X_TextScanner scanner) {
        X_Tag rootTag = null;
        Stack<X_TagImpl> openedTags = new Stack<>();
        Range tagRange;

        while((tagRange = getNextTagRange(scanner.toString())) != null) {
            String strTag = scanner.nextString(tagRange);
            Pair<X_TagImpl, Boolean> pair = parseTagString(strTag);
            X_TagImpl tag = pair.getKey();
            boolean isClosingTag = !pair.getValue();

            if(!isClosingTag) {
                if (!openedTags.empty()) {
                    X_TagImpl parent = openedTags.peek();
                    parent.getChildren().add(tag);
                } else {
                    rootTag.add(tag);
                }

                if (!tag.isAutoClosed()) {
                    openedTags.push(tag);
                    openedStarts.push(seek + ltIndex);
                } else {
                    tag.setHtml(strTag);
                }

            } else if(openedTags.empty()) {
                break;

            } else {
                X_TagImpl popTag = openedTags.pop();
                if(!popTag.getTagName().equals(tag.getTagName())) {
                    break;
                }

                String subs = html.substring(openedStarts.pop(), seek + gtIndex + 1);
                popTag.setHtml(subs);

                if(openedTags.empty() && onlyFirstTag) {
                    break;
                }
            }

            sb.delete(0, gtIndex + 1);
            seek += gtIndex + 1;
        }

        return rootTag;
    }

    private static Range getNextTagRange(String html) {
        Pattern pattern = Pattern.compile("(<[^<]*?>)");
        Matcher m = pattern.matcher(html);
        if(!m.find()) return null;
        return Range.ofBounds(m.start(), m.end());
    }

    // return <tag; true if is a tag open, false if is a closing tag>
    // strTag = <...>
    private static Pair<X_TagImpl, Boolean> parseTagString(String strTag) {
        // Check if is a closing tag
        if(strTag.startsWith("</")) {
            String tagName = strTag.replaceAll("^</", "").replaceAll(">$", "").trim();
            return Pair.of(new X_TagImpl(tagName), false);
        }

        X_TagImpl tag = new X_TagImpl();
        tag.setAutoClosed(strTag.endsWith("/>"));

        String tempStr = strTag.replaceAll("^<", "").replaceAll(">$", "").replaceAll("/$", "").trim();

        StringBuilder sb = new StringBuilder(tempStr.trim());
        int idx = sb.indexOf(" ");
        if(idx == -1) {
            // tag name only
            tag.setTagName(sb.toString());

        } else {
            // tag name + attributes
            tag.setTagName(sb.substring(0, idx));

            sb.delete(0, idx+1);
            int eqIndex;
            while((eqIndex = sb.indexOf("=")) != -1) {
                String attrName = sb.substring(0, eqIndex).trim();
                int begin = eqIndex + 1;
                while(begin < sb.length() && sb.charAt(begin) == ' ')   begin++;
                if(begin == sb.length())    break;

                boolean isQuote = sb.charAt(begin) == '"';
                int end = begin + 1;
                while(end < sb.length()) {
                    if(isQuote) {
                        if(sb.charAt(end) == '"' && sb.charAt(end-1) != '\\') {
                            break;
                        }
                    } else {
                        if(sb.charAt(end) == ' ') {
                            break;
                        }
                    }
                    end++;
                }

                if(end == sb.length()) {
                    if(isQuote) break;
                } else {
                    end++;
                }

                String attrValue = sb.substring(begin, end).replaceAll("^\"", "").replaceAll("\"$", "");
                tag.getAttributes().put(attrName, attrValue);

                sb.delete(0, end);
            }
        }

        return Pair.of(tag, true);
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }
   
    @Override
    public String getAttribute(String attrName) {
        return attributes.get(attrName);
    }

    @Override
    public boolean isAutoClosed() {
        return autoClosed;
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    protected void setTagName(String tagName) {
        this.tagName = tagName;
    }

    protected void setAutoClosed(boolean autoClosed) {
        this.autoClosed = autoClosed;
    }
}