package yankov.console.table.viewer;

import yankov.console.ConsoleColor;
import yankov.console.Const;
import yankov.console.Key;
import yankov.console.Utils;
import yankov.console.model.Command;
import yankov.console.operations.ConsoleOperations;
import yankov.console.table.Table;
import yankov.console.table.TablePrinter;
import yankov.jutils.StringUtils;
import yankov.jutils.functional.Either;
import yankov.jutils.functional.ImmutableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ConsoleTableViewer<T> {
    private static final String TITLE_COLOR = ConsoleColor.BLACK + ConsoleColor.DARK_GRAY_B;
    private static final String HINT_COLOR = ConsoleColor.LIGHT_GREEN + ConsoleColor.DARK_GRAY_B;
    private static final String LOG_COLOR = ConsoleColor.CYAN + ConsoleColor.DARK_GRAY_B;
    private static final String MODE_COLOR = ConsoleColor.GREEN + ConsoleColor.BOLD;

    private final int consoleLines;
    private final int consoleColumns;
    private final ConsoleOperations consoleOperations;

    private Table<T> table;
    private Focus focus;
    private TableViewerSettings settings;

    private String title;
    private Mode mode;
    private String logMessage;
    private int page;

    private final StringBuilder userInput;
    private final TableChangeHandler<T> tableChangeHandler;

    public ConsoleTableViewer(Table<T> table,
                              int consoleLines,
                              int consoleColumns,
                              ConsoleOperations consoleOperations) {
        this.table = table;
        this.focus = new Focus(0, 0);
        this.consoleLines = consoleLines;
        this.consoleColumns = consoleColumns;
        this.consoleOperations = consoleOperations;
        this.title = "";
        this.mode = Mode.COMMAND;
        this.logMessage = "";
        this.page = 0;
        this.settings = new TableViewerSettings(true, true, 2);
        this.userInput = new StringBuilder();
        this.tableChangeHandler = new TableChangeHandler<>();
    }

    public Table<T> getTable() {
        return table;
    }

    public void setTable(Table<T> table, boolean fireChange) {
        Table<T> oldValue = this.table;
        this.table = table;
        if (fireChange) {
            tableChangeHandler.handleTableChange(oldValue);
        }
    }

    public void setTable(Table<T> table) {
        setTable(table, true);
    }

    public ConsoleOperations getConsoleOperations() {
        return consoleOperations;
    }

    public int getConsoleLines() {
        return consoleLines;
    }

    public int getConsoleColumns() {
        return consoleColumns;
    }

    public Focus getFocus() {
        return focus;
    }

    public void setFocus(Focus focus) {
        this.focus = focus;
    }

    public String getTitle() {
        return title;
    }

    public TableViewerSettings getSettings() {
        return settings;
    }

    public void setSettings(TableViewerSettings settings) {
        this.settings = settings;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String getUserInput() {
        return userInput.toString();
    }

    public TableChangeHandler<T> getTableChangeHandler() {
        return tableChangeHandler;
    }

    public void show() {
        do {
            render();
            processCommand();
        } while (getMode() != Mode.EXIT);

        consoleOperations.resetConsole();
    }

    protected final void resetFocus() {
        setFocus(new Focus(0, 0));
    }

    protected final void invalidateFocus() {
        setFocus(new Focus(-1, -1));
    }

    protected final void resetMode() {
        if (getMode() == Mode.COMMAND && userInput.length() == 0) {
            setMode(Mode.EXIT);
        } else {
            setMode(Mode.COMMAND);
        }
    }

    protected final void resetUserInput() {
        userInput.setLength(0);
    }

    protected String getPageUpDescription() {
        return "Prev page";
    }

    protected String getPageDownDescription() {
        return "Nex page";
    }

    protected List<Command> additionalCommands() {
        return new ArrayList<>();
    }

    protected void onPageUp() {
        if (page > 0) {
            page--;
            setFocus(focus.withRow(focus.getRow() - maxTableLinesPerPage()));
        }
    }

    protected void onPageDown() {
        if (page < numberOfPages() - 1) {
            page++;
            int r = focus.getRow() + maxTableLinesPerPage();
            if (r >= getTable().getRowCount()) {
                r = getTable().getRowCount() - 1;
            }
            setFocus(focus.withRow(r));
        }
    }

    protected String getHint() {
        if (getMode() == Mode.HELP) {
            return "Press a key to return.";
        }
        return "";
    }

    private ImmutableList<Command> commands() {
        List<Command> c = new ArrayList<>();

        c.add(new Command("help", x -> showHelp(), "Help", Key.F1));
        c.add(new Command("left", x -> onLeft(), "Prev column", Key.LEFT));
        c.add(new Command("right", x -> onRight(), "Next column", Key.RIGHT));
        c.add(new Command("up", x -> onUp(), "Prev row", Key.UP));
        c.add(new Command("down", x -> onDown(), "Next row", Key.DOWN));
        c.add(new Command("first-col", x -> onHome(), "First column", Key.HOME));
        c.add(new Command("last-col", x -> onEnd(), "Last column", Key.END));
        c.add(new Command("first-row", x -> onCtrlHome(), "First crow", Key.CTRL_HOME));
        c.add(new Command("last-row", x -> onCtrlEnd(), "Last row", Key.CTRL_END));
        c.add(new Command("page-up", x -> onPageUp(), getPageUpDescription(), Key.PAGE_UP));
        c.add(new Command("page-down", x -> onPageDown(), getPageDownDescription(), Key.PAGE_DOWN));
        c.add(new Command("exit", x -> exit(), "Exit"));
        c.add(new Command("tab", x -> onTab(), "Next"));
        c.add(new Command("row-indexes", this::rowIndexes, "Switch row indexes on or off"));

        c.addAll(additionalCommands());

        return ImmutableList.of(c);
    }

    protected void onEnter() {
        ImmutableList<String> cmd = ImmutableList.from(getUserInput().split(" "))
                .stream()
                .filter(x -> !x.isEmpty())
                .toList();

        if (!cmd.isEmpty()) {
            executeCommand(
                    x -> x.getName().equals(cmd.stream().findFirst().orElse("")),
                    cmd.stream().skip(1).toList()
            );
        }

        resetUserInput();
    }

    protected List<String> getFooter() {
        return List.of(
                Utils.colorTextLine(getHint(), HINT_COLOR, consoleColumns),
                Utils.colorTextLine(getLogMessage(), LOG_COLOR, consoleColumns),
                Utils.colorText(getModeString(), MODE_COLOR) + userInput
        );
    }

    protected final Optional<Boolean> analyzeFlagParameter(List<String> p) {
        if (!p.isEmpty()) {
            if (p.get(0).equals("on")) {
                return Optional.of(true);
            } else if (p.get(0).equals("off")) {
                return Optional.of(false);
            }
        }
        return Optional.empty();
    }

    protected String userInputHint(String s) {
        ImmutableList<String> candidates = commands()
                .stream()
                .map(Command::getName)
                .filter(x -> x.startsWith(s))
                .toList();
        if (candidates.isEmpty()) {
            return s;
        }
        if (candidates.size() == 1) {
            return candidates.get(0) + " ";
        }
        StringBuilder commonPart = new StringBuilder();
        int n = candidates.stream().map(String::length).min(Comparator.naturalOrder()).orElse(1);
        for (int i = 0; i < n; i++) {
            int index = i;
            char c = candidates.get(0).charAt(index);
            if (candidates.stream().allMatch(x -> x.charAt(index) == c)) {
                commonPart.append(c);
            }
        }
        return commonPart.toString();
    }

    private void processUserInput() {
        Either<String, Key> input = consoleOperations.readKey();
        if (input.getLeft().isPresent()) {
            userInput.append(input.getLeft().get());
        } else {
            Key key = input.getRight().orElse(Key.UNKNOWN);
            switch (key) {
                case ESC:
                    if (userInput.length() > 0) {
                        resetUserInput();
                    } else {
                        resetMode();
                    }
                    break;
                case ENTER:
                    onEnter();
                    break;
                case BACK_SPACE:
                    if (userInput.length() > 0) {
                        userInput.setLength(userInput.length() - 1);
                    }
                    break;
                case TAB:
                    String ch = userInputHint(getUserInput());
                    resetUserInput();
                    userInput.append(ch);
                default:
                    executeCommand(x -> x.matchKeyBinding(key), List.of());
                    break;
            }
        }
    }

    private void exit() {
        setMode(Mode.EXIT);
    }

    private void showHelp() {
        setMode(Mode.HELP);
    }

    private void rowIndexes(List<String> p) {
        setSettings(settings.withShowRowIndexes(analyzeFlagParameter(p).orElse(settings.isShowRowIndexes())));
    }

    private void onTab() {
        int r = focus.getRow();
        int c = focus.getCol();
        if (c == getTable().getColCount() - 1) {
            setFocus(focus.withCol(0));
            if (r < getTable().getRowCount() - 1) {
                setFocus(focus.withRow(r + 1));
            } else {
                setFocus(focus.withRow(0));
            }
        } else {
            setFocus(focus.withCol(c + 1));
        }
    }

    private void onLeft() {
        if (focus.getCol() > 0) {
            setFocus(focus.withCol(focus.getCol() - 1));
        }
    }

    private void onRight() {
        if (focus.getCol() < getTable().getColCount() - 1) {
            setFocus(focus.withCol(focus.getCol() + 1));
        }
    }

    private void onUp() {
        if (focus.getRow() > 0) {
            setFocus(focus.withRow(focus.getRow() - 1));
        }
    }

    private void onDown() {
        if (focus.getRow() < getTable().getRowCount() - 1) {
            setFocus(focus.withRow(focus.getRow() + 1));
        }
    }

    private void onHome() {
        setFocus(focus.withCol(0));
    }

    private void onEnd() {
        setFocus(focus.withCol(getTable().getColCount() - 1));
    }

    private void onCtrlHome() {
        setFocus(focus.withRow(0));
        page = 0;
    }

    private void onCtrlEnd() {
        setFocus(focus.withRow(getTable().getRowCount() - 1));
        page = numberOfPages() - 1;
    }

    private void render() {
        consoleOperations.clearConsole();
        int verticalMarginSize;
        if (getMode() == Mode.HELP) {
            List<String> h = Help.getHelp(commands());
            verticalMarginSize = consoleLines - getFooter().size() - h.size();
            consoleOperations.writeln(String.join(Const.NEW_LINE, h));
        } else {
            List<String> p = getPage();
            verticalMarginSize = maxTableLinesPerPage() - p.size();
            consoleOperations.writeln(String.join(Const.NEW_LINE, getHeader()));
            if (!p.isEmpty()) {
                consoleOperations.writeln(String.join(Const.NEW_LINE, p));
            }
        }

        List<String> footer = getFooter();
        if (!footer.isEmpty()) {
            consoleOperations.write(String.join(Const.NEW_LINE, getVerticalMargin(verticalMarginSize)));
            consoleOperations.write(String.join(Const.NEW_LINE, footer));
        }

        setLogMessage("");
    }

    private void processCommand() {
        if (getMode() == Mode.HELP) {
            consoleOperations.readKey();
            resetMode();
        } else {
            processUserInput();
        }
    }

    private void executeCommand(Predicate<Command> criteria, List<String> parameters) {
        commands()
                .stream()
                .filter(criteria)
                .findFirst()
                .orElse(Utils.doNothing())
                .getAction()
                .accept(parameters);
    }

    private List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add(Utils.colorTextLine(title, TITLE_COLOR, consoleColumns));
        header.addAll(TablePrinter.headerToConsole(getTable(), settings.isShowRowIndexes()));
        return header;
    }

    private String getModeString() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        return m.replace("_", " ") + ": ";
    }

    private List<String> getPage() {
        ImmutableList<ImmutableList<String>> pages = TablePrinter
                .dataToConsole(getTable(), focus, settings.isShowRowIndexes())
                .sliding(maxTableLinesPerPage());
        if (!pages.isEmpty()) {
            return pages.get(page);
        } else {
            return new ArrayList<>();
        }
    }

    private String getVerticalMargin(int n) {
        return StringUtils.fill(n, '\n');
    }

    private int numberOfPages() {
        return (int) getTable().getData().numberOfSlides(maxTableLinesPerPage());
    }

    private int maxTableLinesPerPage() {
        return consoleLines - getHeader().size() - getFooter().size();
    }
}
