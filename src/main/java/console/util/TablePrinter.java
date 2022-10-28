package console.util;

import console.ConsoleColor;
import console.Const;
import console.table.Focus;
import console.table.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TablePrinter {
    private static final String FOCUS_COLOR = ConsoleColor.BLACK + ConsoleColor.DARK_CYAN_B;

    public static <T> String toCsv(Table<T> table) {
        String header = table
                .getHeader()
                .stream()
                .map(x -> printCsvCellValue(table, x))
                .collect(Collectors.joining(Const.COMMA));

        String data = table
                .getDataStream()
                .map(row -> row
                        .stream()
                        .map(x -> printCsvCellValue(table, x))
                        .collect(Collectors.joining(Const.COMMA))
                ).collect(Collectors.joining(Const.NEW_LINE));
        return header + Const.NEW_LINE + data;
    }

    public static <T> List<String> headerToConsole(Table<T> table) {
        if (!table.isValid()) return new ArrayList<>();

        List<String> headerSeparatorItems = new ArrayList<>();
        for (int i = 0; i < table.getHeader().size(); i++) {
            headerSeparatorItems.add(Utils.generateString(table.fieldSize(i), Const.EQUALS_SYMBOL));
        }
        String headerSeparator = String.join(Const.COL_SEPARATOR, headerSeparatorItems);

        List<String> result = new ArrayList<>();
        result.add(headerSeparator);
        List<String> header = new ArrayList<>();
        for (int i = 0; i < table.getHeader().size(); i++) {
            String value = printConsoleCellValue(
                    table.getHeader().get(i),
                    table.fieldSize(i),
                    false
            );
            header.add(value);
        }
        result.add(String.join(Const.COL_SEPARATOR, header));
        result.add(headerSeparator);

        return result;
    }

    public static <T> Optional<List<String>> dataToConsole(Table<T> table, Focus focus) {
        if (!table.isValid()) return Optional.empty();

        List<String> result = new ArrayList<>();
        for (int i = 0; i < table.getRowCount(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < table.getColCount(); j++) {
                String value = printConsoleCellValue(
                        table.getPrintValue().apply(table.getCellValue(i, j)),
                        table.fieldSize(j),
                        focus.isValid() && i == focus.getRow() && j == focus.getCol()
                );
                row.add(value);
            }
            result.add(String.join(Const.COL_SEPARATOR, row));
        }

        return Optional.of(result);
    }

    private static String printConsoleCellValue(String value, int fieldSize, boolean focused) {
        String text = Utils.containsLetter(value)
                ? value + Utils.generateString(fieldSize - value.length(), ' ')
                : String.format("%" + fieldSize + "s", value);
        return focused ? FOCUS_COLOR + text + ConsoleColor.RESET : text;
    }

    private static <T> String printCsvCellValue(Table<T> table, T value) {
        String s = table.getPrintValue().apply(value);
        return printCsvCellValue(table, s);
    }

    private static <T> String printCsvCellValue(Table<T> table, String value) {
        if (table.isQuotesWrapped()) {
            return Const.QUOTES + value + Const.QUOTES;
        } else {
            return value;
        }
    }
}
