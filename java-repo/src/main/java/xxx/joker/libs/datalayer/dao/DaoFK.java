package xxx.joker.libs.datalayer.dao;

import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import static xxx.joker.libs.core.utils.JkStrings.strf;

class DaoFK implements JkFormattable<DaoFK> {
//public class DaoFK {

    private static final String SEP = "|";

    private long fromID;
    private long depID;
    private String fieldName;

    public DaoFK() {

    }
    public DaoFK(long fromID, long depID, String fieldName) {
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
    public String format() {
        return strf("{}{}{}{}{}", fromID, SEP, fieldName, SEP, depID);
    }

    @Override
    public DaoFK parse(String s) {
        String[] split = JkStrings.splitArr(s, SEP);
        fromID = Long.parseLong(split[0]);
        fieldName = split[1];
        depID = Long.parseLong(split[2]);
        return this;
    }
}
