package xxx.joker.libs.core.repository.entity;

import xxx.joker.libs.core.ToAnalyze;

import java.time.LocalDateTime;

@ToAnalyze
@Deprecated
public interface JkEntity extends Comparable<JkEntity> {

    String getPrimaryKey();

    Long getEntityID();
    void setEntityID(long entityID);

    LocalDateTime getInsertTstamp();
    void setInsertTstamp(LocalDateTime insertTstamp);

    @Override
    default int compareTo(JkEntity o) {
        return getPrimaryKey().compareTo(o.getPrimaryKey());
    }


}
