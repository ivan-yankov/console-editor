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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConsoleTableEditor extends ConsoleTableViewer<String> {
    private final Path file;
    private final FileOperations fileOperations;

    public ConsoleTableEditor(Table<String> table,
                              Path file,
                              int consoleLines,
                              int consoleColumns,
                              ConsoleOperations consoleOperations,
                              FileOperations fileOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
        this.file = file;
        this.fileOperations = fileOperations;
    }

    @Override
    protected List<Command> additionalCommands() {
        return List.of(
                new Command("edit", x -> editCell(), "Edit", Key.F2),
                new Command("hedit", x -> editHeader(), "Edit", Key.F3),
                new Command("date", x -> selectDate(), "Select date", Key.CTRL_F2),
                new Command("save", x -> saveTable(), "Save", Key.F4),
                new Command("row-up", x -> moveRowUp(), "Move up", Key.CTRL_UP),
                new Command("row-down", x -> moveRowDown(), "Move down", Key.CTRL_DOWN),
                new Command("row-insert", x -> insertRow(), "Insert after", Key.F7),
                new Command("row-del", x -> deleteRow(), "Delete row", Key.F8),
                new Command("col-left", x -> moveColumnLeft(), "Delete row", Key.CTRL_LEFT),
                new Command("col-right", x -> moveColumnRight(), "Delete row", Key.CTRL_RIGHT),
                new Command("col-insert", x -> insertColumn(), "Delete row", Key.CTRL_F7),
                new Command("col-del", x -> deleteColumn(), "Delete column", Key.CTRL_F8),
                new Command("cut", x -> cut(), "Cut", Key.CTRL_X),
                new Command("copy", x -> copy(), "Copy", Key.CTRL_C),
                new Command("paste", x -> paste(), "Paste", Key.CTRL_V),
                new Command("del", x -> deleteCellValue(), "Delete", Key.DELETE),
                new Command("auto-corr-dec-sym", this::autoCorrectDecimalSymbol, "Replace comma with dot if input is a number and if only numbers are presented in the table column")
        );
    }

    @Override
    protected boolean allowCommand() {
        return getMode() != Mode.EDIT_CELL && getMode() != Mode.EDIT_HEADER;
    }

    @Override
    protected void processCustomAction() {
        if (getMode() == Mode.EDIT_CELL) {
            processUserInput(x -> getTable().setCellValue(
                            getAutoCorrector().autoCorrectUserInput(x),
                            getFocus().getRow(),
                            getFocus().getCol()
                    )
            );
        } else if (getMode() == Mode.EDIT_HEADER) {
            processUserInput(x -> getTable().setHeaderValue(x, getFocus().getCol()));
        }
    }

    @Override
    protected String getHint() {
        if (getMode() == Mode.EDIT_CELL) {
            return "Enter to accept the input. Empty input to discard editing.";
        }
        return super.getHint();
    }

    private void processUserInput(Consumer<String> userInputProcessor) {
        getConsoleOperations().resetConsole();
        String userInput = getConsoleOperations().consoleReadLine().get();
        if (!userInput.isEmpty()) {
            userInputProcessor.accept(userInput);
        }
        setMode(Mode.KEY);
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
            setMode(Mode.EDIT_CELL);
        }
    }

    private void editHeader() {
        if (getFocus().isValid()) {
            setMode(Mode.EDIT_HEADER);
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

    private void moveColumnLeft() {
        if (getFocus().getCol() > 0) {
            getTable().swapColumns(getFocus().getCol(), getFocus().getCol() - 1);
            getFocus().setCol(getFocus().getCol() - 1);
        }
    }

    private void moveColumnRight() {
        if (getFocus().getCol() < getTable().getColCount() - 1) {
            getTable().swapColumns(getFocus().getCol(), getFocus().getCol() + 1);
            getFocus().setCol(getFocus().getCol() + 1);
        }
    }

    private void insertColumn() {
        getTable().insertEmptyColumn(getFocus().getCol() + 1);
        if (!getFocus().isValid()) {
            resetFocus();
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

    private void autoCorrectDecimalSymbol(List<String> p) {
        getSettings().setAutoCorrectDecimalSymbol(analyzeFlagParameter(p).orElse(getSettings().isAutoCorrectDecimalSymbol()));
        setLogMessage("Auto correct of decimal symbol is " + (getSettings().isAutoCorrectDecimalSymbol() ? "enabled" : "disabled"));
    }
}
