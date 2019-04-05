package xxx.joker.apps.formula1.corelibs;

import xxx.joker.libs.core.objects.Range;

import java.util.List;
import java.util.Map;

public interface X_Tag {


    Map<String, String> getAllAttributes();
    String getAttribute(String attrName);

    boolean isAutoClosed();

    String getTagName();

    X_Tag getChild(int childNum);
    X_Tag getChild(String tagName);
    List<X_Tag> getChildren();
    List<X_Tag> getChildren(String... tagNames);

    X_Tag findChild(String... tagNamesPath);
    List<X_Tag> findChildren(String... tagNamesPath);

    Range getRange();

    String getHtmlTag();
    String getText();
}
