package console.util;

import console.ConsoleColor;
import console.model.Command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static String generateString(int n, Character symbol) {
        return new String(new char[n]).replace('\0', symbol);
    }

    public static boolean containsLetter(String s) {
        return s.chars().anyMatch(Character::isLetter);
    }

    public static String colorText(String s, String color) {
        return color.isEmpty() ? ConsoleColor.RESET + s + ConsoleColor.RESET : color + s + ConsoleColor.RESET;
    }

    public static String printDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public static String printDayFromDate(LocalDate date) {
        return Integer.toString(date.getDayOfMonth());
    }

    public static LocalDate firstDayOfCurrentMonth() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfMonth() - 1);
    }

    public static <T> long numberOfSlides(List<T> items, long step) {
        return Math.round(Math.ceil((double) items.size() / (double) step));
    }

    public static <T> List<List<T>> sliding(List<T> items, long step) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < numberOfSlides(items, step); i++) {
            result.add(items.stream().skip(i * step).limit(step).collect(Collectors.toList()));
        }
        return result;
    }

    public static Command doNothing() {
        return new Command(() -> {
        }, "");
    }

    public static <T> List<List<T>> asList(T[][] array) {
        List<List<T>> result = new ArrayList<>();
        for (T[] t : array) {
            result.add(Arrays.asList(t));
        }
        return result;
    }
}
