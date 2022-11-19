package console.table;

import console.*;
import console.model.Command;
import console.operations.ConsoleOperations;
import either.Either;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ConsoleTableViewer<T> {
    private static final String TITLE_COLOR = ConsoleColor.BLACK + ConsoleColor.DARK_GRAY_B;
    private static final String HINT_COLOR = ConsoleColor.LIGHT_GREEN + ConsoleColor.DARK_GRAY_B;
    private static final String LOG_COLOR = ConsoleColor.CYAN + ConsoleColor.DARK_GRAY_B;
    private static final String MODE_COLOR = ConsoleColor.GREEN + ConsoleColor.BOLD;
    private static final String HELP_CMD_COLOR = ConsoleColor.LIGHT_YELLOW;
    private static final String HELP_KEY_BINDING_COLOR = ConsoleColor.YELLOW;
    private static final String HELP_DESC_COLOR = ConsoleColor.DARK_GRAY;

    private final Table<T> table;
    private final Focus focus;
    private final int consoleLines;
    private final int consoleColumns;
    private final ConsoleOperations consoleOperations;
    private final TableViewerSettings settings;

    private String title;
    private Mode mode;
    private String logMessage;
    private int page;

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
        this.settings = new TableViewerSettings(true, true);
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

    public Table<T> getTable() {
        return table;
    }

    public Focus getFocus() {
        return focus;
    }

    public String getTitle() {
        return title;
    }

    public TableViewerSettings getSettings() {
        return settings;
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

    protected boolean renderFooter() {
        return true;
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
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    protected final void invalidateFocus() {
        getFocus().setRow(-1);
        getFocus().setCol(-1);
    }

    protected List<Command> additionalCommands() {
        return new ArrayList<>();
    }

    protected void onPageUp() {
        if (page > 0) {
            page--;
            focus.setRow(focus.getRow() - maxTableLinesPerPage());
        }
    }

    protected void onPageDown() {
        if (page < numberOfPages() - 1) {
            page++;
            int r = focus.getRow() + maxTableLinesPerPage();
            if (r >= getTable().getRowCount()) {
                r = getTable().getRowCount() - 1;
            }
            focus.setRow(r);
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
                .collect(Collectors.toList());
    }

    private List<Command> commands() {
        List<Command> c = new ArrayList<>();

        if (allowCommandMode()) {
            c.add(new Command("", this::commandMode, "Command mode", Key.F5));
        }

        c.add(new Command("enter", this::onEnter, getEnterDescription(), Key.ENTER));
        c.add(new Command("exit", this::exit, "Exit", Key.ESC));
        c.add(new Command("help", this::helpMode, "Help", Key.F1));
        c.add(new Command("tab", this::onTab, "Next", Key.TAB));
        c.add(new Command("left", this::onLeft, "Prev column", Key.LEFT));
        c.add(new Command("right", this::onRight, "Next column", Key.RIGHT));
        c.add(new Command("up", this::onUp, "Prev row", Key.UP));
        c.add(new Command("down", this::onDown, "Next row", Key.DOWN));
        c.add(new Command("home", this::onHome, "First column", Key.HOME));
        c.add(new Command("end", this::onEnd, "Last column", Key.END));
        c.add(new Command("page-up", this::onPageUp, getPageUpDescription(), Key.PAGE_UP));
        c.add(new Command("page-down", this::onPageDown, getPageDownDescription(), Key.PAGE_DOWN));
        c.add(new Command("indexes", this::toggleRowIndexes, "Row indexes"));

        c.addAll(additionalCommands());

        return c;
    }

    protected void onEnter() {
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

    private void toggleRowIndexes() {
        settings.setShowRowIndexes(!settings.isShowRowIndexes());
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
        consoleOperations.clearConsole();
        if (getMode() == Mode.HELP) {
            List<String> help = getHelp();
            int verticalMarginSize = consoleLines - getFooter().size() - help.size();
            consoleOperations.writeln(String.join(Const.NEW_LINE, help));
            consoleOperations.write(String.join(Const.NEW_LINE, getVerticalMargin(verticalMarginSize)));
        } else {
            List<String> p = getPage();
            int verticalMarginSize = maxTableLinesPerPage() - p.size();
            consoleOperations.writeln(String.join(Const.NEW_LINE, getHeader()));
            if (!p.isEmpty()) {
                consoleOperations.writeln(String.join(Const.NEW_LINE, p));
            }
            consoleOperations.write(String.join(Const.NEW_LINE, getVerticalMargin(verticalMarginSize)));
        }
        if (renderFooter()) {
            consoleOperations.write(String.join(Const.NEW_LINE, getFooter()));
        }
        setLogMessage("");
    }

    private void processCommand() {
        switch (getMode()) {
            case KEY:
                Either<String, Key> input = consoleOperations.readKey();
                String inputKeyName = input.getRight().isPresent() ? input.getRight().get().getName() : "";
                executeCommand(x -> x.hasKeyBinding() && x.getKeyBindingName().equals(inputKeyName));
                break;
            case COMMAND:
                consoleOperations.resetConsole();
                String cmd = consoleOperations.consoleReadLine().get();
                if (cmd.isEmpty()) {
                    setMode(Mode.KEY);
                } else {
                    executeCommand(x -> x.getName().equals(cmd));
                }
                break;
            case HELP:
                consoleOperations.readKey();
                setMode(Mode.KEY);
                break;
        }
    }

    private void executeCommand(Predicate<Command> criteria) {
        commands()
                .stream()
                .filter(criteria)
                .findFirst()
                .orElse(Utils.doNothing())
                .getAction()
                .execute();
    }

    private List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add(Utils.colorTextLine(title, TITLE_COLOR, consoleColumns));
        header.addAll(TablePrinter.headerToConsole(getTable(), settings.isShowRowIndexes()));
        return header;
    }

    private List<String> getFooter() {
        return List.of(
                Utils.colorTextLine(getHint(), HINT_COLOR, consoleColumns),
                Utils.colorTextLine(getLogMessage(), LOG_COLOR, consoleColumns),
                Utils.colorText(getModeString(), MODE_COLOR)
        );
    }

    private String getModeString() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        return m.replace("_", " ") + ": ";
    }

    private List<String> getPage() {
        List<List<String>> pages = Utils.sliding(
                TablePrinter.dataToConsole(table, focus, settings.isShowRowIndexes()).orElse(table.getErrors()),
                maxTableLinesPerPage()
        );
        if (!pages.isEmpty()) {
            return pages.get(page);
        } else {
            return new ArrayList<>();
        }
    }

    private String getVerticalMargin(int n) {
        return Utils.generateString(n, '\n');
    }

    private String commandColoredHelp(Command command, int nameFieldSize, int keyBindingFieldSize) {
        return HELP_CMD_COLOR +
                command.getName() +
                ConsoleColor.RESET +
                Utils.generateString(nameFieldSize - command.getName().length(), ' ') +
                HELP_KEY_BINDING_COLOR +
                command.getKeyBindingName() +
                ConsoleColor.RESET +
                Utils.generateString(keyBindingFieldSize - command.getKeyBindingName().length(), ' ') +
                HELP_DESC_COLOR +
                command.getDescription() +
                ConsoleColor.RESET;
    }

    private long numberOfPages() {
        return Utils.numberOfSlides(
                getTable().getData(),
                maxTableLinesPerPage()
        );
    }

    private int maxTableLinesPerPage() {
        if (renderFooter()) {
            return consoleLines - getHeader().size() - getFooter().size();
        } else {
            return consoleLines - getHeader().size();
        }
    }
}
