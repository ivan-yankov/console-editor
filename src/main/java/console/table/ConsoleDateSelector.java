package console.table;

import console.Const;
import console.Key;
import console.model.Command;
import console.model.Pair;
import console.operations.ConsoleOperations;
import console.factory.DataFactory;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class ConsoleDateSelector extends ConsoleTableViewer<LocalDate> {
    private final Consumer<LocalDate> select;
    private LocalDate firstDayOfMonth;

    public ConsoleDateSelector(Table<LocalDate> table, int consoleLines, int consoleColumns, LocalDate firstDayOfMonth, Consumer<LocalDate> select, ConsoleOperations consoleOperations) {
        super(table, consoleLines, consoleColumns, consoleOperations);
        this.firstDayOfMonth = firstDayOfMonth;
        this.select = select;
        setTitle(createTitle());
        setFocusOnToday();
    }

    @Override
    protected List<Pair<Key, Command>> addCommands() {
        List<Pair<Key, Command>> c = new ArrayList<>();

        c.add(new Pair<>(Key.ESC, new Command(this::onEsc, "Close")));
        c.add(new Pair<>(Key.ENTER, new Command(this::onEnter, "Accept")));
        c.add(new Pair<>(Key.MINUS, new Command(this::previousMonth, "Prev month")));
        c.add(new Pair<>(Key.PLUS, new Command(this::nextMonth, "Next month")));

        return c;
    }

    private void onEsc() {
        setMode(Mode.CLOSE);
    }

    private void onEnter() {
        LocalDate value = getTable().getCellValue(getFocus().getRow(), getFocus().getCol());
        if (!value.equals(Const.INVALID_DATE)) {
            select.accept(value);
            setMode(Mode.CLOSE);
        }
    }

    private void previousMonth() {
        firstDayOfMonth = firstDayOfMonth.minusMonths(1);
        getTable().updateData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth));
        setTitle(createTitle());
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    private void nextMonth() {
        firstDayOfMonth = firstDayOfMonth.plusMonths(1);
        getTable().updateData(DataFactory.createDataForDateConsoleSelector(firstDayOfMonth));
        setTitle(createTitle());
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    private String createTitle() {
        return firstDayOfMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US) + ", " + firstDayOfMonth.getYear();
    }

    private void setFocusOnToday() {
        LocalDate today = LocalDate.now();
        for (int i = 0; i < getTable().getRowCount(); i++) {
            for (int j = 0; j < getTable().getColCount(); j++) {
                if (getTable().getCellValue(i, j).equals(today)) {
                    getFocus().setRow(i);
                    getFocus().setCol(j);
                }
            }
        }
    }
}