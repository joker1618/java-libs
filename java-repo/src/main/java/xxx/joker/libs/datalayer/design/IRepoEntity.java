package xxx.joker.libs.datalayer.design;

import org.apache.commons.lang3.builder.ToStringStyle;
import xxx.joker.libs.core.datetime.JkDateTime;

import java.time.LocalDateTime;

interface IRepoEntity<T> extends Comparable<T> {

    String getPrimaryKey();

    Long getEntityId();
    void setEntityId(Long entityId);

    JkDateTime getCreationTm();
    void setCreationTm();
    void setCreationLdt(LocalDateTime creationLdt);
    void setCreationTm(JkDateTime creationTm);

    String strMini();
    String strShort();
    String strFull();
    String strFull(ToStringStyle sstyle);

}
