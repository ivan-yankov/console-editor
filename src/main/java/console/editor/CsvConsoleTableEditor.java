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
                new Command(Keys.F2, this::editCell, "Edit cell"),
                new Command(Keys.F3, this::saveTable, "Save"),
                new Command(Keys.F5, this::moveRowUp, "Move row up"),
                new Command(Keys.F6, this::moveRowDown, "Move row down"),
                new Command(Keys.F7, this::insertRow, "Insert row after"),
                new Command(Keys.F8, this::deleteRow, "Delete row"),
                new Command(Keys.DELETE, this::deleteCellValue, "Delete cell value")
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

    private void editCell() {
        if (getFocus().isValid()) {
            setMode(Mode.EDIT);
        }
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

    private void saveTable() {
        Utils.writeFile(file, TablePrinter.toCsv(getTable()) + Const.NEW_LINE);
        setLogMessage("Saved in " + file.toString());
    }

    private void moveRowUp() {
        if (getFocus().getRow() > 1) {
            getTable().swapRows(getFocus().getRow(), getFocus().getRow() - 1);
            getFocus().setRow(getFocus().getRow() - 1);
        }
    }

    private void moveRowDown() {
        if (getFocus().getRow() < getTable().getRowCount() - 1) {
            getTable().swapRows(getFocus().getRow(), getFocus().getRow() + 1);
            getFocus().setRow(getFocus().getRow() + 1);
        }
    }

    private void insertRow() {
        Integer row = !getFocus().isValid() ? 1 : getFocus().getRow() + 1;
        getTable().insertRowAt(row);
        if (!getFocus().isValid()) {
            initFocus();
        }
    }

    private void deleteRow() {
        if (getFocus().isValid()) {
            getTable().deleteRow(getFocus().getRow());
        }
        if (getTable().getRowCount() == 0) {
            invalidateFocus();
        }
    }

    private void deleteCellValue() {
        if (getFocus().isValid()) {
            getTable().setCellValue("", getFocus().getRow(), getFocus().getCol());
        }
    }
}
