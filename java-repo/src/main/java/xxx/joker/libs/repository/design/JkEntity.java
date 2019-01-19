package xxx.joker.libs.repository.design;

import java.time.LocalDateTime;

public interface JkEntity extends Comparable<JkEntity> {

    String getPrimaryKey();

    Long getEntityID();
    void setEntityID(Long entityID);

    LocalDateTime getInsertTstamp();
    void setInsertTstamp(LocalDateTime insertTstamp);

    boolean isRegistered();
}
