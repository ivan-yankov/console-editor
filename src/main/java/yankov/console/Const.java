package yankov.console;

import yankov.console.table.viewer.Mode;

import java.time.LocalDate;

public class Const {
    public static final String COL_SEPARATOR = " | ";
    public static final String NEW_LINE = "\n";
    public static final String TAB = "\t";
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String QUOTES = "\"";

    public static final LocalDate INVALID_DATE = LocalDate.MIN;

    public static final Mode DEFAULT_MODE = Mode.KEY;
}
