package console.util;

import console.date.DateConsoleSelector;
import console.editor.Action;
import console.editor.Table;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TableFactory {
    public static Table<String> createStringTable(List<String> header, List<List<String>> data) {
        return new Table<>(header, data, x -> x, () -> "");
    }

    public static Table<String> createEmptyStringTable() {
        return new Table<>(new ArrayList<>(), new ArrayList<>(), x -> x, () -> "");
    }

    public static Table<LocalDate> createDateTable(List<String> header, List<List<LocalDate>> data) {
        return new Table<>(
                header,
                data,
                x -> {
                    if (x.equals(LocalDate.MIN)) {
                        return "";
                    } else {
                        return x.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    }
                },
                () -> LocalDate.MIN
        );
    }

    public static DateConsoleSelector createDateConsoleSelector(Consumer<LocalDate> ok, Action cancel) {
        int numberOfDaysInWeek = DayOfWeek.values().length;

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.minusDays(today.getDayOfMonth() + 1);
        LocalDate lastDayOfMonth = today.plusDays(today.lengthOfMonth() - today.getDayOfMonth());

        int frontAlign = firstDayOfMonth.getDayOfWeek().getValue() - 1;
        int endAlign = numberOfDaysInWeek - lastDayOfMonth.getDayOfWeek().getValue();

        List<LocalDate> monthDays = new ArrayList<>();
        for (int i = 0; i < frontAlign; i++) {
            monthDays.add(LocalDate.MIN);
        }
        for (int i = 0; i < today.lengthOfMonth(); i++) {
            monthDays.add(firstDayOfMonth.plusDays(i));
        }
        for (int i = 0; i < endAlign; i++) {
            monthDays.add(LocalDate.MIN);
        }

        List<String> header = Arrays.stream(DayOfWeek.values())
                .map(x -> x.getDisplayName(TextStyle.SHORT, Locale.US))
                .collect(Collectors.toList());

        List<List<LocalDate>> data = new ArrayList<>();
        int numberOfWeeks = monthDays.size() / numberOfDaysInWeek;
        for (int i = 0; i < numberOfWeeks; i++) {
            data.add(monthDays.stream().skip(i).limit(numberOfWeeks).collect(Collectors.toList()));
        }

        return new DateConsoleSelector(createDateTable(header, data), ok, cancel);
    }
}
