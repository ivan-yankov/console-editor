package console.util;

import console.Const;
import console.date.DateConsoleSelector;
import console.editor.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
                    if (x.equals(Const.INVALID_DATE)) {
                        return "";
                    } else {
                        return Utils.printDayFromDate(x);
                    }
                },
                () -> Const.INVALID_DATE
        );
    }

    public static DateConsoleSelector createDateConsoleSelector(LocalDate firstDayOfMonth, int consoleLines, int consoleColumns, Consumer<LocalDate> select) {
        return new DateConsoleSelector(
                createDateTable(
                        DataFactory.createHeaderForDateConsoleSelector(),
                        DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)
                ),
                consoleLines,
                consoleColumns,
                firstDayOfMonth,
                select
        );
    }
}
