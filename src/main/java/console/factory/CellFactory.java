package console.factory;

import console.Utils;
import console.table.Cell;

import java.time.LocalDate;

public class CellFactory {
    public static Cell<String> createEmptyStringCell() {
        return new Cell<>("", false, x -> x);
    }

    public static Cell<LocalDate> createDateCell(LocalDate date) {
        return new Cell<>(date, false, Utils::printDayFromDate);
    }
}
