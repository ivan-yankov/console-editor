package console.factory;

import console.Const;
import console.table.Cell;
import yankov.jutils.functional.ImmutableList;

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

        List<LocalDate> monthDays = new ArrayList<>();
        for (int i = 0; i < frontAlign; i++) {
            monthDays.add(Const.INVALID_DATE);
        }
        for (int i = 0; i < firstDayOfMonth.lengthOfMonth(); i++) {
            monthDays.add(firstDayOfMonth.plusDays(i));
        }
        for (int i = 0; i < endAlign; i++) {
            monthDays.add(Const.INVALID_DATE);
        }

        return ImmutableList.of(monthDays).stream().map(CellFactory::createDateCell).toList().sliding(numberOfDaysInWeek);
    }
}
