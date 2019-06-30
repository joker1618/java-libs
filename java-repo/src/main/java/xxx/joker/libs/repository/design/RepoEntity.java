package xxx.joker.libs.repository.design;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import xxx.joker.libs.core.datetime.JkDateTime;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public abstract class RepoEntity implements IRepoEntity<RepoEntity> {

    @RepoEntityID
    protected Long entityID;
    @RepoEntityCreationTm
    protected JkDateTime creationTm;

    @Override
    public int hashCode() {
        return getPrimaryKey().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoEntity other = (RepoEntity) o;
        return getPrimaryKey().equals(other.getPrimaryKey());
    }

    @Override
    public String toString() {
        return strShort();
    }

    @Override
    public final Long getEntityID() {
        return entityID;
    }

    @Override
    public final JkDateTime getCreationTm() {
        return creationTm;
    }

    @Override
    public int compareTo(RepoEntity o) {
        return getPrimaryKey().compareTo(o.getPrimaryKey());
    }

    @Override
    public String strShort() {
        return strf("{}[{},{}]", getClass().getSimpleName(), entityID, getPrimaryKey());
    }

    @Override
    public String strFull() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public String strFull(ToStringStyle sstyle) {
        return ToStringBuilder.reflectionToString(this, sstyle);
    }

    @Override
    public void setCreationTm(JkDateTime creationTm) {
        this.creationTm = creationTm;
    }
}
