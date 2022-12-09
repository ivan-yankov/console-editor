package yankov.console;

import yankov.args.ProgramArgumentsParser;
import yankov.console.factory.ConsoleTableFactory;
import yankov.console.operations.ConsoleOperations;
import yankov.console.operations.FileOperations;
import yankov.console.table.viewer.ConsoleTableEditor;
import yankov.console.table.viewer.ConsoleTableViewer;
import yankov.jutils.functional.Either;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ConsoleTableEditorMain {
    public static void main(String[] args) {
        ConsoleOperations consoleOperations = new ConsoleOperations();

        ConsoleTableEditorArgs appArgs = new ConsoleTableEditorArgs();
        ProgramArgumentsParser.parse(args, appArgs);

        int lines = Integer.parseInt(appArgs.getConsoleLines());
        int columns = Integer.parseInt(appArgs.getConsoleColumns());
        Path csvFile = Paths.get(appArgs.getFileName());

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
