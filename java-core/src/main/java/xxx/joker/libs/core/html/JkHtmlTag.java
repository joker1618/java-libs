package xxx.joker.libs.core.html;


import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkHtmlTag {

    private String tagName;
    private Map<String, String> attributeMap;
    private List<JkHtmlTag> children;
    private List<String> textInsideLines;
    private boolean autoClosed;

    protected JkHtmlTag() {
        attributeMap = new HashMap<>();
        children = new ArrayList<>();
    }

    protected JkHtmlTag(String tagName) {
        this.tagName = tagName;
        this.attributeMap = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public JkHtmlTag findFirst(String childName, String... attributes) {
        List<JkHtmlTag> tags = findFirsts(childName, attributes);
        return tags.isEmpty() ? null : tags.get(0);
    }
    public List<JkHtmlTag> findFirsts(String childName, String... attributes) {
        return findFirstTagList(childName, attributes);
    }
    private List<JkHtmlTag> findFirstTagList(String childName, String... attributes) {
        List<Pair<String, String>> attrPairs = JkStreams.filterAndMap(Arrays.asList(attributes), s -> s.contains("="), s -> Pair.of(s.split("=")[0], s.split("=")[1]));

        List<JkHtmlTag> toRet = new ArrayList<>();

        for(JkHtmlTag child : children) {
            if(childName.equalsIgnoreCase(child.getTagName())) {
                boolean res = true;
                for(Pair<String,String> attr : attrPairs) {
                    if(attr.getValue().equals(child.getAttribute(attr.getKey()))) {
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
            toRet.addAll(children.get(i).findFirsts(childName, attributes));
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
        return new ArrayList<>(children);
    }

    public String getTextInside() {
        return textInsideLines.isEmpty() ? null : JkStreams.join(textInsideLines, "");
    }

    public List<String> getTextInsideLines() {
        return new ArrayList<>(textInsideLines);
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

    protected void setTextInsideLines(List<String> textInsideLines) {
        this.textInsideLines = textInsideLines;
    }

    protected void addChildren(JkHtmlTag child) {
        children.add(child);
    }
    protected void setAutoClosed(boolean autoClosed) {
        this.autoClosed = autoClosed;
    }

    @Override
    public String toString() {
        return strf("%s%s", tagName, children.isEmpty() ? "" : "  ("+children.size()+")");
    }

}
