package yankov.console.factory;

import yankov.console.table.Cell;
import yankov.console.table.Table;
import yankov.console.table.viewer.TableColumnsMismatchException;

import java.time.LocalDate;
import java.util.List;

public class TableFactory {
    public static Table<String> createStringTable(List<Cell<String>> header,
                                                  List<List<Cell<String>>> data) {
        return Table.from(header, data, CellFactory::createEmptyStringCell)
                .getRight()
                .orElseThrow(TableColumnsMismatchException::new);
    }

    public static Table<LocalDate> createDateTable(List<Cell<String>> header,
                                                   List<List<Cell<LocalDate>>> data) {
        return Table.from(header, data, () -> null)
                .getRight()
                .orElseThrow(TableColumnsMismatchException::new);
    }
}
