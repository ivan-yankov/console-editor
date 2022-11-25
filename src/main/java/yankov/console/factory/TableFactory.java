package yankov.console.factory;

import yankov.console.Const;
import yankov.console.table.Cell;
import yankov.console.table.Table;
import yankov.console.table.TableColumnsMismatchException;
import yankov.jutils.functional.ImmutableList;

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