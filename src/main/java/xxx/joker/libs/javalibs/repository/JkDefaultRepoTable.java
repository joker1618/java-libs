package xxx.joker.libs.javalibs.repository;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public abstract class JkDefaultRepoTable implements JkRepoTable {

    @Override
    public int compareTo(JkRepoTable o) {
        return StringUtils.compare(getPrimaryKey(), o.getPrimaryKey());
    }

    @Override
    public int hashCode() {
        return getPrimaryKey().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JkRepoTable other = (JkRepoTable) o;
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

}
