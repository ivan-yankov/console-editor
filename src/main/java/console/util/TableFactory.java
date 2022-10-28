package console.util;

import console.Const;
import console.table.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TableFactory {
    public static Table<String> createStringTable(List<String> header, List<List<String>> data, boolean quotesWrapped) {
        return new Table<>(header, data, x -> x, () -> "", quotesWrapped);
    }

    public static Table<String> createEmptyStringTable() {
        return new Table<>(new ArrayList<>(), new ArrayList<>(), x -> x, () -> "", false);
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
                () -> Const.INVALID_DATE,
                false
        );
    }
}
