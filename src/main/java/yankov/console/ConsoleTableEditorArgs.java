package yankov.console;

import yankov.args.annotations.ProgramArgument;

public class ConsoleTableEditorArgs {
    @ProgramArgument(order = 0, defaultValue = "50")
    private String consoleLines;

    @ProgramArgument(order = 1, defaultValue = "100")
    private String consoleColumns;

    @ProgramArgument(order = 2, defaultValue = "unnamed.csv")
    private String fileName;

    public String getConsoleLines() {
        return consoleLines;
    }

    public String getConsoleColumns() {
        return consoleColumns;
    }

    public String getFileName() {
        return fileName;
    }
}
