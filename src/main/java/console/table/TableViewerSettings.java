package console.table;

public class TableViewerSettings {
    private final boolean showRowIndexes;
    private final boolean autoCorrectDecimalSymbol;

    public TableViewerSettings(boolean showRowIndexes, boolean autoCorrectDecimalSymbol) {
        this.showRowIndexes = showRowIndexes;
        this.autoCorrectDecimalSymbol = autoCorrectDecimalSymbol;
    }

    public boolean isShowRowIndexes() {
        return showRowIndexes;
    }

    public boolean isAutoCorrectDecimalSymbol() {
        return autoCorrectDecimalSymbol;
    }

    public TableViewerSettings withShowRowIndexes(boolean showRowIndexes) {
        return new TableViewerSettings(showRowIndexes, autoCorrectDecimalSymbol);
    }

    public TableViewerSettings withAutoCorrectDecimalSymbol(boolean autoCorrectDecimalSymbol) {
        return new TableViewerSettings(showRowIndexes, autoCorrectDecimalSymbol);
    }
}
