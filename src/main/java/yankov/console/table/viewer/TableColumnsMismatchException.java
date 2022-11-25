package yankov.console.table.viewer;

public class TableColumnsMismatchException extends RuntimeException {
    public TableColumnsMismatchException() {
        super("Number of header and data columns do not match");
    }
}
