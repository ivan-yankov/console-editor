package console.editor;

public class Focus {
    private Integer row;
    private Integer col;

    public Focus(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public boolean isValid() {
        return row >= 0 && col >= 0;
    }
}
