package console;

import console.model.Command;
import console.model.Pair;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static String colorTextLine(String s, String color, int lineLength) {
        String str = s + generateString(lineLength - s.length(), ' ');
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
        return new Command("", () -> {
        }, "");
    }

    public static <T> List<List<T>> asList(T[][] array) {
        List<List<T>> result = new ArrayList<>();
        for (T[] t : array) {
            result.add(Arrays.asList(t));
        }
        return result;
    }

    public static <T> List<List<T>> asMutableList2d(List<List<T>> list) {
        List<List<T>> result = new ArrayList<>();
        for (List<T> t : list) {
            result.add(asMutableList(t));
        }
        return result;
    }

    public static <T> List<T> asMutableList(List<T> list) {
        return new ArrayList<>(list);
    }

    public static <T> Stream<Pair<T, Integer>> zipWithIndex(Stream<T> stream) {
        List<Pair<T, Integer>> result = new ArrayList<>();
        List<T> items = stream.collect(Collectors.toList());
        for (int i = 0; i < items.size(); i++) {
            result.add(new Pair<>(items.get(i), i));
        }
        return result.stream();
    }

    public static String wrongNumberOfColumnsMessage(int row, int expected, int actual) {
        return "Line " + row + " of the csv file contains wrong number of columns. Expected " + expected + " actual " + actual;
    }
}
