package console.editor;

import console.Const;
import console.Key;
import console.Keys;
import console.date.DateConsoleSelector;
import console.util.TableFactory;
import console.util.TablePrinter;
import console.util.Utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConsoleTableEditor extends ConsoleTable<String> {
    private final Path file;

    public ConsoleTableEditor(Table<String> table, Path file, int consoleLines, int consoleColumns) {
        super(table, consoleLines, consoleColumns);
        this.file = file;
    }

    @Override
    protected Map<Mode, Stream<Command>> additionalCommands() {
        Stream<Command> selectModeCommands = Stream.of(
                new Command(Keys.F2, this::editCell, "Edit"),
                new Command(Keys.CTRL_F2, this::selectDate, "Select date"),
                new Command(Keys.F3, this::saveTable, "Save"),
                new Command(Keys.F4, this::close, "Close"),
                new Command(Keys.F5, this::moveRowUp, "Move up"),
                new Command(Keys.F6, this::moveRowDown, "Move down"),
                new Command(Keys.F7, this::insertRow, "Insert after"),
                new Command(Keys.F8, this::deleteRow, "Delete row"),
                new Command(Keys.CTRL_DELETE, this::deleteColumn, "Delete column"),
                new Command(Keys.CTRL_X, this::cut, "Cut"),
                new Command(Keys.CTRL_C, this::copy, "Copy"),
                new Command(Keys.CTRL_V, this::paste, "Paste"),
                new Command(Keys.DELETE, this::deleteCellValue, "Delete")
        );

        Stream<Command> editModeCommands = Stream.of(
                new Command(Keys.ESC, this::onEsc, "Discard changes"),
                new Command(Keys.ENTER, this::onEnter, "Accept new value")
        );

        Map<Mode, Stream<Command>> result = new HashMap<>();
        result.put(Mode.SELECT, selectModeCommands);
        result.put(Mode.EDIT, editModeCommands);
        return result;
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

    private void selectDate() {
        if (getFocus().isValid()) {
            DateConsoleSelector dateSelector = TableFactory.createDateConsoleSelector(
                    Utils.firstDayOfCurrentMonth(),
                    getConsoleLines(),
                    getConsoleColumns(),
                    date -> getTable().setCellValue(Utils.printDate(date), getFocus().getRow(), getFocus().getCol())
            );
            dateSelector.show();
        }
    }

    private void onEsc() {
        setMode(Mode.SELECT);
        setUserInput("");
    }

    private void onEnter() {
        getTable().setCellValue(getUserInput(), getFocus().getRow(), getFocus().getCol());
        setMode(Mode.SELECT);
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

    private void close() {
        setMode(Mode.CLOSE);
    }

    private void moveRowUp() {
        if (getFocus().getRow() > 0) {
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
        getTable().insertRowAt(getFocus().getRow() + 1);
        if (!getFocus().isValid()) {
            resetFocus();
        }
    }

    private void deleteRow() {
        if (getFocus().isValid()) {
            getTable().deleteRow(getFocus().getRow());
        }
        if (getTable().getRowCount() == 0) {
            invalidateFocus();
        } else {
            int row = getFocus().getRow() - 1;
            if (row < 0) row++;
            getFocus().setRow(row);
        }
    }

    private void deleteColumn() {
        if (getFocus().isValid()) {
            getTable().deleteCol(getFocus().getCol());
        }
        if (getTable().getColCount() == 0) {
            invalidateFocus();
        } else {
            int col = getFocus().getCol() - 1;
            if (col < 0) col++;
            getFocus().setRow(col);
        }
    }

    private void cut() {
        setValueToClipboard();
        deleteCellValue();
    }

    private void copy() {
        setValueToClipboard();
    }

    private void paste() {
        if (getFocus().isValid()) {
            Transferable contents = getClipboard().getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String value = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    deleteCellValue();
                    getTable().setCellValue(value, getFocus().getRow(), getFocus().getCol());
                } catch (UnsupportedFlavorException | IOException ex) {
                    // ignore
                }
            }
        }
    }

    private void deleteCellValue() {
        if (getFocus().isValid()) {
            getTable().setCellValue(getTable().getEmptyValue().get(), getFocus().getRow(), getFocus().getCol());
        }
    }

    private Clipboard getClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    private void setValueToClipboard() {
        if (getFocus().isValid()) {
            String value = getTable().getCellValue(getFocus().getRow(), getFocus().getCol());
            StringSelection stringSelection = new StringSelection(value);
            getClipboard().setContents(stringSelection, null);
        }
    }
}
