package yankov.console;

import yankov.console.factory.ConsoleTableFactory;
import yankov.console.operations.ConsoleOperations;
import yankov.console.operations.FileOperations;
import yankov.console.table.ConsoleTableEditor;
import yankov.console.table.ConsoleTableViewer;
import yankov.jutils.functional.Either;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ConsoleTableEditorMain {
    private static final List<ProgramArgument> arguments = List.of(
            new ProgramArgument("console-lines", "Number of lines of the current console"),
            new ProgramArgument("console-columns", "Number of columns of the current console"),
            new ProgramArgument("input-file", "CSV file to be edited")
    );

    public static void main(String[] args) {
        ConsoleOperations consoleOperations = new ConsoleOperations();

        if (args.length < arguments.size()) {
            consoleOperations.writeln("Missing required argument.");
            arguments
                    .stream()
                    .skip(args.length)
                    .map(x -> Const.TAB + x.getName() + ": " + x.getDescription())
                    .forEach(consoleOperations::writeln);
            return;
        }

        int lines = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        Path csvFile = Paths.get(args[2]);

        Either<String, ConsoleTableEditor> editor = ConsoleTableFactory.createConsoleTableEditor(
                csvFile,
                lines,
                columns,
                csvFile.toString(),
                consoleOperations,
                new FileOperations(consoleOperations)
        );
        editor.fold(
                ConsoleTableViewer::show,
                consoleOperations::writeError
        );

        consoleOperations.clearConsole();

        System.exit(0);
    }
}
