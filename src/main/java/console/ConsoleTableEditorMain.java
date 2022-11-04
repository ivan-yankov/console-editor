package console;

import console.factory.ConsoleTableFactory;
import console.operations.ConsoleOperations;
import console.operations.FileOperations;

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

        ConsoleTableFactory.createConsoleTableEditor(
                csvFile,
                lines,
                columns,
                csvFile.toString(),
                consoleOperations,
                new FileOperations(consoleOperations)
        ).show();

        consoleOperations.writeln();
        System.exit(0);
    }
}
