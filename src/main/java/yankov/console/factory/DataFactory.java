package yankov.console.factory;

import yankov.console.Const;
import yankov.console.table.Cell;
import yankov.jutils.functional.ImmutableList;
import yankov.jutils.functional.tuples.Tuple;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DataFactory {
    public static ImmutableList<Cell<String>> createHeaderForDateConsoleSelector() {
        return ImmutableList.from(DayOfWeek.values())
                .stream()
                .map(x -> x.getDisplayName(TextStyle.SHORT, Locale.US))
                .map(x -> new Cell<>(x, false, y -> y))
                .toList();
    }

    public static ImmutableList<ImmutableList<Cell<LocalDate>>> createDataForDateConsoleSelector(LocalDate firstDayOfMonth) {
        int numberOfDaysInWeek = DayOfWeek.values().length;

        LocalDate lastDayOfMonth = firstDayOfMonth.plusDays(firstDayOfMonth.lengthOfMonth() - 1);

        int frontAlign = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        int endAlign = numberOfDaysInWeek - lastDayOfMonth.getDayOfWeek().getValue();

        List<Tuple<LocalDate, Boolean>> monthDays = new ArrayList<>();
        for (int i = frontAlign; i > 0; i--) {
            monthDays.add(new Tuple<>(firstDayOfMonth.minusDays(i), false));
        }
        for (int i = 0; i < firstDayOfMonth.lengthOfMonth(); i++) {
            monthDays.add(new Tuple<>(firstDayOfMonth.plusDays(i), true));
        }
        for (int i = 0; i < endAlign; i++) {
            monthDays.add(new Tuple<>(lastDayOfMonth.plusDays(i + 1), false));
        }

        return ImmutableList.of(monthDays)
                .stream()
                .map(x -> CellFactory.createDateCell(x._1(), x._2()))
                .toList()
                .sliding(numberOfDaysInWeek);
    }
}
