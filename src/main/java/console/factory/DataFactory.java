package console.factory;

import console.Const;
import console.Utils;
import console.table.Cell;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class DataFactory {
    public static List<Cell<String>> createHeaderForDateConsoleSelector() {
        return Arrays.stream(DayOfWeek.values())
                .map(x -> x.getDisplayName(TextStyle.SHORT, Locale.US))
                .map(x -> new Cell<>(x, false, y -> y))
                .collect(Collectors.toList());
    }

    public static List<List<Cell<LocalDate>>> createDataForDateConsoleSelector(LocalDate firstDayOfMonth) {
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

        return Utils.sliding(monthDays.stream().map(x -> new Cell<>(x, false, Utils::printDate)).collect(Collectors.toList()), numberOfDaysInWeek);
    }
}
