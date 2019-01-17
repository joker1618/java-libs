package xxx.joker.libs.repository.managers;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

class ForeignKey {

    private long fromID;
    private int fromFieldIdx;
    private long depID;

    public ForeignKey() {

    }
    public ForeignKey(long fromID, int fromFieldIdx, long depID) {
        this.fromID = fromID;
        this.fromFieldIdx = fromFieldIdx;
        this.depID = depID;
    }

    public long getFromID() {
        return fromID;
    }
    public void setFromID(long fromID) {
        this.fromID = fromID;
    }
    public int getFromFieldIdx() {
        return fromFieldIdx;
    }
    public void setFromFieldIdx(int fromFieldIdx) {
        this.fromFieldIdx = fromFieldIdx;
    }
    public long getDepID() {
        return depID;
    }
    public void setDepID(long depID) {
        this.depID = depID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForeignKey that = (ForeignKey) o;
        return getFromID() == that.getFromID() &&
                getFromFieldIdx() == that.getFromFieldIdx() &&
                getDepID() == that.getDepID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFromID(), getFromFieldIdx(), getDepID());
    }
}
