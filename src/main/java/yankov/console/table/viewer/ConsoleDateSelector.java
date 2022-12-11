package yankov.console.table.viewer;

import yankov.console.Const;
import yankov.console.factory.DataFactory;
import yankov.console.operations.ConsoleOperations;
import yankov.console.table.Table;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConsoleDateSelector extends ConsoleTableViewer<LocalDate> {
    private final Consumer<LocalDate> select;
    private LocalDate firstDayOfMonth;

    public ConsoleDateSelector(
            Table<LocalDate> table,
            int consoleLines,
            int consoleColumns,
            LocalDate firstDayOfMonth,
            Supplier<LocalDate> todayDate,
            Consumer<LocalDate> select,
            ConsoleOperations consoleOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
        this.firstDayOfMonth = firstDayOfMonth;
        this.select = select;
        setTitle(createTitle());
        selectDate(todayDate.get());
    }

    @Override
    protected void onEnter() {
        LocalDate value = getTable().getCell(getFocus().getRow(), getFocus().getCol()).getValue();
        if (!value.equals(Const.INVALID_DATE)) {
            select.accept(value);
            setMode(Mode.EXIT);
        }
    }

    @Override
    protected boolean showFooter() {
        return false;
    }

    @Override
    protected void onPageUp() {
        firstDayOfMonth = firstDayOfMonth.minusMonths(1);
        setTable(getTable().withData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)).getRight().orElse(getTable()));
        setTitle(createTitle());
        resetFocus();
    }

    @Override
    protected void onPageDown() {
        firstDayOfMonth = firstDayOfMonth.plusMonths(1);
        setTable(getTable().withData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)).getRight().orElse(getTable()));
        setTitle(createTitle());
        resetFocus();
    }

    @Override
    protected String getPageUpDescription() {
        return "Prev month";
    }

    @Override
    protected String getPageDownDescription() {
        return "Next month";
    }

    private String createTitle() {
        return firstDayOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + ", " + firstDayOfMonth.getYear();
    }

    private void selectDate(LocalDate date) {
        for (int i = 0; i < getTable().getRowCount(); i++) {
            for (int j = 0; j < getTable().getColCount(); j++) {
                if (getTable().getCell(i, j).getValue().equals(date)) {
                    setFocus(new Focus(i, j));
                }
            }
        }
    }
}
