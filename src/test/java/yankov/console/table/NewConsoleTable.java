package yankov.console.table;

import yankov.console.operations.ConsoleOperations;
import yankov.console.operations.FileOperations;
import yankov.console.table.ConsoleTableViewer;
import yankov.console.table.Table;

import java.nio.file.Path;

@FunctionalInterface
public interface NewConsoleTable<T, E extends ConsoleTableViewer<?>> {
    E apply(Table<T> table, int lines, int columns, Path file, FileOperations fileOperations, ConsoleOperations consoleOperations);
}
