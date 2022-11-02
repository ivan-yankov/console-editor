package console.table;

import console.operations.ConsoleOperations;
import console.operations.FileOperations;

import java.nio.file.Path;

@FunctionalInterface
public interface NewConsoleTable<T, E extends ConsoleTableViewer<?>> {
    E apply(Table<T> table, int lines, int columns, Path file, FileOperations fileOperations, ConsoleOperations consoleOperations);
}
