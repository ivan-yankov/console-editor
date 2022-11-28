package yankov.console.table.viewer;

public class TableViewerSettings {
    private final boolean showRowIndexes;
    private final boolean autoCorrectDecimalSymbol;
    private final Integer decimalPlaces;

    public TableViewerSettings(boolean showRowIndexes,
                               boolean autoCorrectDecimalSymbol,
                               Integer decimalPlaces) {
        this.showRowIndexes = showRowIndexes;
        this.autoCorrectDecimalSymbol = autoCorrectDecimalSymbol;
        this.decimalPlaces = decimalPlaces;
    }

    public boolean isShowRowIndexes() {
        return showRowIndexes;
    }

    public boolean isAutoCorrectDecimalSymbol() {
        return autoCorrectDecimalSymbol;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public TableViewerSettings withShowRowIndexes(boolean showRowIndexes) {
        return new TableViewerSettings(showRowIndexes, autoCorrectDecimalSymbol, decimalPlaces);
    }

    public TableViewerSettings withAutoCorrectDecimalSymbol(boolean autoCorrectDecimalSymbol) {
        return new TableViewerSettings(showRowIndexes, autoCorrectDecimalSymbol, decimalPlaces);
    }

    public TableViewerSettings withDecimalPlaces(Integer decimalPlaces) {
        return new TableViewerSettings(showRowIndexes, autoCorrectDecimalSymbol, decimalPlaces);
    }
}
