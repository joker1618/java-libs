package xxx.joker.libs.repository.design;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public abstract class JkRepoEntity implements JkEntity {

    protected Long entityID;
    protected LocalDateTime insertTstamp;

    @Override
    public int hashCode() {
        return getPrimaryKey().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JkEntity other = (JkEntity) o;
        return getPrimaryKey().equals(other.getPrimaryKey());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    @Override
    public final Long getEntityID() {
        return entityID;
    }

    @Override
    public final void setEntityID(Long entityID) {
        this.entityID = entityID;
    }

    @Override
    public final LocalDateTime getInsertTstamp() {
        return insertTstamp;
    }

    @Override
    public final void setInsertTstamp(LocalDateTime insertTstamp) {
        this.insertTstamp = insertTstamp;
    }

    @Override
    public int compareTo(JkEntity o) {
        return getPrimaryKey().compareTo(o.getPrimaryKey());
    }

    @Override
    public boolean isRegistered() {
        return entityID != null;
    }
}
