package console.util;

import console.date.ConsoleDateSelector;
import console.menu.ConsoleMenu;
import console.model.Command;
import console.model.Pair;
import console.table.Table;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConsoleTableFactory {
    public static ConsoleDateSelector createDateConsoleSelector(LocalDate firstDayOfMonth, int consoleLines, int consoleColumns, Consumer<LocalDate> select) {
        return new ConsoleDateSelector(
                TableFactory.createDateTable(
                        DataFactory.createHeaderForDateConsoleSelector(),
                        DataFactory.createDataForDateConsoleSelector(firstDayOfMonth)
                ),
                consoleLines,
                consoleColumns,
                firstDayOfMonth,
                select
        );
    }

    public static ConsoleMenu createConsoleMenu(List<Pair<String, List<Command>>> commands, int consoleLines, int consoleColumns) {
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
        return new ConsoleMenu(
                new Table<>(
                        header,
                        Utils.asList(tableData),
                        Command::getLabel,
                        Utils::doNothing,
                        false
                ),
                consoleLines,
                consoleColumns
        );
    }
}
