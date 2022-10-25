package console.date;

import console.Keys;
import console.editor.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DateConsoleSelector extends ConsoleTable<LocalDate> {
    private final Action cancel;
    private final Consumer<LocalDate> ok;

    public DateConsoleSelector(Table<LocalDate> table, Consumer<LocalDate> ok, Action cancel) {
        super(table);
        this.ok = ok;
        this.cancel = cancel;
    }

    @Override
    protected Map<Mode, Stream<Command>> additionalCommands() {
        Stream<Command> commands = Stream.of(
                new Command(Keys.ESC, this::onEsc, "Discard changes"),
                new Command(Keys.ENTER, this::onEnter, "Accept new value")
        );

        Map<Mode, Stream<Command>> result = new HashMap<>();
        result.put(Mode.SELECT, commands);
        return result;
    }

    private void onEsc() {
        cancel.execute();
    }

    private void onEnter() {
        ok.accept(getTable().getCellValue(getFocus().getRow(), getFocus().getCol()));
    }
}
