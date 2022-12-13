package yankov.console.table.viewer;

import yankov.console.ConsoleColor;
import yankov.console.Const;
import yankov.console.Key;
import yankov.console.Utils;
import yankov.console.model.Command;
import yankov.console.operations.ConsoleOperations;
import yankov.console.table.Table;
import yankov.console.table.TablePrinter;
import yankov.jutils.functional.ImmutableList;
import yankov.jutils.functional.RangeInt;

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
    private final TableChangeHandler<T> tableChangeHandler;
    private final UserInputProcessor userInputProcessor;

    private Table<T> table;
    private Focus focus;
    private TableViewerSettings settings;
    private int columnPage;

    private String title;
    private Mode mode;
    private String logMessage;

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
        this.settings = new TableViewerSettings(true, true, 2);
        this.columnPage = 0;
        this.tableChangeHandler = new TableChangeHandler<>();
        this.userInputProcessor = new UserInputProcessor(
                consoleOperations,
                this::inputHint,
                this::processInput,
                key -> executeCommand(x -> x.matchKeyBinding(key), List.of()),
                this::onEnter,
                this::resetMode
        );
    }

    public Table<T> getTable() {
        return table;
    }

    public void setTable(Table<T> table, boolean fireChange) {
        Table<T> oldValue = this.table;
        this.table = table;
        columnPage = 0;

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
            userInputProcessor.processUserInput();
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
        if (getMode() == Mode.COMMAND && userInputProcessor.getUserInput().isEmpty()) {
            setMode(Mode.EXIT);
        } else {
            setMode(Mode.COMMAND);
        }
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
        int r = focus.getRow() - getLinesPerPage(getHeader().size(), getFooter().size());
        if (r < 0) {
            r = 0;
        }
        setFocus(focus.withRow(r));
    }

    protected void onPageDown() {
        int r = focus.getRow() + getLinesPerPage(getHeader().size(), getFooter().size());
        if (r >= getTable().getRowCount()) {
            r = getTable().getRowCount() - 1;
        }
        setFocus(focus.withRow(r));
    }

    protected String getHint() {
        return getMode() == Mode.HELP ? "Esc to return." : "";
    }

    protected boolean showFooter() {
        return true;
    }

    protected void onEnter() {
    }

    protected void processInput(String input) {
        switch (getMode()) {
            case COMMAND:
                ImmutableList<String> cmd = ImmutableList.from(input.split(" "))
                        .stream()
                        .filter(x -> !x.isEmpty())
                        .toList();
                if (!cmd.isEmpty()) {
                    executeCommand(
                            x -> x.getName().equals(cmd.stream().findFirst().orElse("")),
                            cmd.stream().skip(1).toList()
                    );
                }
                break;
            case HELP:
                consoleOperations.readKey();
                resetMode();
                break;
            default:
                break;
        }
    }

    protected String inputHint(String s) {
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
        c.add(new Command("page-right", x -> pageRight(), "Page right", Key.ALT_RIGHT));
        c.add(new Command("page-left", x -> pageLeft(), "Page left", Key.ALT_LEFT));
        c.add(new Command("exit", x -> exit(), "Exit"));
        c.add(new Command("tab", x -> onTab(), "Next"));
        c.add(new Command("row-indexes", this::rowIndexes, "Switch row indexes on or off"));

        c.addAll(additionalCommands());

        return ImmutableList.of(c);
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
    }

    private void onCtrlEnd() {
        setFocus(focus.withRow(getTable().getRowCount() - 1));
    }

    private void render() {
        consoleOperations.clearConsole();
        if (getMode() == Mode.HELP) {
            List<String> help = alignPage(Help.getHelp(commands()), 0, getFooter().size());
            consoleOperations.writeln(String.join(Const.NEW_LINE, help));
        } else {
            List<String> p = getPage();
            consoleOperations.writeln(String.join(Const.NEW_LINE, getHeader()));
            if (!p.isEmpty()) {
                consoleOperations.writeln(String.join(Const.NEW_LINE, p));
            }
        }

        List<String> footer = getFooter();
        if (!footer.isEmpty()) {
            consoleOperations.write(String.join(Const.NEW_LINE, footer));
        }

        setLogMessage("");
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

    private List<String> getFooter() {
        if (getMode() == Mode.HELP || showFooter()) {
            return List.of(
                    Utils.colorTextLine(getHint(), HINT_COLOR, consoleColumns),
                    Utils.colorTextLine(getLogMessage(), LOG_COLOR, consoleColumns),
                    Utils.colorText(getModeString(), MODE_COLOR) + userInputProcessor.getUserInput() + cursor()
            );
        } else {
            return List.of();
        }
    }

    private ImmutableList<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add(Utils.colorTextLine(title, TITLE_COLOR, consoleColumns));
        header.addAll(TablePrinter.headerToConsole(table, getColumnPage(), consoleColumns, settings.isShowRowIndexes()));
        return ImmutableList.of(header);
    }

    private String getModeString() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        return m.replace("_", " ") + ": ";
    }

    private ImmutableList<String> getPage() {
        ImmutableList<ImmutableList<Integer>> pageIndexes = ImmutableList.tabulate(table.getRowCount(), x -> x)
                .sliding(getLinesPerPage(getHeader().size(), getFooter().size()));

        if (!pageIndexes.isEmpty()) {
            int pageIndex = (int) Math.round(Math.floor((double) focus.getRow() / (double) getLinesPerPage(getHeader().size(), getFooter().size())));
            ImmutableList<Integer> p = pageIndexes.get(pageIndex);
            RangeInt visibleRows = new RangeInt(p.get(0), p.get(p.size() - 1) + 1);
            ImmutableList<String> page = TablePrinter
                    .dataToConsole(table, focus, visibleRows, getColumnPage(), consoleColumns, settings.isShowRowIndexes());
            return alignPage(page, getHeader().size(), getFooter().size());
        } else {
            return alignPage(ImmutableList.from(), getHeader().size(), getFooter().size());
        }
    }

    private int getLinesPerPage(int headerSize, int footerSize) {
        return consoleLines - headerSize - footerSize;
    }

    private ImmutableList<String> alignPage(ImmutableList<String> page, int headerSize, int footerSize) {
        int n = getLinesPerPage(headerSize, footerSize) - page.size();
        ImmutableList<String> additional = ImmutableList.fill(n, "");
        return page.appendAll(additional);
    }

    private String cursor() {
        return ConsoleColor.WHITE_B + " " + ConsoleColor.RESET;
    }

    private ImmutableList<RangeInt> columnPages() {
        List<RangeInt> ranges = new ArrayList<>();
        int start = 0;
        int end = 0;
        do {
            int length = 0;
            for (int i = start; i < table.getColCount(); i++) {
                length += table.fieldSize(i) + Const.COL_SEPARATOR.length();
                if (length < consoleColumns) {
                    end++;
                }
            }
            if (end == start) {
                end++;
            }
            ranges.add(new RangeInt(start, end));
            start = end;
        } while(end < table.getColCount());
        return ImmutableList.of(ranges);
    }

    private RangeInt getColumnPage() {
        ImmutableList<RangeInt> pages = columnPages();
        if (columnPage < pages.size()) {
            return pages.get(columnPage);

        } else {
            return new RangeInt(0, 0);
        }
    }

    private void pageRight() {
        if (getColumnPage().getEnd() < table.getColCount()) {
            columnPage++;
            setFocus(new Focus(focus.getRow(), getColumnPage().getStart()));
        }
    }

    private void pageLeft() {
        if (columnPage > 0) {
            columnPage--;
            setFocus(new Focus(focus.getRow(), getColumnPage().getStart()));
        }
    }
}
