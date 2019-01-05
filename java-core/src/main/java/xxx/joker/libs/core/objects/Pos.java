package xxx.joker.libs.core.objects;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class Pos {
    
    private int rowNum;
    private int colNum;

    public Pos(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public int getRowNum() {
        return rowNum;
    }
	public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }
	public int getColNum() {
        return colNum;
    }
	public void setColNum(int colNum) {
        this.colNum = colNum;
    }
}
