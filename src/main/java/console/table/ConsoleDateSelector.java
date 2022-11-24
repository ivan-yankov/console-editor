package console.table;

import console.Const;
import console.factory.DataFactory;
import console.operations.ConsoleOperations;

import java.time.LocalDate;
import java.time.format.TextStyle;
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
    protected void onPageUp() {
        firstDayOfMonth = firstDayOfMonth.minusMonths(1);
        setTable(getTable().withData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)).getRight().orElse(getTable()));
        setTitle(createTitle());
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    @Override
    protected void onPageDown() {
        firstDayOfMonth = firstDayOfMonth.plusMonths(1);
        setTable(getTable().withData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)).getRight().orElse(getTable()));
        setTitle(createTitle());
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    @Override
    protected String getPageUpDescription() {
        return "Prev month";
    }

    @Override
    protected String getPageDownDescription() {
        return "Next month";
    }

    @Override
    protected String getEnterDescription() {
        return "Accept";
    }

    private String createTitle() {
        return firstDayOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + ", " + firstDayOfMonth.getYear();
    }

    private void selectDate(LocalDate date) {
        for (int i = 0; i < getTable().getRowCount(); i++) {
            for (int j = 0; j < getTable().getColCount(); j++) {
                if (getTable().getCell(i, j).getValue().equals(date)) {
                    getFocus().setRow(i);
                    getFocus().setCol(j);
                }
            }
        }
    }
}
