package yankov.console.table;

import yankov.console.ConsoleColor;
import yankov.console.Const;
import yankov.console.Utils;
import yankov.console.table.viewer.Focus;
import yankov.jutils.StringUtils;
import yankov.jutils.functional.ImmutableList;
import yankov.jutils.functional.RangeInt;

import java.util.ArrayList;
import java.util.List;
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

    public static <T> ImmutableList<String> headerToConsole(Table<T> table,
                                                            RangeInt visibleColumns,
                                                            int consoleColumns,
                                                            boolean withRowIndexes) {
        if (table.getHeader().isEmpty()) return ImmutableList.from();

        List<String> result = new ArrayList<>();
        List<String> header = new ArrayList<>();
        for (int i = visibleColumns.getStart(); i < visibleColumns.getEnd(); i++) {
            String value = printConsoleCellValue(
                    table.getHeader().get(i).toConsoleString(),
                    Math.min(table.fieldSize(i), consoleColumns)
            );
            header.add(value);
        }
        String headerStr = String.join(Const.COL_SEPARATOR, header);
        if (withRowIndexes) {
            headerStr = rowIndex(table.getRowCount(), 0) + headerStr;
        }
        result.add(Utils.colorText(headerStr, HEADER_COLOR));

        return ImmutableList.of(result);
    }

    public static <T> ImmutableList<String> dataToConsole(Table<T> table,
                                                          Focus focus,
                                                          RangeInt visibleRows,
                                                          RangeInt visibleColumns,
                                                          int consoleColumns,
                                                          boolean withRowIndexes) {
        List<String> result = new ArrayList<>();
        for (int i = visibleRows.getStart(); i < visibleRows.getEnd(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = visibleColumns.getStart(); j < visibleColumns.getEnd(); j++) {
                String value = printConsoleCellValue(
                        table.getCell(i, j).toConsoleString(),
                        Math.min(table.fieldSize(j), consoleColumns)
                );
                boolean focused = focus.isValid() && i == focus.getRow() && j == focus.getCol();
                String coloredValue = focused
                        ? Utils.colorText(value, FOCUS_COLOR)
                        : Utils.colorText(value, table.getCell(i, j).getColor());
                row.add(coloredValue);
            }
            String rowStr = String.join(Const.COL_SEPARATOR, row);
            if (withRowIndexes) {
                rowStr = rowIndex(table.getRowCount(), i + 1) + rowStr;
            }
            result.add(rowStr);
        }

        return ImmutableList.of(result);
    }

    private static String rowIndex(int rowCount, int index) {
        int n = Integer.toString(rowCount).length();
        String f = "%" + n + "s";
        if (index > 0) {
            return Utils.colorText(String.format(f, index), INDEXES_COLOR) +
                    Const.COL_SEPARATOR;
        } else {
            return StringUtils.fill(n, ' ') + Const.COL_SEPARATOR;
        }
    }

    private static String printConsoleCellValue(String s, int fieldSize) {
        String value = trimCellValue(s, fieldSize);
        return StringUtils.containsLetter(value)
                ? value + StringUtils.fill(fieldSize - value.length(), ' ')
                : String.format("%" + fieldSize + "s", value);
    }

    private static String trimCellValue(String s, int size) {
        return s.length() > size ? s.substring(0, size - 1) + "~" : s;
    }
}
