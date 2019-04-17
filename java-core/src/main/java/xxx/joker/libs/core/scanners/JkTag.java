package xxx.joker.libs.core.scanners;

import xxx.joker.libs.core.objects.Range;

import java.util.List;
import java.util.Map;

public interface JkTag {

    Map<String, String> getAllAttributes();
    String getAttribute(String attrName);

    boolean hasAttribute(String attrName);
    boolean matchAttribute(String attrName, String attrValue);
    boolean matchAttributes(String... attribs);

    boolean isAutoClosed();

    String getTagName();

    JkTag getChild(int childNum, int... subNums);
    JkTag getChild(String tagName);
    JkTag getChild(String tagName, String... attributes);
    List<JkTag> getChildren();
    List<JkTag> getChildren(String... tagNames);

    JkTag findChild(String... tagsPaths);
    List<JkTag> findChildren(String... tagsPaths);

    JkTag findFirstTag(String tagName);
    JkTag findFirstTag(String tagName, String... attributes);
    List<JkTag> findFirstTags(String tagName);
    List<JkTag> findFirstTags(String tagName, String... attributes);

    Range getRange();

    String getHtmlTag();
    String getText();
    String getTextFlat();
}
