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
    private static final String HELP_CMD_COLOR = ConsoleColor.LIGHT_YELLOW;
    private static final String HELP_KEY_BINDING_COLOR = ConsoleColor.YELLOW;
    private static final String HELP_DESC_COLOR = ConsoleColor.DARK_GRAY;

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
        this.mode = Mode.KEY;
        this.logMessage = "";
        this.page = 0;
        this.settings = new TableViewerSettings(true, true, 2);
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

    public TableChangeHandler<T> getTableChangeHandler() {
        return tableChangeHandler;
    }

    public void show() {
        do {
            render();
            if (allowCommand()) {
                processCommand();
            } else {
                processCustomAction();
            }
        } while (getMode() != Mode.EXIT);

        consoleOperations.resetConsole();
    }

    protected boolean allowCommandMode() {
        return true;
    }

    protected boolean allowCommand() {
        return true;
    }

    protected void processCustomAction() {
    }

    protected String getPageUpDescription() {
        return "Prev page";
    }

    protected String getPageDownDescription() {
        return "Nex page";
    }

    protected String getEnterDescription() {
        return "";
    }

    protected final void resetFocus() {
        setFocus(new Focus(0, 0));
    }

    protected final void invalidateFocus() {
        setFocus(new Focus(-1, -1));
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
        if (getMode() == Mode.COMMAND) {
            return "Empty command to return to key mode.";
        } else if (getMode() == Mode.HELP) {
            return "Press a key to return.";
        }
        return "";
    }

    private List<String> getHelp() {
        int nameFieldSize = commands()
                .stream()
                .map(x -> x.getName().length())
                .max(Comparator.naturalOrder())
                .orElse(15) + 1;
        int keyBindingFieldSize = commands()
                .stream()
                .map(x -> x.getKeyBindingName().length())
                .max(Comparator.naturalOrder())
                .orElse(8) + 1;
        return commands()
                .stream()
                .filter(x -> !x.getDescription().isEmpty())
                .map(x -> commandColoredHelp(x, nameFieldSize, keyBindingFieldSize))
                .toList();
    }

    private ImmutableList<Command> commands() {
        List<Command> c = new ArrayList<>();

        if (allowCommandMode()) {
            c.add(new Command("", x -> commandMode(), "Command mode", Key.F5));
        }

        c.add(new Command("enter", x -> onEnter(), getEnterDescription(), Key.ENTER));
        c.add(new Command("exit", x -> exit(), "Exit", Key.ESC));
        c.add(new Command("help", x -> helpMode(), "Help", Key.F1));
        c.add(new Command("tab", x -> onTab(), "Next", Key.TAB));
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
        c.add(new Command("row-indexes", this::rowIndexes, "Switch row indexes on or off"));

        c.addAll(additionalCommands());

        return ImmutableList.of(c);
    }

    protected void onEnter() {
    }

    protected List<String> getFooter() {
        return List.of(
                Utils.colorTextLine(getHint(), HINT_COLOR, consoleColumns),
                Utils.colorTextLine(getLogMessage(), LOG_COLOR, consoleColumns),
                Utils.colorText(getModeString(), MODE_COLOR)
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

    private void exit() {
        setMode(Mode.EXIT);
    }

    private void commandMode() {
        setMode(Mode.COMMAND);
    }

    private void helpMode() {
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
            List<String> help = getHelp();
            verticalMarginSize = consoleLines - getFooter().size() - help.size();
            consoleOperations.writeln(String.join(Const.NEW_LINE, help));
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
        switch (getMode()) {
            case KEY:
                Either<String, Key> input = consoleOperations.readKey();
                String inputKeyName = input.getRight().isPresent() ? input.getRight().get().getName() : "";
                executeCommand(x -> x.hasKeyBinding() && x.getKeyBindingName().equals(inputKeyName), List.of());
                break;
            case COMMAND:
                consoleOperations.resetConsole();
                ImmutableList<String> cmd = ImmutableList.from(consoleOperations.consoleReadLine().get().split(" "))
                        .stream()
                        .filter(x -> !x.isEmpty())
                        .toList();
                if (cmd.isEmpty()) {
                    setMode(Mode.KEY);
                } else {
                    executeCommand(
                            x -> x.getName().equals(cmd.stream().findFirst().orElse("")),
                            cmd.stream().skip(1).toList()
                    );
                }
                break;
            case HELP:
                consoleOperations.readKey();
                setMode(Mode.KEY);
                break;
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

    private String commandColoredHelp(Command command, int nameFieldSize, int keyBindingFieldSize) {
        return HELP_CMD_COLOR +
                command.getName() +
                ConsoleColor.RESET +
                StringUtils.fill(nameFieldSize - command.getName().length(), ' ') +
                HELP_KEY_BINDING_COLOR +
                command.getKeyBindingName() +
                ConsoleColor.RESET +
                StringUtils.fill(keyBindingFieldSize - command.getKeyBindingName().length(), ' ') +
                HELP_DESC_COLOR +
                command.getDescription() +
                ConsoleColor.RESET;
    }

    private int numberOfPages() {
        return (int) getTable().getData().numberOfSlides(maxTableLinesPerPage());
    }

    private int maxTableLinesPerPage() {
        return consoleLines - getHeader().size() - getFooter().size();
    }
}
