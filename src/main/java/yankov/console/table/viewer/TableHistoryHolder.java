package yankov.console.table.viewer;

import yankov.console.table.Table;

public class TableHistoryHolder {
    private static HistoryHolder<Table<String>> instance;

    private TableHistoryHolder() {
    }

    public static synchronized HistoryHolder<Table<String>> getInstance() {
        if (instance == null) {
            instance = new HistoryHolder<>();
        }
        return instance;
    }
}
