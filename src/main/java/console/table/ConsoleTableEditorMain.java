package console.table;

import console.operations.ConsoleOperations;
import console.Const;
import console.factory.ConsoleTableFactory;
import console.operations.FileOperations;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleTableEditorMain {
    public static void main(String[] args) {
        ConsoleOperations consoleOperations = new ConsoleOperations();

        if (args.length < 3) {
            consoleOperations.writeError("Missing required argument. Required 3 provided " + args.length);
            consoleOperations.writeln("Program arguments:");
            consoleOperations.writeln(Const.TAB + "number-of-console-lines [required]: Number of lines of the console");
            consoleOperations.writeln(Const.TAB + "number-of-console-columns [required]: Number of columns of the console");
            consoleOperations.writeln(Const.TAB + "input-file [required]: CSV file with at least header line");
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
