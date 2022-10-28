package console.util;

import console.ConsoleColor;
import console.model.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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

    public static Command doNothing() {
        return new Command(() -> {
        }, "");
    }

    public static Supplier<String> consoleReadLine() {
        return System.console()::readLine;
    }

    public static <T> List<List<T>> asList(T[][] array) {
        List<List<T>> result = new ArrayList<>();
        for (T[] t : array) {
            result.add(Arrays.asList(t));
        }
        return result;
    }
}
