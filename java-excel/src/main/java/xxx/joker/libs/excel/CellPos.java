package xxx.joker.libs.excel;

class CellPos {
    
    private int rowNum;
    private int colNum;

    public CellPos(int rowNum, int colNum) {
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
