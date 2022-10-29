package console.util;

import console.date.ConsoleDateSelector;
import console.menu.ConsoleMenu;
import console.model.Command;
import console.model.Pair;
import console.table.ConsoleTableEditor;
import console.table.Table;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConsoleTableFactory {
    public static ConsoleDateSelector createDateConsoleSelector(int consoleLines, int consoleColumns, Consumer<LocalDate> select, Supplier<String> consoleReadLine) {
        return createDateConsoleSelector(Utils.firstDayOfCurrentMonth(), consoleLines, consoleColumns, select, consoleReadLine);
    }

    public static ConsoleDateSelector createDateConsoleSelector(LocalDate firstDayOfMonth, int consoleLines, int consoleColumns, Consumer<LocalDate> select, Supplier<String> consoleReadLine) {
        return new ConsoleDateSelector(
                TableFactory.createDateTable(
                        DataFactory.createHeaderForDateConsoleSelector(),
                        DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)
                ),
                consoleLines,
                consoleColumns,
                firstDayOfMonth,
                select,
                consoleReadLine
        );
    }

    public static ConsoleTableEditor createConsoleTableEditor(Path csvFile, int lines, int columns, String title, Supplier<String> consoleReadLine) {
        String csv = Utils.readFile(csvFile).orElse("");
        Table<String> table = TableParser.fromCsv(csv);
        ConsoleTableEditor editor = new ConsoleTableEditor(table, csvFile, lines, columns, consoleReadLine);
        editor.setTitle(title);
        return editor;
    }

    public static ConsoleMenu createConsoleMenu(List<Pair<String, List<Command>>> commands, int consoleLines, int consoleColumns, String title, Supplier<String> consoleReadLine) {
        List<String> header = commands.stream().map(Pair::getKey).collect(Collectors.toList());
        int numberOfRows = commands.stream().map(x -> x.getValue().size()).max(Comparator.naturalOrder()).orElse(0);
        int numberOfColumns = header.size();
        Command[][] tableData = new Command[numberOfRows][numberOfColumns];
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                if (i < commands.get(j).getValue().size()) {
                    tableData[i][j] = commands.get(j).getValue().get(i);
                } else {
                    tableData[i][j] = Utils.doNothing();
                }
            }
        }

        Table<Command> table = new Table<>(
                header,
                Utils.asList(tableData),
                Command::getLabel,
                Utils::doNothing,
                false
        );

        ConsoleMenu menu = new ConsoleMenu(table, consoleLines, consoleColumns, consoleReadLine);
        menu.setTitle(title);
        return menu;
    }
}
