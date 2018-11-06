package xxx.joker.libs.core.html;

import xxx.joker.libs.core.utils.JkStreams;

import java.util.*;

public class JkHtmlTag {

    private String tagName;
    private Map<String, String> attributeMap;
    private List<JkHtmlTag> children;
    private String textInside;
    private boolean selfClosed;

    protected JkHtmlTag() {
        attributeMap = new HashMap<>();
        children = new ArrayList<>();
    }

    protected JkHtmlTag(String tagName) {
        this.tagName = tagName;
        attributeMap = new HashMap<>();
        children = new ArrayList<>();
    }

    public String getTagName() {
        return tagName;
    }
    protected void addAttribute(String attrName, String attrValue) {
        attributeMap.put(attrName, attrValue);
    }
    public String getAttribute(String attrName) {
        return attributeMap.get(attrName);
    }

    protected void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Map<String, String> getAttributes() {
        return attributeMap;
    }

    public boolean containsAttribute(String attrName) {
        return attributeMap.get(attrName) != null;
    }
    public List<JkHtmlTag> getChildren() {
        return children;
    }
    public String getTextInside() {
        return textInside;
    }

    protected void setTextInside(String textInside) {
        this.textInside = textInside;
    }

    public List<JkHtmlTag> getChildrenTags() {
        return new ArrayList<>(children);
    }
    public List<JkHtmlTag> getChildrenTags(String childName) {
        return getChildrenTags(children, childName);
    }
    private List<JkHtmlTag> getChildrenTags(List<JkHtmlTag> source, String childName) {
        List<JkHtmlTag> filter = JkStreams.filter(source, t -> t.getTagName().equals(childName));
        if(!filter.isEmpty())   return filter;
        for(JkHtmlTag f : source) {
            List<JkHtmlTag> res = getChildrenTags(f.getChildren(), childName);
            if(!res.isEmpty())   return res;
        }
        return Collections.emptyList();
    }

    public boolean isSelfClosed() {
        return selfClosed;
    }

    public void setSelfClosed(boolean selfClosed) {
        this.selfClosed = selfClosed;
    }
}
