package console.util;

import console.ConsoleColors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class Utils {
    private static final String HELP_COLOR = ConsoleColors.YELLOW;

    public static Optional<String> readFile(Path file) {
        try {
            return Optional.of(Files.readString(file));
        } catch (IOException e) {
            logError("Unable to read file: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static void writeFile(Path file, String contents) {
        try {
            Files.writeString(file, contents);
        } catch (IOException e) {
            logError("Unable to write file: " + e.getMessage());
        }
    }

    public static void logError(String msg) {
        System.err.println(msg);
    }

    public static String generateString(Integer n, Character symbol) {
        return new String(new char[n]).replace('\0', symbol);
    }

    public static boolean containsLetter(String s) {
        return s.chars().anyMatch(Character::isLetter);
    }

    public static void write(String s, String color) {
        System.out.print(colorText(s, color));
    }

    public static void writeln(String s, String color) {
        System.out.println(colorText(s, color));
    }

    private static String colorText(String s, String color) {
        return color.isEmpty() ? ConsoleColors.RESET + s + ConsoleColors.RESET : color + s + ConsoleColors.RESET;
    }

    public static void printHelp(List<String> items) {
        String cmd = "Help: " + String.join("; ", items);
        String sep = Utils.generateString(cmd.length(), '-');
        writeln(sep, HELP_COLOR);
        writeln(cmd, HELP_COLOR);
        writeln(sep, HELP_COLOR);
    }
}
