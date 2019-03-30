package xxx.joker.libs.repository.design2;

import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

interface IRepoEntity<T> extends Comparable<T> {

    String getPrimaryKey();

    Long getEntityID();

    String strShort();
    String strFull();
    String strFull(ToStringStyle sstyle);
}
