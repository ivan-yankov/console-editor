package console.factory;

import console.Utils;
import console.table.Cell;

import java.time.LocalDate;

public class CellFactory {
    public static Cell<String> createStringCell(String value) {
        return new Cell<>(value, false, x -> x);
    }

    public static Cell<String> createEmptyStringCell() {
        return createStringCell("");
    }

    public static Cell<LocalDate> createDateCell(LocalDate date) {
        return new Cell<>(date, false, Utils::printDayFromDate);
    }
}
