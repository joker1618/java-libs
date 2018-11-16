package xxx.joker.libs.core.html;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.utils.JkStreams;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JkHtmlScanner {

//    public static void main(String[] args) throws IOException {
//        String html = JkStreams.join(Files.readAllLines(Paths.get("C:\\Users\\f.barbano\\Desktop\\aaa.txt")), "");
//        parseHtml(html);
//    }

    private JkHtmlScanner() {}

    public static List<JkHtmlTag> parseHtml(String html) {
        String finalHtml = fixHtml(html);
        StringBuilder sb = new StringBuilder(finalHtml);

        List<JkHtmlTag> rootTags = new ArrayList<>();
        List<JkHtmlTag> openedList = new ArrayList<>();
        int ltIndex;

        while((ltIndex = sb.indexOf("<")) != -1) {
            int gtIndex = sb.indexOf(">", ltIndex);
            if(gtIndex == -1)   break;

            String strTag = sb.substring(ltIndex, gtIndex + 1);
            Pair<JkHtmlTag, Boolean> pair = parseTagString(strTag);
            JkHtmlTag tag = pair.getKey();

            if(pair.getValue()) {
                if (!openedList.isEmpty()) {
                    JkHtmlTag parent = openedList.get(openedList.size() - 1);
                    parent.getChildren().add(tag);
                    String trim = sb.substring(0, ltIndex).trim();
                    if(!trim.isEmpty()) {
                        parent.getTextTagLines().add(trim);
                        parent.getAllTextInsideLines().add(trim);
                    }
                    if("br".equalsIgnoreCase(tag.getTagName())) {
                        parent.getTextTagLines().add(StringUtils.LF);
                        parent.getAllTextInsideLines().add(StringUtils.LF);
                    }

                } else {
                    rootTags.add(tag);
                }
                if (!tag.isAutoClosed())    openedList.add(tag);

            } else if(openedList.isEmpty()) {
                break;

            } else {
                JkHtmlTag parentTag = openedList.get(openedList.size()-1);
                if(!parentTag.getTagName().equals(tag.getTagName())) {
                    break;
                }

                String trim = sb.substring(0, ltIndex).trim();
                if(!trim.isEmpty()) {
                    parentTag.getTextTagLines().add(trim);
                    parentTag.getAllTextInsideLines().add(trim);
                }

                openedList.remove(openedList.size()-1);

                if(!openedList.isEmpty()) {
                    JkHtmlTag lastTag = openedList.get(openedList.size()-1);
                    lastTag.getAllTextInsideLines().addAll(parentTag.getAllTextInsideLines());
                }
            }

            sb.delete(0, gtIndex+1);
        }

        return rootTags;
    }

    // return <tag; true if is a tag description, false if is a tag closure>
    // strTag = <...>
    private static Pair<JkHtmlTag, Boolean> parseTagString(String strTag) {
        // Check if is a tag closure
        if(strTag.startsWith("</")) {
            String tagName = strTag.replaceAll("^</", "").replaceAll(">$", "").trim();
            return Pair.of(new JkHtmlTag(tagName), false);
        }

        JkHtmlTag tag = new JkHtmlTag();
        tag.setAutoClosed(strTag.endsWith("/>"));

        String tempStr = strTag.replaceAll("^<", "").replaceAll(">$", "").replaceAll("/$", "").trim();

        StringBuilder sb = new StringBuilder(tempStr.trim());
        int idx = sb.indexOf(" ");
        if(idx == -1) {
            tag.setTagName(sb.toString());

        } else {
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
        html = html.replaceAll("<!--((.|\\s)*?)-->", ""); // remove html comments
        html = html.replaceAll("<!((.|\\s)*?)>", ""); // remove DOCTYPE
        return html;
    }

}
