package console.date;

import console.Const;
import console.Key;
import console.Keys;
import console.editor.*;
import console.util.DataFactory;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DateConsoleSelector extends ConsoleTable<LocalDate> {
    private final Consumer<LocalDate> select;
    private LocalDate firstDayOfMonth;

    public DateConsoleSelector(Table<LocalDate> table, int consoleLines, int consoleColumns, LocalDate firstDayOfMonth, Consumer<LocalDate> select) {
        super(table, consoleLines, consoleColumns);
        this.firstDayOfMonth = firstDayOfMonth;
        this.select = select;
        setTitle(createTitle());
        setFocusOnToday();
    }

    @Override
    protected Map<Mode, Stream<Command>> additionalCommands() {
        Stream<Command> commands = Stream.of(
                new Command(Keys.ESC, this::onEsc, "Close"),
                new Command(Keys.ENTER, this::onEnter, "Accept"),
                new Command(new Key("-"), this::previousMonth, "Prev month"),
                new Command(new Key("+"), this::nextMonth, "Next month")
        );

        Map<Mode, Stream<Command>> result = new HashMap<>();
        result.put(Mode.SELECT, commands);
        return result;
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
