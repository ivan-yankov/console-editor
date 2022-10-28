package console.date;

import console.Const;
import console.Key;
import console.Keys;
import console.editor.Command;
import console.editor.ConsoleTableViewer;
import console.editor.Mode;
import console.editor.Table;
import console.util.DataFactory;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class DateConsoleSelector extends ConsoleTableViewer<LocalDate> {
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
    protected List<Command> addCommands() {
        return List.of(
                new Command(Mode.SELECT, Keys.ESC, this::onEsc, "Close"),
                new Command(Mode.SELECT, Keys.ENTER, this::onEnter, "Accept"),
                new Command(Mode.SELECT, new Key("-"), this::previousMonth, "Prev month"),
                new Command(Mode.SELECT, new Key("+"), this::nextMonth, "Next month")
        );
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
