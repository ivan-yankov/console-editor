package console.table;

import console.ConsoleColor;
import console.Const;
import console.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TablePrinter {
    private static final String FOCUS_COLOR = ConsoleColor.BLACK + ConsoleColor.CYAN_B;
    private static final String INDEXES_COLOR = ConsoleColor.BLACK + ConsoleColor.DARK_GRAY_B;
    private static final String HEADER_COLOR = ConsoleColor.BLACK + ConsoleColor.YELLOW_B;

    public static <T> String toCsv(Table<T> table) {
        String header = table
                .getHeader()
                .stream()
                .map(Cell::toCsvString)
                .collect(Collectors.joining(Const.COMMA));

        String data = table
                .getData()
                .stream()
                .map(row -> row
                        .stream()
                        .map(Cell::toCsvString)
                        .collect(Collectors.joining(Const.COMMA))
                ).collect(Collectors.joining(Const.NEW_LINE));
        return header + Const.NEW_LINE + data;
    }

    public static <T> List<String> headerToConsole(Table<T> table, boolean withRowIndexes) {
        if (!table.isValid() || table.getHeader().isEmpty()) return new ArrayList<>();

        List<String> result = new ArrayList<>();
        List<String> header = new ArrayList<>();
        for (int i = 0; i < table.getHeader().size(); i++) {
            String value = printConsoleCellValue(
                    table.getHeader().get(i).toConsoleString(),
                    table.fieldSize(i),
                    false
            );
            header.add(value);
        }
        String headerStr = String.join(Const.COL_SEPARATOR, header);
        if (withRowIndexes) {
            headerStr = index(table.getRowCount(), 0) + headerStr;
        }
        result.add(Utils.colorText(headerStr, HEADER_COLOR));

        return result;
    }

    public static <T> Optional<List<String>> dataToConsole(Table<T> table, Focus focus, boolean withRowIndexes) {
        if (!table.isValid()) return Optional.empty();

        List<String> result = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < table.getColCount(); j++) {
                String value = printConsoleCellValue(
                        table.getCell(i, j).toConsoleString(),
                        table.fieldSize(j),
                        focus.isValid() && i == focus.getRow() && j == focus.getCol()
                );
                row.add(value);
            }
            String rowStr = String.join(Const.COL_SEPARATOR, row);
            if (withRowIndexes) {
                rowStr = index(table.getRowCount(), i + 1) + rowStr;
            }
            result.add(rowStr);
        }

        return Optional.of(result);
    }

    private static String index(int rowCount, int index) {
        int n = Integer.toString(rowCount).length();
        String f = "%" + n + "s";
        if (index > 0) {
            return Utils.colorText(String.format(f, index), INDEXES_COLOR) +
                    Const.COL_SEPARATOR;
        } else {
            return Utils.generateString(n, ' ') + Const.COL_SEPARATOR;
        }
    }

    private static String printConsoleCellValue(String value, int fieldSize, boolean focused) {
        String text = Utils.containsLetter(value)
                ? value + Utils.generateString(fieldSize - value.length(), ' ')
                : String.format("%" + fieldSize + "s", value);
        return focused ? FOCUS_COLOR + text + ConsoleColor.RESET : text;
    }
}
