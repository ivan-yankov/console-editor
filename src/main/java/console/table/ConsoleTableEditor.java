package console.table;

import console.*;
import console.date.ConsoleDateSelector;
import console.model.Command;
import console.model.Pair;
import console.util.ConsoleTableFactory;
import console.util.TablePrinter;
import console.util.Utils;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConsoleTableEditor extends ConsoleTableViewer<String> {
    private final Path file;

    public ConsoleTableEditor(Table<String> table, Path file, int consoleLines, int consoleColumns) {
        super(table, consoleLines, consoleColumns);
        this.file = file;
    }

    @Override
    protected List<Pair<CommandKey, Command>> addCommands() {
        List<Pair<CommandKey, Command>> c = new ArrayList<>();

        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F2), new Command(this::editCell, "Edit")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.CTRL_F2), new Command(this::selectDate, "Select date")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F3), new Command(this::saveTable, "Save")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F4), new Command(this::close, "Close")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F5), new Command(this::moveRowUp, "Move up")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F6), new Command(this::moveRowDown, "Move down")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F7), new Command(this::insertRow, "Insert after")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.F8), new Command(this::deleteRow, "Delete row")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.CTRL_DELETE), new Command(this::deleteColumn, "Delete column")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.CTRL_X), new Command(this::cut, "Cut")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.CTRL_C), new Command(this::copy, "Copy")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.CTRL_V), new Command(this::paste, "Paste")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.DELETE), new Command(this::deleteCellValue, "Delete")));

        return c;
    }

    @Override
    protected void readUserInput() {
        try {
            RawConsoleInput.resetConsoleMode();
            String userInput = System.console().readLine();
            if (!userInput.isEmpty()) {
                getTable().setCellValue(userInput, getFocus().getRow(), getFocus().getCol());
            }
            setMode(Mode.SELECT);
        } catch (IOException e) {
            // ignored
        }
    }

    private void editCell() {
        if (getFocus().isValid()) {
            setMode(Mode.EDIT);
        }
    }

    private void selectDate() {
        if (getFocus().isValid()) {
            ConsoleDateSelector dateSelector = ConsoleTableFactory.createDateConsoleSelector(
                    Utils.firstDayOfCurrentMonth(),
                    getConsoleLines(),
                    getConsoleColumns(),
                    date -> getTable().setCellValue(Utils.printDate(date), getFocus().getRow(), getFocus().getCol())
            );
            dateSelector.show();
        }
    }

    private void saveTable() {
        Utils.writeFile(file, TablePrinter.toCsv(getTable()) + Const.NEW_LINE);
        setLogMessage(Utils.colorText("Saved in [" + file.toString() + "]", ConsoleColor.CYAN));
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
            getFocus().setCol(col);
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
