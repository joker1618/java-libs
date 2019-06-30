package xxx.joker.libs.repository.design;

import org.apache.commons.lang3.builder.ToStringStyle;
import xxx.joker.libs.core.datetime.JkDateTime;

interface IRepoEntity<T> extends Comparable<T> {

    String getPrimaryKey();

    Long getEntityID();
    JkDateTime getCreationTm();

    String strShort();
    String strFull();
    String strFull(ToStringStyle sstyle);

    void setCreationTm(JkDateTime creationTm);

}
