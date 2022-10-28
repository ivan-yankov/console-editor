package console.table;

import console.*;
import console.model.Command;
import console.model.Pair;
import console.util.TablePrinter;
import console.util.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        return Utils.doNothing();
    }

    protected void resetFocus() {
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    protected void invalidateFocus() {
        getFocus().setRow(-1);
        getFocus().setCol(-1);
    }

    protected List<Pair<CommandKey, Command>> addCommands() {
        return new ArrayList<>();
    }

    private List<Pair<CommandKey, Command>> commands() {
        List<Pair<CommandKey, Command>> c = new ArrayList<>();

        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.TAB), new Command(this::onTab, "Next")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.LEFT), new Command(this::onLeft, "Prev column")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.RIGHT), new Command(this::onRight, "Next column")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.UP), new Command(this::onUp, "Prev row")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.DOWN), new Command(this::onDown, "Next row")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.HOME), new Command(this::onHome, "Firs column")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.END), new Command(this::onEnd, "Last column")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.PAGE_UP), new Command(this::prevPage, "Prev page")));
        c.add(new Pair<>(new CommandKey(Mode.SELECT, Keys.PAGE_DOWN), new Command(this::nextPage, "Nex page")));

        c.addAll(addCommands());

        return c;
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
        commands()
                .stream()
                .filter(x -> x.getKey().getMode().equals(getMode()) && x.getKey().getKey().getName().equals(k.getName()))
                .map(Pair::getValue)
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
            Utils.writeError("Unable to clear the console: " + e.getMessage());
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
        int fieldSize = commands()
                .stream()
                .map(x -> Math.max(x.getKey().getKey().getName().length(), x.getValue().getLabel().length()))
                .max(Comparator.naturalOrder())
                .orElse(15) + 1;

        int helpLength = fieldSize * 2;

        List<Pair<CommandKey, Command>> entries = commands()
                .stream()
                .filter(x -> x.getKey().getMode().equals(getMode()))
                .collect(Collectors.toList());

        StringBuilder help = new StringBuilder();
        int currentRowLength = 0;
        help.append(Const.NEW_LINE);
        for (Pair<CommandKey, Command> entry : entries) {
            if (currentRowLength + helpLength > consoleColumns) {
                help.append(Const.NEW_LINE);
                currentRowLength = 0;
            }
            help.append(commandColoredHelp(entry.getKey(), entry.getValue(), fieldSize));
            currentRowLength += helpLength;
        }
        return List.of(help.toString().split(Const.NEW_LINE));
    }

    private String commandColoredHelp(CommandKey commandKey, Command command, int fieldSize) {
        return HELP_CMD_COLOR +
                commandKey.getKey().getName() +
                ConsoleColor.RESET +
                Utils.generateString(fieldSize - commandKey.getKey().getName().length(), ' ') +
                HELP_DESC_COLOR +
                command.getLabel() +
                ConsoleColor.RESET +
                Utils.generateString(fieldSize - command.getLabel().length(), ' ');
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
