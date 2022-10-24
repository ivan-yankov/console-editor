package console.editor;

import console.ConsoleColors;
import console.ConsoleReader;
import console.Key;
import console.Keys;
import console.util.TablePrinter;
import console.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ConsoleTableEditor {
    private static final String MODE_COLOR = ConsoleColors.GREEN;
    private static final String USER_INPUT_COLOR = ConsoleColors.MAGENTA;
    private static final String LOG_COLOR = ConsoleColors.CYAN;

    private final Table table;
    private final Focus focus = new Focus(-1, -1);

    private Mode mode = Mode.COMMAND;
    private String userInput = "";
    private String logMessage = "";

    public ConsoleTableEditor(Table table) {
        this.table = table;
    }

    public void edit() {
        if (!focus.isValid() && table.hasData()) {
            focus.setRow(1);
            focus.setCol(0);
        }
        do {
            render();
            executeCommand();
        } while (getMode() != Mode.EXIT);
    }

    protected Table getTable() {
        return table;
    }

    protected Focus getFocus() {
        return focus;
    }

    protected Mode getMode() {
        return mode;
    }

    protected void setMode(Mode mode) {
        this.mode = mode;
    }

    protected String getUserInput() {
        return userInput;
    }

    protected void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    protected String getLogMessage() {
        return logMessage;
    }

    protected void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    protected Map<Mode, Stream<Command>> commands() {
        Stream<Command> commandModeCommands = Stream.of(
                new Command(Keys.TAB, this::onTab, "Next cell"),
                new Command(Keys.LEFT, this::onLeft, "Previous column"),
                new Command(Keys.RIGHT, this::onRight, "Next column"),
                new Command(Keys.UP, this::onUp, "Previous row"),
                new Command(Keys.DOWN, this::onDown, "Next row"),
                new Command(Keys.HOME, this::onHome, "First column"),
                new Command(Keys.END, this::onEnd, "Last column"),
                new Command(Keys.F4, this::onExit, "Close")
        );
        Map<Mode, Stream<Command>> m = new HashMap<>();
        m.put(Mode.COMMAND, commandModeCommands);
        return m;
    }

    protected Command defaultCommand(Key k) {
        return new Command(k, () -> {}, "Do nothing");
    }

    private void onTab() {
        Integer r = focus.getRow();
        Integer c = focus.getCol();
        if (c == table.getColCount() - 1) {
            focus.setCol(0);
            if (r < table.getRowCount() - 1) {
                focus.setRow(r + 1);
            } else {
                focus.setRow(1);
            }
        } else {
            focus.setCol(c + 1);
        }
    }

    private void onLeft() {
        if (focus.getCol() > 0) {
            focus.setCol(focus.getCol() - 1);
        }
    }

    private void onRight() {
        if (focus.getCol() < table.getColCount() - 1) {
            focus.setCol(focus.getCol() + 1);
        }
    }

    private void onUp() {
        if (focus.getRow() > 1) {
            focus.setRow(focus.getRow() - 1);
        }
    }

    private void onDown() {
        if (focus.getRow() < table.getRowCount() - 1) {
            focus.setRow(focus.getRow() + 1);
        }
    }

    private void onHome() {
        focus.setCol(0);
    }

    private void onEnd() {
        focus.setCol(table.getColCount() - 1);
    }

    private void onExit() {
        setMode(Mode.EXIT);
    }

    private void render() {
        clearConsole();
        Utils.writeln(TablePrinter.toConsole(table, focus).orElse("Invalid table"), "");
        Utils.printHelp(commands().get(getMode()));
        printMode();
        printUserInput();
        if (!getLogMessage().isEmpty()) {
            Utils.writeln(getLogMessage(), LOG_COLOR);
            setLogMessage("");
        }
    }

    private void executeCommand() {
        Key k = ConsoleReader.readKey();
        commands()
                .get(getMode())
                .filter(x -> x.getKey().equals(k))
                .findFirst()
                .orElse(defaultCommand(k))
                .getAction()
                .execute();
    }

    private void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            ProcessBuilder pb = os.contains("Windows")
                    ? new ProcessBuilder("cmd", "/c", "cls")
                    : new ProcessBuilder("clear");
            Process p = pb.inheritIO().start();
            p.waitFor();
        } catch (Exception e) {
            System.err.println("Unable to clear the console: " + e.getMessage());
        }
    }

    private void printMode() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        Utils.write(m + ": ", MODE_COLOR);
    }

    private void printUserInput() {
        Utils.write(getUserInput(), USER_INPUT_COLOR);
    }
}
