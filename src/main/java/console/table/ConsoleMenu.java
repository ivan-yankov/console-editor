package console.table;

import console.Key;
import console.model.Command;
import console.model.Pair;
import console.operations.ConsoleOperations;

import java.util.ArrayList;
import java.util.List;

public class ConsoleMenu extends ConsoleTableViewer<Command> {
    public ConsoleMenu(Table<Command> table, int consoleLines, int consoleColumns, ConsoleOperations consoleOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
    }

    @Override
    protected List<Pair<Key, Command>> addCommands() {
        List<Pair<Key, Command>> c = new ArrayList<>();
        c.add(new Pair<>(Key.ENTER, new Command(this::onEnter, "Execute")));
        return c;
    }

    private void onEnter() {
        if (getFocus().isValid()) {
            getTable().getCellValue(getFocus().getRow(), getFocus().getCol()).getAction().execute();
        }
    }
}
