package console.menu;

import console.Key;
import console.Keys;
import console.model.Command;
import console.model.Pair;
import console.table.ConsoleTableViewer;
import console.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ConsoleMenu extends ConsoleTableViewer<Command> {
    public ConsoleMenu(Table<Command> table, int consoleLines, int consoleColumns, Supplier<String> consoleReadLine) {
        super(table, consoleLines, consoleColumns, consoleReadLine);
    }

    @Override
    protected List<Pair<Key, Command>> addCommands() {
        List<Pair<Key, Command>> c = new ArrayList<>();
        c.add(new Pair<>(Keys.ENTER, new Command(this::onEnter, "Execute")));
        return c;
    }

    private void onEnter() {
        if (getFocus().isValid()) {
            getTable().getCellValue(getFocus().getRow(), getFocus().getCol()).getAction().execute();
        }
    }
}
