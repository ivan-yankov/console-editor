package yankov.console.table.viewer;

import yankov.console.model.Command;
import yankov.console.operations.ConsoleOperations;
import yankov.console.table.Table;

import java.util.List;

public class ConsoleMenu extends ConsoleTableViewer<Command> {
    public ConsoleMenu(Table<Command> table, int consoleLines, int consoleColumns, ConsoleOperations consoleOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
        setSettings(getSettings().withShowRowIndexes(false));
    }

    @Override
    protected void onEnter() {
        if (getFocus().isValid()) {
            getTable().getCell(getFocus().getRow(), getFocus().getCol()).getValue().getAction().accept(List.of());
        }
    }

    @Override
    protected boolean showFooter() {
        return false;
    }
}
