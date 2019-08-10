package xxx.joker.libs.datalayer.design;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import xxx.joker.libs.core.datetime.JkDateTime;

import java.time.LocalDateTime;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public abstract class RepoEntity implements IRepoEntity<RepoEntity> {

    @EntityID
    protected Long entityId;
    @EntityCreationTm
    protected JkDateTime creationTm;

    @Override
    public final int hashCode() {
        return getPrimaryKey().toLowerCase().hashCode();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoEntity other = (RepoEntity) o;
        return getPrimaryKey().equalsIgnoreCase(other.getPrimaryKey());
    }

    @Override
    public String toString() {
        return strShort();
    }

    @Override
    public final Long getEntityId() {
        return entityId;
    }

    @Override
    public final JkDateTime getCreationTm() {
        return creationTm;
    }

    @Override
    public final int compareTo(RepoEntity o) {
        return getPrimaryKey().compareToIgnoreCase(o.getPrimaryKey());
    }

    @Override
    public String strShort() {
        return strf("{}[{},{}]", getClass().getSimpleName(), entityId, getPrimaryKey());
    }

    @Override
    public String strMini() {
        return strf("[{},{}]", entityId, getPrimaryKey());
    }

    @Override
    public String strFull() {
        return strFull(ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public String strFull(ToStringStyle sstyle) {
        return ToStringBuilder.reflectionToString(this, sstyle);
    }

    @Override
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

     @Override
    public final void setCreationTm() {
        this.creationTm = JkDateTime.now();
    }

    @Override
    public final void setCreationLdt(LocalDateTime creationLdt) {
        this.creationTm = JkDateTime.of(creationLdt);
    }

    @Override
    public final void setCreationTm(JkDateTime creationTm) {
        this.creationTm = creationTm;
    }
}
