package xxx.joker.libs.repository.engine;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

class RepoFK {

    private long fromID;
    private String fieldName;
    private long depID;

    RepoFK(long fromID, String fieldName, long depID) {
        this.fromID = fromID;
        this.fieldName = fieldName;
        this.depID = depID;
    }

    public long getFromID() {
        return fromID;
    }
    public String getFieldName() {
        return fieldName;
    }
    public long getDepID() {
        return depID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoFK that = (RepoFK) o;
        return getFromID() == that.getFromID() &&
                StringUtils.equals(getFieldName(), that.getFieldName()) &&
                getDepID() == that.getDepID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFromID(), getFieldName(), getDepID());
    }
}
