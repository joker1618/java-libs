package xxx.joker.libs.core.html;

import java.util.List;
import java.util.Map;

public interface JkHtmlTag {

    JkHtmlTag findFirst(String childName, String... attributes);
    List<JkHtmlTag> findFirsts(String childName, String... attributes);
    List<JkHtmlTag> findAll(String childName, String... attributes);

    String getTagName();

    Map<String, String> getAttributes();
    String getAttribute(String attrName);

    List<JkHtmlTag> getChildren();
    List<JkHtmlTag> getChildren(String tagName);
    JkHtmlTag getChildren(String tagName, int childNum);
    JkHtmlTag getFirstChildren(String tagName, String... attributes);
    List<JkHtmlTag> getChildren(String tagName, String... attributes);

    String getTextTag();
    String getTextTag(String joiner);
    String getAllTextInside();
    String getAllTextInside(String joiner);
    List<String> getTextTagLines();
    List<String> getAllTextInsideLines();

    boolean isAutoClosed();

    String getHtml();
    String getTextInside();



}
