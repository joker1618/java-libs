package xxx.joker.libs.javalibs.datamodel.entity;

import org.apache.commons.lang3.StringUtils;

public abstract class JkComparableEntity extends JkDefaultEntity {

    @Override
    public int compareTo(JkEntity o) {
        return StringUtils.compare(getPrimaryKey(), o.getPrimaryKey());
    }

}
