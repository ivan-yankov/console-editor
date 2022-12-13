package yankov.console;

import yankov.args.ProgramArgumentsParser;
import yankov.console.factory.ConsoleTableFactory;
import yankov.console.operations.ConsoleOperations;
import yankov.console.table.viewer.ConsoleDateSelector;

public class CalendarMain {
    public static void main(String[] args) {
        ConsoleOperations consoleOperations = new ConsoleOperations();

        ConsoleTableEditorArgs appArgs = new ConsoleTableEditorArgs();
        ProgramArgumentsParser.parse(args, appArgs);

        int lines = Integer.parseInt(appArgs.getConsoleLines());
        int columns = Integer.parseInt(appArgs.getConsoleColumns());

        ConsoleDateSelector calendar = ConsoleTableFactory.createConsoleDateSelector(
                Utils.firstDayOfCurrentMonth(),
                lines,
                columns,
                date -> {
                },
                consoleOperations
        );
        calendar.show();

        consoleOperations.clearConsole();

        System.exit(0);
    }
}
