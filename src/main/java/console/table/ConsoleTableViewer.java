package console.table;

import console.*;
import console.model.Command;
import console.model.Pair;
import console.operations.ConsoleOperations;
import console.Utils;
import either.Either;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ConsoleTableViewer<T> {
    private static final String MODE_COLOR = ConsoleColor.BOLD + ConsoleColor.GREEN;
    private static final String HELP_CMD_COLOR = ConsoleColor.ORANGE;
    private static final String HELP_DESC_COLOR = ConsoleColor.DARK_GRAY;

    private final Table<T> table;
    private final Focus focus;
    private final int consoleLines;
    private final int consoleColumns;
    private final ConsoleOperations consoleOperations;

    private String title;
    private Mode mode;
    private String logMessage;
    private int page;
    private boolean showRowIndexes;

    public ConsoleTableViewer(Table<T> table, int consoleLines, int consoleColumns, ConsoleOperations consoleOperations) {
        this.table = table;
        this.focus = new Focus(0, 0);
        this.consoleLines = consoleLines;
        this.consoleColumns = consoleColumns;
        this.consoleOperations = consoleOperations;
        this.title = "";
        this.mode = Mode.SELECT;
        this.logMessage = "";
        this.page = 0;
        this.showRowIndexes = false;
    }

    public ConsoleOperations getConsoleOperations() {
        return consoleOperations;
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

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public void show() {
        do {
            render();
            if (getMode() == Mode.EDIT) {
                readUserInput();
            } else {
                processCommand();
            }
        } while (getMode() != Mode.CLOSE);
    }

    protected void readUserInput() {
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

    protected boolean isShowRowIndexes() {
        return showRowIndexes;
    }

    protected void setShowRowIndexes(boolean showRowIndexes) {
        this.showRowIndexes = showRowIndexes;
    }

    protected void resetFocus() {
        getFocus().setRow(0);
        getFocus().setCol(0);
    }

    protected void invalidateFocus() {
        getFocus().setRow(-1);
        getFocus().setCol(-1);
    }

    protected List<Pair<Key, Command>> addCommands() {
        return new ArrayList<>();
    }

    private List<Pair<Key, Command>> commands() {
        List<Pair<Key, Command>> c = new ArrayList<>();

        c.add(new Pair<>(Key.TAB, new Command(this::onTab, "Next")));
        c.add(new Pair<>(Key.LEFT, new Command(this::onLeft, "Prev column")));
        c.add(new Pair<>(Key.RIGHT, new Command(this::onRight, "Next column")));
        c.add(new Pair<>(Key.UP, new Command(this::onUp, "Prev row")));
        c.add(new Pair<>(Key.DOWN, new Command(this::onDown, "Next row")));
        c.add(new Pair<>(Key.HOME, new Command(this::onHome, "Firs column")));
        c.add(new Pair<>(Key.END, new Command(this::onEnd, "Last column")));
        c.add(new Pair<>(Key.PAGE_UP, new Command(this::prevPage, "Prev page")));
        c.add(new Pair<>(Key.PAGE_DOWN, new Command(this::nextPage, "Nex page")));

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
        consoleOperations.writeln(String.join(Const.NEW_LINE, getHeader()));
        consoleOperations.writeln(String.join(Const.NEW_LINE, getPage()));
        consoleOperations.writeln(String.join(Const.NEW_LINE, getFooter()));
        setLogMessage("");
        consoleOperations.write(getModeString());
    }

    private void processCommand() {
        Either<String, Key> input = ConsoleReader.readKey();
        String inputKeyName = input.getRight().isPresent() ? input.getRight().get().getName() : "";
        commands()
                .stream()
                .filter(x -> x.getKey().getName().equals(inputKeyName))
                .map(Pair::getValue)
                .findFirst()
                .orElse(Utils.doNothing())
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
            consoleOperations.writeError("Unable to clear the console: " + e.getMessage());
        }
    }

    private List<String> getHeader() {
        List<String> header = new ArrayList<>();
        header.add(title);
        header.addAll(TablePrinter.headerToConsole(getTable(), showRowIndexes));
        return header;
    }

    private List<String> getFooter() {
        List<String> footer = new ArrayList<>();
        if (getMode() == Mode.EDIT) {
            footer.add("");
            footer.add(Utils.colorText("Enter empty input to escape edit", ConsoleColor.DARK_GRAY));
        } else {
            footer.addAll(getCommandsHelp());
        }
        footer.add(getLogMessage());
        return footer;
    }

    private String getModeString() {
        String m = getMode().toString().substring(0, 1).toUpperCase() + getMode().toString().substring(1).toLowerCase();
        return Utils.colorText(m.replace("_", " ") + ": ", MODE_COLOR);
    }

    private List<String> getPage() {
        List<List<String>> pages = Utils.sliding(
                TablePrinter.dataToConsole(table, focus, showRowIndexes).orElse(List.of("Invalid table")),
                maxTableLinesPerPage()
        );
        if (!pages.isEmpty()) {
            return pages.get(page);
        } else {
            return new ArrayList<>();
        }
    }

    private List<String> getCommandsHelp() {
        int fieldSize = commands()
                .stream()
                .map(x -> Math.max(x.getKey().getName().length(), x.getValue().getLabel().length()))
                .max(Comparator.naturalOrder())
                .orElse(15) + 1;

        int helpLength = fieldSize * 2;

        StringBuilder help = new StringBuilder();
        int currentRowLength = 0;
        help.append(Const.NEW_LINE);
        for (Pair<Key, Command> entry : commands()) {
            if (currentRowLength + helpLength > consoleColumns) {
                help.append(Const.NEW_LINE);
                currentRowLength = 0;
            }
            help.append(commandColoredHelp(entry.getKey(), entry.getValue(), fieldSize));
            currentRowLength += helpLength;
        }
        return List.of(help.toString().split(Const.NEW_LINE));
    }

    private String commandColoredHelp(Key key, Command command, int fieldSize) {
        return HELP_CMD_COLOR +
                key.getName() +
                ConsoleColor.RESET +
                Utils.generateString(fieldSize - key.getName().length(), ' ') +
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
