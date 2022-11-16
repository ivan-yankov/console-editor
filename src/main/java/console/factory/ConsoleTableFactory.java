package console.factory;

import console.table.*;
import console.model.Command;
import console.model.Pair;
import console.operations.ConsoleOperations;
import console.operations.FileOperations;
import console.Utils;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConsoleTableFactory {
    public static ConsoleDateSelector createDateConsoleSelector(
            LocalDate firstDayOfMonth,
            int consoleLines,
            int consoleColumns,
            Consumer<LocalDate> select,
            ConsoleOperations consoleOperations) {
        return new ConsoleDateSelector(
                TableFactory.createDateTable(
                        DataFactory.createHeaderForDateConsoleSelector(),
                        DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)
                ),
                consoleLines,
                consoleColumns,
                firstDayOfMonth,
                LocalDate::now,
                select,
                consoleOperations
        );
    }

    public static ConsoleTableEditor createConsoleTableEditor(
            Path csvFile,
            int lines,
            int columns,
            String title,
            ConsoleOperations consoleOperations,
            FileOperations fileOperations) {
        String csv = fileOperations.readFile(csvFile).orElse("");
        Table<String> table = TableParser.fromCsv(csv);
        ConsoleTableEditor editor = new ConsoleTableEditor(table, csvFile, lines, columns, consoleOperations, fileOperations);
        editor.setTitle(title);
        return editor;
    }

    public static ConsoleMenu createConsoleMenu(
            List<Pair<String, List<Command>>> commands,
            int consoleLines,
            int consoleColumns,
            String title,
            ConsoleOperations consoleOperations) {
        List<Cell<String>> header = commands.stream().map(x -> new Cell<>(x.getKey(), false, y -> y)).collect(Collectors.toList());
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
                Utils.asList(tableData)
                        .stream()
                        .map(x -> x.stream().map(y -> new Cell<>(y, false, Command::getLabel)).collect(Collectors.toList()))
                        .collect(Collectors.toList()),
                () -> new Cell<>(Utils.doNothing(), false, Command::getLabel)
        );

        ConsoleMenu menu = new ConsoleMenu(table, consoleLines, consoleColumns, consoleOperations);
        menu.setTitle(title);
        return menu;
    }
}
