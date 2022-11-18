package console.table;

import console.*;
import console.factory.ConsoleTableFactory;
import console.model.Command;
import console.operations.ConsoleOperations;
import console.operations.FileOperations;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleTableEditor extends ConsoleTableViewer<String> {
    private final Path file;
    private final FileOperations fileOperations;

    public ConsoleTableEditor(Table<String> table, Path file, int consoleLines, int consoleColumns, ConsoleOperations consoleOperations, FileOperations fileOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
        this.file = file;
        this.fileOperations = fileOperations;
    }

    @Override
    protected List<Command> additionalCommands() {
        return List.of(
                new Command("edit", this::editCell, "Edit", Key.F2),
                new Command("date", this::selectDate, "Select date", Key.CTRL_F2),
                new Command("save", this::saveTable, "Save", Key.F3),
                new Command("row-up", this::moveRowUp, "Move up", Key.F5),
                new Command("row-down", this::moveRowDown, "Move down", Key.F6),
                new Command("row-insert", this::insertRow, "Insert after", Key.F7),
                new Command("row-del", this::deleteRow, "Delete row", Key.F8),
                new Command("col-del", this::deleteColumn, "Delete column", Key.CTRL_DELETE),
                new Command("cut", this::cut, "Cut", Key.CTRL_X),
                new Command("copy", this::copy, "Copy", Key.CTRL_C),
                new Command("paste", this::paste, "Paste", Key.CTRL_V),
                new Command("del", this::deleteCellValue, "Delete", Key.DELETE),
                new Command("auto-corr-dec", this::autoCorrectDecimalSymbol, "Replace comma with dot if input is a number and if only numbers are presented in the table column")
        );
    }

    @Override
    protected boolean allowCommand() {
        return getMode() != Mode.EDIT;
    }

    @Override
    protected void processCustomAction() {
        if (getMode() == Mode.EDIT) {
            getConsoleOperations().resetConsole();
            String userInput = getConsoleOperations().consoleReadLine().get();
            if (!userInput.isEmpty()) {
                getTable().setCellValue(
                        getAutoCorrector().autoCorrectUserInput(userInput),
                        getFocus().getRow(),
                        getFocus().getCol()
                );
            }
            setMode(Mode.KEY);
        }
    }

    @Override
    protected String getHint() {
        if (getMode() == Mode.EDIT) {
            return "Enter to accept the input. Empty input to discard editing.";
        }
        return super.getHint();
    }

    private AutoCorrector getAutoCorrector() {
        return new AutoCorrector(
                getSettings().isAutoCorrectDecimalSymbol(),
                getTable()
                        .getData()
                        .stream()
                        .map(x -> x.get(getFocus().getCol()).getValue())
                        .collect(Collectors.toList())
        );
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
                    date -> getTable().setCellValue(Utils.printDate(date), getFocus().getRow(), getFocus().getCol()),
                    getConsoleOperations()
            );
            dateSelector.show();
        }
    }

    private void saveTable() {
        fileOperations.writeFile(file, TablePrinter.toCsv(getTable()) + Const.NEW_LINE);
        setLogMessage("Saved " + file.toString());
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
        getTable().insertEmptyRow(getFocus().getRow() + 1);
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
            getTable().setEmptyCellValue(getFocus().getRow(), getFocus().getCol());
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

    private void autoCorrectDecimalSymbol() {
        getSettings().setAutoCorrectDecimalSymbol(!getSettings().isAutoCorrectDecimalSymbol());
        setLogMessage("Auto correct of decimal symbol is " + (getSettings().isAutoCorrectDecimalSymbol() ? "enabled" : "disabled"));
    }
}
