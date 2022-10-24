package console.util;

import console.ConsoleColors;
import console.Const;
import console.editor.Focus;
import console.editor.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TablePrinter {
    private static final String FOCUS_COLOR = ConsoleColors.CYAN_B;

    public static String toCsv(Table table) {
        return table.dataStream().map(row -> String.join(Const.COMMA, row)).collect(Collectors.joining(Const.NEW_LINE));
    }

    public static Optional<String> toConsole(Table table, Focus focus) {
        if (!table.isValid()) return Optional.empty();

        List<String> headerSeparatorItems = new ArrayList<>();
        for (int i = 0; i < table.getHeader().size(); i++) {
            headerSeparatorItems.add(Utils.generateString(table.fieldSize(i), Const.EQUALS_SYMBOL));
        }
        String headerSeparator = String.join(Const.COL_SEPARATOR, headerSeparatorItems);

        List<String> result = new ArrayList<>();
        result.add(headerSeparator);
        for (int i = 0; i < table.getRowCount(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < table.getColCount(); j++) {
                row.add(printCell(table, i, j, focus));
            }
            result.add(String.join(Const.COL_SEPARATOR, row));
            if (i == 0) {
                result.add(headerSeparator);
            }
        }

        return Optional.of(String.join(Const.NEW_LINE, result));
    }

    private static String printCell(Table table, Integer row, Integer col, Focus focus) {
        String cellValue = table.getCellValue(row, col);
        Integer fs = table.fieldSize(col);
        String text = Utils.containsLetter(cellValue)
                ? cellValue + Utils.generateString(fs - cellValue.length(), ' ')
                : String.format("%" + fs + "s", cellValue);
        boolean focused = focus.isValid() && row.equals(focus.getRow()) && col.equals(focus.getCol());
        return focused ? FOCUS_COLOR + text + ConsoleColors.RESET : text;
    }
}
