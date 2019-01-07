package xxx.joker.libs.core.html;


import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class JkHtmlTag {

    private String tagName;
    private Map<String, String> attributeMap;
    private List<JkHtmlTag> children;
    private List<String> textTagLines;
    private List<String> allTextInsideLines;
    private boolean autoClosed;

    protected JkHtmlTag() {
        this.attributeMap = new HashMap<>();
        this.children = new ArrayList<>();
        this.textTagLines = new ArrayList<>();
        this.allTextInsideLines = new ArrayList<>();
    }

    protected JkHtmlTag(String tagName) {
        this.tagName = tagName;
        this.attributeMap = new HashMap<>();
        this.children = new ArrayList<>();
        this.textTagLines = new ArrayList<>();
        this.allTextInsideLines = new ArrayList<>();
    }

    public JkHtmlTag findFirst(String childName, String... attributes) {
        List<JkHtmlTag> tags = findFirsts(childName, attributes);
        return tags.isEmpty() ? null : tags.get(0);
    }
    public List<JkHtmlTag> findFirsts(String childName, String... attributes) {
        return findFirstTagList(childName, attributes);
    }
    private List<JkHtmlTag> findFirstTagList(String childName, String... attributes) {
        List<Pair<String, String>> attrPairs = JkStreams.map(Arrays.asList(attributes), s -> Pair.of(s.split("=")[0], s.split("=")[1]));

        List<JkHtmlTag> toRet = new ArrayList<>();

        for(JkHtmlTag child : children) {
            if(childName.equalsIgnoreCase(child.getTagName())) {
                boolean res = true;
                for(Pair<String,String> attr : attrPairs) {
                    if(!attr.getValue().equals(child.getAttribute(attr.getKey()))) {
                        res = false;
                        break;
                    }
                }
                if(res) {
                    toRet.add(child);
                }
            }
        }

        for(int i = 0; i < children.size() && toRet.isEmpty(); i++) {
            toRet.addAll(children.get(i).findFirstTagList(childName, attributes));
        }

        return toRet;
    }

    public List<JkHtmlTag> findAll(String childName, String... attributes) {
        return findAllTagList(childName, attributes);
    }
    private List<JkHtmlTag> findAllTagList(String childName, String... attributes) {
        List<Pair<String, String>> attrPairs = JkStreams.filterAndMap(Arrays.asList(attributes), s -> s.contains("="), s -> Pair.of(s.split("=")[0], s.split("=")[1]));

        List<JkHtmlTag> toRet = new ArrayList<>();

        for(JkHtmlTag child : children) {
            if(childName.equalsIgnoreCase(child.getTagName())) {
                boolean res = true;
                for(Pair<String,String> attr : attrPairs) {
                    if(!attr.getValue().equals(child.getAttribute(attr.getKey()))) {
                        res = false;
                        break;
                    }
                }
                if(res) {
                    toRet.add(child);
                }
            }
            toRet.addAll(child.findAllTagList(childName, attributes));
        }

        return toRet;
    }

    public String getTagName() {
        return tagName;
    }

    public Map<String, String> getAttributes() {
        return new HashMap<>(attributeMap);
    }
    public String getAttribute(String attrName) {
        return attributeMap.get(attrName);
    }

    public List<JkHtmlTag> getChildren() {
        return children;
    }
    public List<JkHtmlTag> getChildren(String tagName) {
        return JkStreams.filter(children, t -> t.getTagName().equalsIgnoreCase(tagName));
    }
    public JkHtmlTag getChildren(String tagName, int childNum) {
        List<JkHtmlTag> chlist = getChildren(tagName);
        return childNum < chlist.size() ? chlist.get(childNum) : null;
    }
    public JkHtmlTag getFirstChildren(String tagName, String... attributes) {
        List<JkHtmlTag> chlist = getChildren(tagName, attributes);
        return chlist.isEmpty() ? null : chlist.get(0);
    }
    public List<JkHtmlTag> getChildren(String tagName, String... attributes) {
        List<JkHtmlTag> chlist = getChildren(tagName);
        List<Pair<String, String>> attrPairs = JkStreams.filterAndMap(Arrays.asList(attributes), s -> s.contains("="), s -> Pair.of(s.split("=")[0], s.split("=")[1]));
        List<JkHtmlTag> finalList = new ArrayList<>();
        for(JkHtmlTag child : chlist) {
            boolean add = true;
            for (Pair<String, String> pair : attrPairs) {
                if (!pair.getValue().equals(child.getAttribute(pair.getKey()))) {
                    add = false;
                    break;
                }
            }
            if(add) {
                finalList.add(child);
            }
        }
        return finalList;
    }

    public String getTextTag() {
        return getTextTag("");
    }
    public String getTextTag(String joiner) {
        String str = textTagLines.isEmpty() ? null : JkStreams.join(textTagLines, joiner);
        if(str == null) return null;
        return str.contains("&") ? HtmlChars.escapeHtmlChars(str) : str;
    }
    public String getAllTextInside() {
        return getAllTextInside("");
    }
    public String getAllTextInside(String joiner) {
        String str = allTextInsideLines.isEmpty() ? null : JkStreams.join(allTextInsideLines, joiner);
        if(str == null) return null;
        return str.contains("&") ? HtmlChars.escapeHtmlChars(str) : str;
    }

    public List<String> getTextTagLines() {
        return textTagLines;
    }
    public List<String> getAllTextInsideLines() {
        return allTextInsideLines;
    }

    public boolean isAutoClosed() {
        return autoClosed;
    }

    protected void addAttribute(String attrName, String attrValue) {
        attributeMap.put(attrName, attrValue);
    }
    protected void setTagName(String tagName) {
        this.tagName = tagName;
    }

    protected void setAutoClosed(boolean autoClosed) {
        this.autoClosed = autoClosed;
    }

    @Override
    public String toString() {
        return strf("%s%s", tagName, children.isEmpty() ? "" : "  ("+children.size()+")");
    }

}
