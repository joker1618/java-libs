package xxx.joker.libs.core.html;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JkHtmlScanner {

    public static List<JkHtmlTag> parseHtml(String html) {
        String finalHtml = html.replaceAll("<!--((.|\\s)*?)-->", "").replaceAll("<!((.|\\s)*?)>", ""); // remove html comments
        StringBuilder sb = new StringBuilder(finalHtml);

        List<JkHtmlTag> rootTags = new ArrayList<>();
        List<JkHtmlTag> openedList = new ArrayList<>();
        Map<JkHtmlTag,List<String>> txtInsideMap = new HashMap<>();
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
                    parent.addChildren(tag);
                    String trim = sb.substring(0, ltIndex).trim();
                    if(!trim.isEmpty()) {
                        txtInsideMap.putIfAbsent(parent, new ArrayList<>());
                        txtInsideMap.get(parent).add(trim);
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
                    txtInsideMap.putIfAbsent(parentTag, new ArrayList<>());
                    txtInsideMap.get(parentTag).add(trim);
                }
                if(txtInsideMap.containsKey(parentTag)) {
                    parentTag.setTextInsideLines(txtInsideMap.get(parentTag));
                }

                openedList.remove(openedList.size()-1);
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


}
