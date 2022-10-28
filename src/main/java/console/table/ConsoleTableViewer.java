package console.table;

import console.*;
import console.util.TablePrinter;
import console.util.Utils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConsoleTableViewer<T> {
    private static final String MODE_COLOR = ConsoleColor.BOLD + ConsoleColor.GREEN;
    private static final String USER_INPUT_COLOR = ConsoleColor.MAGENTA;
    private static final String LOG_COLOR = ConsoleColor.CYAN;
    private static final String HELP_CMD_COLOR = ConsoleColor.ORANGE;
    private static final String HELP_DESC_COLOR = ConsoleColor.DARK_GRAY;

    private final Table<T> table;
    private final Focus focus;
    private final int consoleLines;
    private final int consoleColumns;

    private String title;
    private Mode mode;
    private String userInput;
    private String logMessage;
    private int page;

    public ConsoleTableViewer(Table<T> table, int consoleLines, int consoleColumns) {
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

    protected Command defaultCommand(Key k) {
        return new Command(Mode.SELECT, k, () -> {
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

    protected List<Command> addCommands() {
        return new ArrayList<>();
    }

    private List<Command> commands() {
        List<Command> c = List.of(
                new Command(Mode.SELECT, Keys.TAB, this::onTab, "Next"),
                new Command(Mode.SELECT, Keys.LEFT, this::onLeft, "Prev column"),
                new Command(Mode.SELECT, Keys.RIGHT, this::onRight, "Next column"),
                new Command(Mode.SELECT, Keys.UP, this::onUp, "Prev row"),
                new Command(Mode.SELECT, Keys.DOWN, this::onDown, "Next row"),
                new Command(Mode.SELECT, Keys.HOME, this::onHome, "First column"),
                new Command(Mode.SELECT, Keys.END, this::onEnd, "Last column"),
                new Command(Mode.SELECT, Keys.PAGE_UP, this::prevPage, "Prev page"),
                new Command(Mode.SELECT, Keys.PAGE_DOWN, this::nextPage, "Next page")
        );
        List<Command> allCommands = new ArrayList<>(c);
        allCommands.addAll(addCommands());
        return allCommands;
    }

    private Stream<Command> commandsForMode(Mode mode) {
        return commands().stream().filter(x -> x.getMode().equals(mode));
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

    private void render() {
        clearConsole();
        Utils.writeln(String.join(Const.NEW_LINE, getHeader()));
        Utils.writeln(String.join(Const.NEW_LINE, getPage()));
        Utils.writeln(String.join(Const.NEW_LINE, getFooter()));
        setLogMessage("");
        Utils.write(getModeString());
        Utils.write(Utils.colorText(getUserInput(), USER_INPUT_COLOR));
    }

    private void processCommand() {
        Key k = ConsoleReader.readKey();
        commandsForMode(getMode()).filter(x -> x.getKey().getName().equals(k.getName()))
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

    private List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add(title);
        header.addAll(TablePrinter.headerToConsole(getTable()));
        return header;
    }

    private List<String> getFooter() {
        List<String> footer = new ArrayList<>(getHelp());
        footer.add(Utils.colorText(getLogMessage(), LOG_COLOR));
        return footer;
    }

    private String getModeString() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        return Utils.colorText(m.replace("_", " ") + ": ", MODE_COLOR);
    }

    private List<String> getPage() {
        List<List<String>> pages = Utils.sliding(
                TablePrinter.dataToConsole(table, focus).orElse(List.of("Invalid table")),
                maxTableLinesPerPage()
        );
        if (!pages.isEmpty()) {
            return pages.get(page);
        } else {
            return new ArrayList<>();
        }
    }

    public List<String> getHelp() {
        List<Command> commandList = commandsForMode(getMode()).collect(Collectors.toList());

        int fieldSize = commandList
                .stream()
                .map(x -> Math.max(x.getKey().getName().length(), x.getDescription().length()))
                .max(Comparator.naturalOrder())
                .orElse(15) + 1;

        int helpLength = fieldSize * 2;

        StringBuilder help = new StringBuilder();
        int currentRowLength = 0;
        help.append(Const.NEW_LINE);
        for (Command c : commandList) {
            if (currentRowLength + helpLength > consoleColumns) {
                help.append(Const.NEW_LINE);
                currentRowLength = 0;
            }
            help.append(commandColoredHelp(c, fieldSize));
            currentRowLength += helpLength;
        }
        return List.of(help.toString().split(Const.NEW_LINE));
    }

    private String commandColoredHelp(Command command, int fieldSize) {
        return HELP_CMD_COLOR +
                command.getKey().getName() +
                ConsoleColor.RESET +
                Utils.generateString(fieldSize - command.getKey().getName().length(), ' ') +
                HELP_DESC_COLOR +
                command.getDescription() +
                ConsoleColor.RESET +
                Utils.generateString(fieldSize - command.getDescription().length(), ' ');
    }

    private long numberOfPages() {
        return Utils.numberOfSlides(
                getTable().getDataStream().collect(Collectors.toList()),
                maxTableLinesPerPage()
        );
    }

    private int maxTableLinesPerPage() {
        int headerAndFooterLines = getHeader().size() + getFooter().size();
        return consoleLines - headerAndFooterLines - 1; // for mode line
    }
}
