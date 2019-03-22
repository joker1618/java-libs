package xxx.joker.libs.core.html;


import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.*;

import static xxx.joker.libs.core.utils.JkStrings.strf;

class JkHtmlTagImpl implements JkHtmlTag {

    private String tagName;
    private Map<String, String> attributeMap;
    private List<JkHtmlTag> children;
    private List<String> textTagLines;
    private List<String> allTextInsideLines;
    private boolean autoClosed;
    private String origHtml;

    protected JkHtmlTagImpl() {
        this.attributeMap = new HashMap<>();
        this.children = new ArrayList<>();
        this.textTagLines = new ArrayList<>();
        this.allTextInsideLines = new ArrayList<>();
    }

    protected JkHtmlTagImpl(String tagName) {
        this.tagName = tagName;
        this.attributeMap = new HashMap<>();
        this.children = new ArrayList<>();
        this.textTagLines = new ArrayList<>();
        this.allTextInsideLines = new ArrayList<>();
    }

    @Override
    public JkHtmlTag findFirst(String childName, String... attributes) {
        List<JkHtmlTag> tags = findFirsts(childName, attributes);
        return tags.isEmpty() ? null : tags.get(0);
    }
    @Override
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
            toRet.addAll(children.get(i).findFirsts(childName, attributes));
        }

        return toRet;
    }

    @Override
    public List<JkHtmlTag> findAll(String childName, String... attributes) {
        return findAllTagList(childName, attributes);
    }
    private List<JkHtmlTag> findAllTagList(String childName, String... attributes) {
        List<Pair<String, String>> attrPairs = JkStreams.filterMap(Arrays.asList(attributes), s -> s.contains("="), s -> Pair.of(s.split("=")[0], s.split("=")[1]));

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
            toRet.addAll(child.findAll(childName, attributes));
        }

        return toRet;
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public Map<String, String> getAttributes() {
        return new HashMap<>(attributeMap);
    }
    @Override
    public String getAttribute(String attrName) {
        return attributeMap.get(attrName);
    }

    @Override
    public List<JkHtmlTag> getChildren() {
        return children;
    }
    @Override
    public List<JkHtmlTag> getChildren(String tagName) {
        return JkStreams.filter(children, t -> t.getTagName().equalsIgnoreCase(tagName));
    }
    @Override
    public JkHtmlTag getChildren(String tagName, int childNum) {
        List<JkHtmlTag> chlist = getChildren(tagName);
        return childNum < chlist.size() ? chlist.get(childNum) : null;
    }
    @Override
    public JkHtmlTag getFirstChildren(String tagName, String... attributes) {
        List<JkHtmlTag> chlist = getChildren(tagName, attributes);
        return chlist.isEmpty() ? null : chlist.get(0);
    }
    @Override
    public List<JkHtmlTag> getChildren(String tagName, String... attributes) {
        List<JkHtmlTag> chlist = getChildren(tagName);
        List<Pair<String, String>> attrPairs = JkStreams.filterMap(Arrays.asList(attributes), s -> s.contains("="), s -> Pair.of(s.split("=")[0], s.split("=")[1]));
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

    @Override
    public String getTextTag() {
        return getTextTag("");
    }
    @Override
    public String getTextTag(String joiner) {
        String str = textTagLines.isEmpty() ? null : JkStreams.join(textTagLines, joiner);
        if(str == null) return null;
        return str.contains("&") ? HtmlChars.escapeHtmlChars(str) : str;
    }
    @Override
    public String getAllTextInside() {
        return getAllTextInside("");
    }
    @Override
    public String getAllTextInside(String joiner) {
        String str = allTextInsideLines.isEmpty() ? null : JkStreams.join(allTextInsideLines, joiner);
        if(str == null) return null;
        return str.contains("&") ? HtmlChars.escapeHtmlChars(str) : str;
    }

    @Override
    public List<String> getTextTagLines() {
        return textTagLines;
    }
    @Override
    public List<String> getAllTextInsideLines() {
        return allTextInsideLines;
    }

    @Override
    public String getHtml() {
        return origHtml.contains("&") ? HtmlChars.escapeHtmlChars(origHtml) : origHtml;
    }
    @Override
    public String getTextInside() {
        String html = origHtml.contains("&") ? HtmlChars.escapeHtmlChars(origHtml) : origHtml;
        return html.replaceAll("^<(.*?)>", "").replaceAll("</(.*?)>$", "");
    }

    @Override
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
    protected void setHtml(String origHtml) {
        this.origHtml = origHtml;
    }


    @Override
    public String toString() {
        return strf("%s%s", tagName, children.isEmpty() ? "" : "  ("+children.size()+")");
    }

}
