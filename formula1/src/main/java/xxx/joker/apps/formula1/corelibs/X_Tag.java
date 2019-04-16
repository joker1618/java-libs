package xxx.joker.apps.formula1.corelibs;

import xxx.joker.libs.core.objects.Range;

import java.util.List;
import java.util.Map;

public interface X_Tag {

    Map<String, String> getAllAttributes();
    String getAttribute(String attrName);

    boolean hasAttribute(String attrName);
    boolean matchAttribute(String attrName, String attrValue);
    boolean matchAttributes(String... attribs);

    boolean isAutoClosed();

    String getTagName();

    X_Tag getChild(int childNum, int... subNums);
    X_Tag getChild(String tagName);
    X_Tag getChild(String tagName, String... attributes);
    List<X_Tag> getChildren();
    List<X_Tag> getChildren(String... tagNames);

    X_Tag findChild(String... tagsPaths);
    List<X_Tag> findChildren(String... tagsPaths);

    X_Tag findFirstTag(String tagName);
    X_Tag findFirstTag(String tagName, String... attributes);
    List<X_Tag> findFirstTags(String tagName);
    List<X_Tag> findFirstTags(String tagName, String... attributes);

    Range getRange();

    String getHtmlTag();
    String getText();
    String getTextFlat();
}
