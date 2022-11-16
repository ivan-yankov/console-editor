package console.factory;

import console.Const;
import console.table.Cell;
import console.table.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TableFactory {
    public static Table<String> createStringTable(List<Cell<String>> header, List<List<Cell<String>>> data) {
        return new Table<>(header, data, CellFactory::createEmptyStringCell);
    }

    public static Table<String> createEmptyStringTable() {
        return new Table<>(new ArrayList<>(), new ArrayList<>(), CellFactory::createEmptyStringCell);
    }

    public static Table<LocalDate> createDateTable(List<Cell<String>> header, List<List<Cell<LocalDate>>> data) {
        return new Table<>(
                header,
                data,
                () -> CellFactory.createDateCell(Const.INVALID_DATE)
        );
    }
}
