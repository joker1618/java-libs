package xxx.joker.libs.core.format.csv;

import xxx.joker.libs.core.util.JkStruct;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static xxx.joker.libs.core.util.JkConvert.*;

public class JkCsvRow {

    private Supplier<List<String>> header;
    private List<String> origData;
    private List<String> currentData;

    JkCsvRow(Supplier<List<String>> header, List<String> origData) {
        this.header = header;
        this.origData = origData;
        this.currentData = new ArrayList<>(origData);
    }

    public void resetChanges() {
        currentData.clear();
        currentData.addAll(origData);
    }

    public List<String> getHeader() {
        return header.get();
    }

    public List<String> getOrigData() {
        return origData;
    }

    public List<String> getCurrentData() {
        return currentData;
    }

    public boolean getBoolean(int colNum) {
        return toBoolean(currentData.get(colNum));
    }
    public boolean getBoolean(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header.get(), colName);
        return getBoolean(index);
    }

    public String getString(int colNum) {
        return currentData.get(colNum);
    }
    public String getString(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header.get(), colName);
        return index == -1 ? null : getString(index);
    }

    public Integer getInt(int colNum) {
        return toInt(currentData.get(colNum));
    }
    public Integer getInt(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header.get(), colName);
        return index == -1 ? null : getInt(index);
    }

    public Double getDouble(int colNum) {
        return toDouble(currentData.get(colNum));
    }
    public Double getDouble(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header.get(), colName);
        return index == -1 ? null : getDouble(index);
    }

    public void removeCol(String colName) {
        int index = JkStruct.indexOfIgnoreCase(header.get(), colName);
        if(index != -1) {
            currentData.remove(index);
        }
    }
}
