package console;

import console.model.Command;
import yankov.jutils.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Utils {
    public static String colorText(String s, String color) {
        return color.isEmpty() ? ConsoleColor.RESET + s + ConsoleColor.RESET : color + s + ConsoleColor.RESET;
    }

    public static String colorTextLine(String s, String color, int lineLength) {
        String str = s + StringUtils.fill(lineLength - s.length(), ' ');
        return color.isEmpty() ? ConsoleColor.RESET + str + ConsoleColor.RESET : color + str + ConsoleColor.RESET;
    }

    public static String printDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String printDayFromDate(LocalDate date) {
        if (date.equals(Const.INVALID_DATE)) {
            return "";
        } else {
            return Integer.toString(date.getDayOfMonth());
        }
    }

    public static LocalDate firstDayOfCurrentMonth() {
        return firstDayOfMonth(LocalDate.now());
    }

    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.minusDays(date.getDayOfMonth() - 1);
    }

    public static Command doNothing() {
        return new Command("", x -> {
        }, "");
    }
}
