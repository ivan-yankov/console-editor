package yankov.console;

import yankov.args.ProgramArgumentsParser;
import yankov.console.factory.ConsoleTableFactory;
import yankov.console.operations.ConsoleOperations;

public class CalendarMain {
    public static void main(String[] args) {
        ConsoleOperations consoleOperations = new ConsoleOperations();

        ConsoleTableEditorArgs appArgs = new ConsoleTableEditorArgs();
        ProgramArgumentsParser.parse(args, appArgs);

        int lines = Integer.parseInt(appArgs.getConsoleLines());
        int columns = Integer.parseInt(appArgs.getConsoleColumns());

        ConsoleTableFactory.createConsoleDateSelector(
                Utils.firstDayOfCurrentMonth(),
                lines,
                columns,
                date -> {
                },
                consoleOperations
        ).show();

        consoleOperations.clearConsole();

        System.exit(0);
    }
}
