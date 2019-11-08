package xxx.joker.libs.datalayer.dao;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import static xxx.joker.libs.core.utils.JkStrings.strf;

class DaoFK_v3  {
//public class DaoFK {

    private static final String SEP = "|";

    private long sourceID;
    private String fieldName;
    // only for map
    private int idxKey = -1;
    // only for map (id when Pair.key=true, else string fmt), null for collection
    private Pair<Boolean, String> mapKey = null;
    // only for collection or map with collection as a value
    private int idxValue = -1;
    // id for collection, id when Pair.key=true, else string fmt for map
    private Pair<Boolean, String> value = null;

    public DaoFK_v3() {sgdhet

    }
    public DaoFK_v3(long sourceID, String fieldName, int idxValue, String value) {
        this.sourceID = sourceID;
        this.fieldName = fieldName;
        this.idxValue = idxValue;
        this.value = Pair.of(true, value);
    }




}
