package xxx.joker.apps.formula1.corelibs;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.objects.Range;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.*;

class X_TagImpl implements X_Tag {

    // The root tag has null parent, but has the html set
    // All other tags:
    // - have a tag parent, but not html string
    // - to retrieve the html, they ask to parent
    private X_TagImpl parent;
    private String html;

    private String tagName;
    private int startPos;
    private int endPos;
    private boolean autoClosed;
    private Map<String, String> attributes;
    private List<X_Tag> children;


    public X_TagImpl() {
        this(null);
    }
    public X_TagImpl(String tagName) {
        this.tagName = tagName;
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
        this.startPos = -1;
        this.endPos = -1;
    }

    @Override
    public Map<String, String> getAllAttributes() {
        return new HashMap<>(attributes);
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

    @Override
    public X_Tag getChild(int childNum) {
        return childNum < children.size() ? children.get(childNum) : null;
    }

    @Override
    public X_Tag getChild(String tagName) {
        List<X_Tag> children = getChildren(tagName);
        return children.isEmpty() ? null : children.get(0);
    }

    @Override
    public List<X_Tag> getChildren() {
        return children;
    }

    @Override
    public List<X_Tag> getChildren(String... tagNames) {
        return JkStreams.filter(children, ch -> JkTests.containsIgnoreCase(tagNames, ch.getTagName()));
    }

    @Override
    public X_Tag findChild(String... tagsPaths) {
        List<X_Tag> res = findChildren(tagsPaths);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public List<X_Tag> findChildren(String... tagsPaths) {
        for (String tagsPath : tagsPaths) {
            List<X_Tag> childs = findTagChilds(tagsPath);
            if(!childs.isEmpty())   return childs;
        }
        return Collections.emptyList();
    }

    @Override
    public X_Tag findFirstTag(String tagName) {
        List<X_Tag> res = findFirstTags(tagName);
        return res.isEmpty() ? null : res.get(0);
    }

    @Override
    public List<X_Tag> findFirstTags(String tagName) {
        List<X_Tag> res = getChildren(tagName);
        if(res.isEmpty()) {
            for (X_Tag child : children) {
                res = child.findFirstTags(tagName);
                if(!res.isEmpty()) {
                    return res;
                }
            }
        }
        return res;
    }

    private List<X_Tag> findTagChilds(String tagsPath) {
        X_Tag t = this;
        int pos = 0;
        String[] tagsName = JkStrings.splitArr(tagsPath, " ", true);
        for(; pos < tagsName.length - 1; pos++) {
            String tn = tagsName[pos];
            t = t.getChild(tn);
            if(t == null) {
                return Collections.emptyList();
            }
        }
        return t.getChildren(tagsName[pos]);
    }

    @Override
    public Range getRange() {
        return Range.ofBounds(startPos, endPos);
    }

    @Override
    public String getHtmlTag() {
        return getFullHtml().substring(startPos, endPos);
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        if(!children.isEmpty()) {
            List<Range> chRanges = JkStreams.map(children, ch -> ch.getRange().shiftStart(-1 * startPos));
            String htag = getHtmlTag();
            int start = 0;
            for (Range r : chRanges) {
                sb.append(htag, start, r.getStart());
                start = r.getEnd();
            }
            sb.append(htag.substring(start));
        } else {
            sb.append(getHtmlTag());
        }

        String str = sb.toString().replaceAll("^<[^<]*?>", "").replaceAll("</[^<]*?>$", "");
        return X_HtmlChars.fixDirtyChars(str).trim();
    }


    @Override
    public String getTextFlat() {
        return getHtmlTag().replaceAll("<[^<]*?>", "").trim();
    }

    private String getFullHtml() {
        return html == null ? parent.getFullHtml() : html;
    }

    public X_Tag getParent() {
        return parent;
    }

    public int getStartPos() {
        return startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    protected void setTagName(String tagName) {
        this.tagName = tagName;
    }

    protected void setAutoClosed(boolean autoClosed) {
        this.autoClosed = autoClosed;
    }

    protected void setParent(X_TagImpl parent) {
        this.parent = parent;
    }

    protected void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    protected void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    protected Map<String, String> getAttributes() {
        return attributes;
    }

    protected void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    protected void setHtml(String html) {
        this.html = html;
    }
}