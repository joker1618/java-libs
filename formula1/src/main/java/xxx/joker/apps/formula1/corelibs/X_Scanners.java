package xxx.joker.apps.formula1.corelibs;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.objects.Range;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class X_Scanners {

    public static X_TextScanner getTextScanner(String txt) {
        return getTextScanner(txt, false);
    }
    public static X_TextScanner getTextScanner(String txt, boolean ignoreCase) {
        return new X_TextScannerImpl(txt, ignoreCase);
    }

    public static X_Tag parseHtmlTag(String html, String tagName, String... startStrings) {
        X_TextScanner scanner = new X_TextScannerImpl(html, true);

        for(String ss : startStrings) {
            if(StringUtils.isNotEmpty(ss)) {
                if(!scanner.startAt(ss)) {
                    return null;
                }
            }
        }

        String toFind = "<";
        if(StringUtils.isNotBlank(tagName)) {
            toFind += tagName;
        }
        if(!scanner.startAt(toFind)) {
            return null;
        }

        scanner.rebaseOrigText();
        return parseHtml(scanner);
    }
    private static X_Tag parseHtml(X_TextScanner scanner) {
        X_TagImpl rootTag = null;
        Stack<X_TagImpl> openedTags = new Stack<>();
        Range tagRange;

        while((tagRange = getNextTagRange(scanner.toString())) != null) {
            String strTag = scanner.nextString(tagRange);
            scanner.skip(tagRange.getEnd());

            Pair<X_TagImpl, Boolean> pair = parseTagString(strTag);
            X_TagImpl tag = pair.getKey();
            boolean isClosingTag = !pair.getValue();

            if(!isClosingTag) {
                tag.setStartPos((int)scanner.position() - tagRange.getLength());

                if(rootTag == null) {
                    rootTag = tag;
                } else {
                    X_TagImpl parent = openedTags.peek();
                    parent.getChildren().add(tag);
                    tag.setParent(parent);
                }

                if (!tag.isAutoClosed()) {
                    openedTags.push(tag);
                } else {
                    tag.setEndPos((int)scanner.position());
                    if(openedTags.isEmpty()) { // the first tag is auto-closed
                        break;
                    }
                }

            } else {
                X_TagImpl popTag = openedTags.pop();
                popTag.setEndPos((int)scanner.position());
                if(openedTags.empty() || !popTag.getTagName().equals(tag.getTagName())) {
                    break;
                }
            }
        }

        if(rootTag != null) {
            int len = (int) scanner.position();
            if(rootTag.getEndPos() == -1) {
                rootTag.setEndPos(len);
            }
            scanner.reset();
            rootTag.setHtml(scanner.nextString(len));
        }

        return rootTag;
    }
    private static Range getNextTagRange(String html) {
        Pattern pattern = Pattern.compile("(<[^<]*?>)");
        Matcher m = pattern.matcher(html);
        if(!m.find()) return null;
        return Range.ofBounds(m.start(), m.end());
    }
    // return <tag; true if is a tag open (i.e. <div>), false if is a closing tag (i.e. </div>)>
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

}
