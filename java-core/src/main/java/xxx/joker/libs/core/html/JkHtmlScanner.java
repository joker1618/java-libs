package xxx.joker.libs.core.html;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JkHtmlScanner {

    private static final Logger logger = LoggerFactory.getLogger(JkHtmlScanner.class);

    private JkHtmlScanner() {}

    public static JkHtmlTag parseTag(String html, String tagName) {
        String finalHtml = fixHtml(html);
        int idx = StringUtils.indexOfIgnoreCase(finalHtml, "<" + tagName);
        if(idx == -1)   return null;
        List<JkHtmlTag> res = parseHtml(finalHtml.substring(idx), true);
        return res.isEmpty() ? null : res.get(0);
    }

    public static List<JkHtmlTag> parseHtml(String html) {
        String finalHtml = fixHtml(html);
        return parseHtml(finalHtml, false);
    }

    private static List<JkHtmlTag> parseHtml(String html, boolean onlyFirstTag) {
        StringBuilder sb = new StringBuilder(html);

        List<JkHtmlTag> rootTags = new ArrayList<>();
        Stack<JkHtmlTagImpl> openedTags = new Stack<>();
        Stack<Integer> openedStarts = new Stack<>();
        int seek = 0;
        int ltIndex;

        while((ltIndex = sb.indexOf("<")) != -1) {
            int gtIndex = sb.indexOf(">", ltIndex);
            if(gtIndex == -1)   break;

            String strTag = sb.substring(ltIndex, gtIndex + 1);
            Pair<JkHtmlTagImpl, Boolean> pair = parseTagString(strTag);
            JkHtmlTagImpl tag = pair.getKey();
            boolean isClosure = !pair.getValue();

            if(!isClosure) {
                if (!openedTags.empty()) {
                    JkHtmlTagImpl parent = openedTags.peek();
                    parent.getChildren().add(tag);
                } else {
                    rootTags.add(tag);
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
                JkHtmlTagImpl popTag = openedTags.pop();
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

        return rootTags;
    }

    // return <tag; true if is a tag open, false if is a tag closure>
    // strTag = <...>
    private static Pair<JkHtmlTagImpl, Boolean> parseTagString(String strTag) {
        // Check if is a tag closure
        if(strTag.startsWith("</")) {
            String tagName = strTag.replaceAll("^</", "").replaceAll(">$", "").trim();
            return Pair.of(new JkHtmlTagImpl(tagName), false);
        }

        JkHtmlTagImpl tag = new JkHtmlTagImpl();
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
                tag.addAttribute(attrName, attrValue);

                sb.delete(0, end);
            }
        }

        return Pair.of(tag, true);
    }

    private static String fixHtml(String html) {
        html = html.replaceAll("<!--((.|\\s)*?)-->", "");   // delete html comments
        html = html.replaceAll("<!((.|\\s)*?)>", "");       // delete DOCTYPE
        return html;
    }

}
