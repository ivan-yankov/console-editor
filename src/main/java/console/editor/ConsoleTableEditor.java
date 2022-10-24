package console.editor;

import console.*;
import console.util.TablePrinter;
import console.util.Utils;

public class ConsoleTableEditor {
    private static final String MODE_COLOR = ConsoleColors.GREEN;
    private static final String USER_INPUT_COLOR = ConsoleColors.MAGENTA;
    private static final String LOG_COLOR = ConsoleColors.CYAN;

    private Focus focus = new Focus(-1, -1);
    private Mode mode = Mode.COMMAND;
    private String userInput = "";
    private String logMessage = "";

    public void edit(Table table) {
        if (!focus.isValid() && table.hasData()) focus = new Focus(1, 0);
        do {
            render(table);
            action(table);
        } while (mode != Mode.EXIT);
    }

    private void onTab(Integer tableRows, Integer tableCols) {
        Integer r = focus.getRow();
        Integer c = focus.getCol();
        if (c == tableCols - 1) {
            focus.setCol(0);
            if (r < tableRows - 1) {
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

    private void onRight(Integer tableCols) {
        if (focus.getCol() < tableCols - 1) {
            focus.setCol(focus.getCol() + 1);
        }
    }

    private void onUp() {
        if (focus.getRow() > 1) {
            focus.setRow(focus.getRow() - 1);
        }
    }

    private void onDown(Integer tableRows) {
        if (focus.getRow() < tableRows - 1) {
            focus.setRow(focus.getRow() + 1);
        }
    }

    private void onHome() {
        focus.setCol(0);
    }

    private void onEnd(Integer tableCols) {
        focus.setCol(tableCols - 1);
    }

    private void render(Table table) {
        clearConsole();
        Utils.writeln(TablePrinter.toConsole(table, focus).orElse("Invalid table"), "");
        switch (mode) {
            case COMMAND:
                Utils.printHelp(HelpItems.commands);
                break;
            case EDIT:
                Utils.printHelp(HelpItems.edit);
        }
        printMode();
        printUserInput();
        if (!logMessage.isEmpty()) {
            Utils.writeln(logMessage, LOG_COLOR);
            logMessage = "";
        }
    }

    private void action(Table table) {
        Key k = ConsoleReader.readKey();
        switch (mode) {
            case COMMAND:
                if (Keys.F4.equals(k)) {
                    mode = Mode.EXIT;
                } else if (Keys.TAB.equals(k)) {
                    onTab(table.getRowCount(), table.getColCount());
                } else if (Keys.LEFT.equals(k)) {
                    onLeft();
                } else if (Keys.RIGHT.equals(k)) {
                    onRight(table.getColCount());
                } else if (Keys.UP.equals(k)) {
                    onUp();
                } else if (Keys.DOWN.equals(k)) {
                    onDown(table.getRowCount());
                } else if (Keys.HOME.equals(k)) {
                    onHome();
                } else if (Keys.END.equals(k)) {
                    onEnd(table.getColCount());
                } else if (Keys.F2.equals(k)) {
                    mode = Mode.EDIT;
                }
                break;
            case EDIT:
                if (Keys.ESC.equals(k)) {
                    mode = Mode.COMMAND;
                    userInput = "";
                } else if (Keys.ENTER.equals(k)) {
                    table.setCellValue(userInput, focus.getRow(), focus.getCol());
                    mode = Mode.COMMAND;
                    userInput = "";
                } else {
                    if (!Keys.asList().contains(k)) {
                        userInput = userInput + k.getName();
                    }
                }
                break;
        }
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
        String m = mode.toString().substring(0, 1).toUpperCase() + mode.toString().substring(1).toLowerCase();
        Utils.write(m + ": ", MODE_COLOR);
    }

    private void printUserInput() {
        Utils.write(userInput, USER_INPUT_COLOR);
    }
}
