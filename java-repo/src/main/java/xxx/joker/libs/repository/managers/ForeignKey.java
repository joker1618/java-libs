package xxx.joker.libs.repository.managers;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ForeignKey {

    private long fromID;
    private int fromFieldIdx;
    private long depID;

    public ForeignKey(long fromID, int fromFieldIdx, long depID) {
        this.fromID = fromID;
        this.fromFieldIdx = fromFieldIdx;
        this.depID = depID;
    }
    public ForeignKey(String[] line) {
        this.fromID = JkConvert.toLong(line[0]);
        this.fromFieldIdx = JkConvert.toInt(line[1]);
        this.depID = JkConvert.toLong(line[2]);
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

}
