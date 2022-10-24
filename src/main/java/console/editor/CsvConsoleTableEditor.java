package console.editor;

import console.Key;
import console.Keys;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CsvConsoleTableEditor extends ConsoleTableEditor {
    public CsvConsoleTableEditor(Table table) {
        super(table);
    }

    @Override
    protected Map<Mode, Stream<Command>> commands() {
        Stream<Command> commandModeCommands = Stream.of(
                new Command(Keys.F2, this::onEdit, "Edit cell")
        );
        Stream<Command> editModeCommands = Stream.of(
                new Command(Keys.ESC, this::onEsc, "Edit cell"),
                new Command(Keys.ENTER, this::onEnter, "Edit cell"),
                new Command(Keys.F2, this::onEdit, "Edit cell")
        );

        Map<Mode, Stream<Command>> m = new HashMap<>();
        m.put(Mode.COMMAND, Stream.concat(super.commands().get(Mode.COMMAND), commandModeCommands));
        m.put(Mode.EDIT, editModeCommands);
        return m;
    }

    @Override
    protected Command defaultCommand(Key k) {
        if (getMode() == Mode.EDIT) {
            return new Command(k, () -> onUserKeyPress(k), "Type user input");
        }
        return super.defaultCommand(k);
    }

    private void onEdit() {
        setMode(Mode.EDIT);
    }

    private void onEsc() {
        setMode(Mode.COMMAND);
        setUserInput("");
    }

    private void onEnter() {
        getTable().setCellValue(getUserInput(), getFocus().getRow(), getFocus().getCol());
        setMode(Mode.COMMAND);
        setUserInput("");
    }

    private void onUserKeyPress(Key k) {
        if (!Keys.asList().contains(k)) {
            setUserInput(getUserInput() + k.getName());
        }
    }
}
