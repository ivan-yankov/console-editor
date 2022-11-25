package yankov.console.table.viewer;

import yankov.console.table.Table;

public class TableChangeHandler<T> {
    private final HistoryHolder<Table<T>> historyHolder;

    public TableChangeHandler() {
        this.historyHolder = new HistoryHolder<>();
    }

    public HistoryHolder<Table<T>> getHistoryHolder() {
        return historyHolder;
    }

    public void handleTableChange(Table<T> oldValue) {
        historyHolder.addUndoState(oldValue);
    }
}
