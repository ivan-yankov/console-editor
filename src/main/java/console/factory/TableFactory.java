package console.factory;

import console.Const;
import console.table.Cell;
import console.table.Table;
import console.table.TableColumnsMismatchException;
import yankov.functional.ImmutableList;

import java.time.LocalDate;

public class TableFactory {
    public static Table<String> createStringTable(ImmutableList<Cell<String>> header,
                                                  ImmutableList<ImmutableList<Cell<String>>> data) {
        return Table.from(header, data, CellFactory::createEmptyStringCell)
                .getRight()
                .orElseThrow(TableColumnsMismatchException::new);
    }

    public static Table<LocalDate> createDateTable(ImmutableList<Cell<String>> header,
                                                   ImmutableList<ImmutableList<Cell<LocalDate>>> data) {
        return Table.from(header, data, () -> CellFactory.createDateCell(Const.INVALID_DATE))
                .getRight()
                .orElseThrow(TableColumnsMismatchException::new);
    }
}
