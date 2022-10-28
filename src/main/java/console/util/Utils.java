package console.util;

import console.ConsoleColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Utils {
    public static Optional<String> readFile(Path file) {
        try {
            return Optional.of(Files.readString(file));
        } catch (IOException e) {
            writeError("Unable to read file: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void writeFile(Path file, String contents) {
        try {
            Files.writeString(file, contents);
        } catch (IOException e) {
            writeError("Unable to write file: " + e.getMessage());
        }
    }

    public static void writeError(String s) {
        System.err.println(s);
    }

    public static String generateString(int n, Character symbol) {
        return new String(new char[n]).replace('\0', symbol);
    }

    public static boolean containsLetter(String s) {
        return s.chars().anyMatch(Character::isLetter);
    }

    public static void writeln() {
        System.out.println();
    }

    public static void writeln(String s) {
        System.out.println(s);
    }

    public static void write(String s) {
        System.out.print(s);
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

    public static List<Character> chars(String s) {
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            result.add(s.charAt(i));
        }
        return result;
    }

    public static String buildString(List<Character> chars) {
        StringBuilder result = new StringBuilder();
        for (Character c : chars) {
            result.append(c);
        }
        return result.toString();
    }
}
