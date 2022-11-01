package console.table;

import console.model.Command;
import console.operations.ConsoleOperations;

public class ConsoleMenu extends ConsoleTableViewer<Command> {
    public ConsoleMenu(Table<Command> table, int consoleLines, int consoleColumns, ConsoleOperations consoleOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
    }

    @Override
    protected void onEnter() {
        if (getFocus().isValid()) {
            getTable().getCellValue(getFocus().getRow(), getFocus().getCol()).getAction().execute();
        }
    }

    @Override
    protected String getEnterLabel() {
        return "Execute";
    }
}
