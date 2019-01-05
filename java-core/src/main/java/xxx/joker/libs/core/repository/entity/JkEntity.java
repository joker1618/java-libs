package xxx.joker.libs.core.repository.entity;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
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
