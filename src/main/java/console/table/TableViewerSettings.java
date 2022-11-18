package console.table;

public class TableViewerSettings {
    private boolean showRowIndexes;
    private boolean autoCorrectDecimalSymbol;

    public TableViewerSettings(boolean showRowIndexes, boolean autoCorrectDecimalSymbol) {
        this.showRowIndexes = showRowIndexes;
        this.autoCorrectDecimalSymbol = autoCorrectDecimalSymbol;
    }

    public boolean isShowRowIndexes() {
        return showRowIndexes;
    }

    public void setShowRowIndexes(boolean showRowIndexes) {
        this.showRowIndexes = showRowIndexes;
    }

    public boolean isAutoCorrectDecimalSymbol() {
        return autoCorrectDecimalSymbol;
    }

    public void setAutoCorrectDecimalSymbol(boolean autoCorrectDecimalSymbol) {
        this.autoCorrectDecimalSymbol = autoCorrectDecimalSymbol;
    }
}
