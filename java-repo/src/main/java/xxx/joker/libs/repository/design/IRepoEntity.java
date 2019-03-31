package xxx.joker.libs.repository.design;

import org.apache.commons.lang3.builder.ToStringStyle;

interface IRepoEntity<T> extends Comparable<T> {

    String getPrimaryKey();

    Long getEntityID();

    String strShort();
    String strFull();
    String strFull(ToStringStyle sstyle);
}
