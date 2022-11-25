package console.table;

import console.model.Command;
import console.operations.ConsoleOperations;

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
    protected String getEnterDescription() {
        return "Execute";
    }

    @Override
    protected boolean allowCommandMode() {
        return false;
    }

    @Override
    protected List<String> getFooter() {
        if (getMode() == Mode.HELP) {
            return super.getFooter();
        } else {
            return List.of();
        }
    }
}
