package console.table;

public class Focus {
    private final int row;
    private final int col;

    public Focus(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Focus withRow(int row) {
        return new Focus(row, col);
    }

    public Focus withCol(int col) {
        return new Focus(row, col);
    }

    public boolean isValid() {
        return row >= 0 && col >= 0;
    }
}
