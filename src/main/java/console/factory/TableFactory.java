package console.factory;

import console.Const;
import console.table.Cell;
import console.table.Table;
import console.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TableFactory {
    public static Table<String> createStringTable(List<Cell<String>> header, List<List<Cell<String>>> data) {
        return new Table<>(header, data, emptyStringCell());
    }

    public static Table<String> createEmptyStringTable() {
        return new Table<>(new ArrayList<>(), new ArrayList<>(), emptyStringCell());
    }

    public static Table<LocalDate> createDateTable(List<Cell<String>> header, List<List<Cell<LocalDate>>> data) {
        return new Table<>(
                header,
                data,
                () -> new Cell<>(Const.INVALID_DATE, false, Utils::printDate)
        );
    }

    public static Supplier<Cell<String>> emptyStringCell() {
        return () -> new Cell<>("", false, x -> x);
    }
}
