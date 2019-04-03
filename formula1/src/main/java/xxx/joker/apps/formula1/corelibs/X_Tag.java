package xxx.joker.apps.formula1.corelibs;

import java.util.Map;

public interface X_Tag {


    Map<String, String> getAttributes();

    String getAttribute(String attrName);

    boolean isAutoClosed();

    String getTagName();
}
