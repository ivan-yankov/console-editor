package console.editor;

import console.*;
import console.util.TablePrinter;
import console.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsoleTable<T> {
    private static final String MODE_COLOR = ConsoleColors.BOLD + ConsoleColors.GREEN;
    private static final String USER_INPUT_COLOR = ConsoleColors.MAGENTA;
    private static final String LOG_COLOR = ConsoleColors.CYAN;

    private final Table<T> table;
    private final Focus focus;
    private final int consoleLines;
    private final int consoleColumns;

    private String title;
    private Mode mode;
    private String userInput;
    private String logMessage;
    private int page;

    public ConsoleTable(Table<T> table, int consoleLines, int consoleColumns) {
        this.table = table;
        this.focus = new Focus(0, 0);
        this.consoleLines = consoleLines;
        this.consoleColumns = consoleColumns;
        this.title = "";
        this.mode = Mode.SELECT;
        this.userInput = "";
        this.logMessage = "";
        this.page = 0;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getConsoleLines() {
        return consoleLines;
    }

    public int getConsoleColumns() {
        return consoleColumns;
    }

    public void show() {
        do {
            render();
            processCommand();
        } while (getMode() != Mode.CLOSE);
    }

    protected Table<T> getTable() {
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

    protected Map<Mode, Stream<Command>> additionalCommands() {
        return new HashMap<>();
    }

    protected Command defaultCommand(Key k) {
        return new Command(k, () -> {
        }, "Do nothing");
    }

    protected void resetFocus() {
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    protected void invalidateFocus() {
        getFocus().setRow(-1);
        getFocus().setCol(-1);
    }

    private Map<Mode, Stream<Command>> commands() {
        Stream<Command> base = Stream.of(
                new Command(Keys.TAB, this::onTab, "Next"),
                new Command(Keys.LEFT, this::onLeft, "Prev column"),
                new Command(Keys.RIGHT, this::onRight, "Next column"),
                new Command(Keys.UP, this::onUp, "Prev row"),
                new Command(Keys.DOWN, this::onDown, "Next row"),
                new Command(Keys.HOME, this::onHome, "First column"),
                new Command(Keys.END, this::onEnd, "Last column"),
                new Command(Keys.PAGE_UP, this::prevPage, "Prev page"),
                new Command(Keys.PAGE_DOWN, this::nextPage, "Next page")
        );

        Map<Mode, Stream<Command>> additional = additionalCommands();

        Map<Mode, Stream<Command>> result = new HashMap<>();
        result.put(Mode.SELECT, base);
        for (Mode m : additional.keySet()) {
            result.put(m, Stream.concat(result.getOrDefault(m, Stream.empty()), additional.getOrDefault(m, Stream.empty())));
        }

        return result;
    }

    private void onTab() {
        int r = focus.getRow();
        int c = focus.getCol();
        if (c == table.getColCount() - 1) {
            focus.setCol(0);
            if (r < table.getRowCount() - 1) {
                focus.setRow(r + 1);
            } else {
                focus.setRow(0);
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
        if (focus.getRow() > 0) {
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

    private void render() {
        clearConsole();
        printTitle();
        printHeader();
        printPage();
        Utils.writeln(getHelp());
        printLog();
        printMode();
        printUserInput();
    }

    private void processCommand() {
        Key k = ConsoleReader.readKey();
        commands()
                .get(getMode())
                .filter(x -> x.getKey().getName().equals(k.getName()))
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

    private void printTitle() {
        if (!title.isEmpty()) {
            Utils.writeln(title);
        }
    }

    private void printHeader() {
        String header = String.join(Const.NEW_LINE, getHeader());
        Utils.writeln(header);
    }

    private void printPage() {
        List<List<String>> pages = Utils.sliding(TablePrinter.dataToConsole(table, focus), maxTableLinesPerPage());
        if (pages.isEmpty()) {
            Utils.writeln("Empty or invalid table");
        } else {
            Utils.writeln(String.join(Const.NEW_LINE, pages.get(page)));
        }
    }

    private List<String> getHeader() {
        return TablePrinter.headerToConsole(getTable());
    }

    private String getHelp() {
        return Utils.printHelp(commands().get(getMode()), consoleColumns);
    }

    private void printLog() {
        if (!getLogMessage().isEmpty()) {
            Utils.writeln("Information: " + getLogMessage(), LOG_COLOR);
            setLogMessage("");
        }
    }

    private void printMode() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        Utils.write(m.replace("_", " ") + ": ", MODE_COLOR);
    }

    private void printUserInput() {
        Utils.write(getUserInput(), USER_INPUT_COLOR);
    }

    private void prevPage() {
        if (page > 0) {
            page--;
            focus.setRow(focus.getRow() - maxTableLinesPerPage());
        }
    }

    private void nextPage() {
        if (page < numberOfPages() - 1) {
            page++;
            int r = focus.getRow() + maxTableLinesPerPage();
            if (r >= getTable().getRowCount()) {
                r = getTable().getRowCount() - 1;
            }
            focus.setRow(r);
        }
    }

    private long numberOfPages() {
        return Utils.numberOfSlides(getTable().getDataStream().collect(Collectors.toList()), maxTableLinesPerPage());
    }

    private int maxTableLinesPerPage() {
        int helpLines = (int) getHelp().chars().filter(x -> x == '\n').count();
        int headerLines = getHeader().size();
        int additionalLines = 2; // for title and mode
        return consoleLines - helpLines - headerLines - additionalLines;
    }
}
