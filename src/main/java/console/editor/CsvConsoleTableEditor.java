package console.editor;

import console.Const;
import console.Key;
import console.Keys;
import console.util.TablePrinter;
import console.util.Utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CsvConsoleTableEditor extends ConsoleTableEditor {
    private final Path file;

    public CsvConsoleTableEditor(Table table, Path file) {
        super(table);
        this.file = file;
    }

    @Override
    protected Map<Mode, Stream<Command>> commands() {
        Stream<Command> commandModeCommands = Stream.of(
                new Command(Keys.F2, this::onEdit, "Edit cell"),
                new Command(Keys.F3, this::onSave, "Save")
        );
        Stream<Command> editModeCommands = Stream.of(
                new Command(Keys.ESC, this::onEsc, "Discard changes"),
                new Command(Keys.ENTER, this::onEnter, "Accept new value")
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

    private void onSave() {
        Utils.writeFile(file, TablePrinter.toCsv(getTable()) + Const.NEW_LINE);
        setLogMessage("Saved in " + file.toString());
    }
}
