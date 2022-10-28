package console.date;

import console.Const;
import console.Key;
import console.Keys;
import console.model.Pair;
import console.model.Command;
import console.table.ConsoleTableViewer;
import console.table.Mode;
import console.table.Table;
import console.util.DataFactory;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConsoleDateSelector extends ConsoleTableViewer<LocalDate> {
    private final Consumer<LocalDate> select;
    private LocalDate firstDayOfMonth;

    public ConsoleDateSelector(Table<LocalDate> table, int consoleLines, int consoleColumns, LocalDate firstDayOfMonth, Consumer<LocalDate> select, Supplier<String> consoleReadLine) {
        super(table, consoleLines, consoleColumns, consoleReadLine);
        this.firstDayOfMonth = firstDayOfMonth;
        this.select = select;
        setTitle(createTitle());
        setFocusOnToday();
    }

    @Override
    protected List<Pair<Key, Command>> addCommands() {
        List<Pair<Key, Command>> c = new ArrayList<>();

        c.add(new Pair<>(Keys.ESC, new Command(this::onEsc, "Close")));
        c.add(new Pair<>(Keys.ENTER, new Command(this::onEnter, "Accept")));
        c.add(new Pair<>(new Key("-"), new Command(this::previousMonth, "Prev month")));
        c.add(new Pair<>(new Key("+"), new Command(this::nextMonth, "Next month")));

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
