package console.util;

import console.ConsoleColors;
import console.Const;
import console.editor.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    private static final String HELP_CMD_COLOR = ConsoleColors.ORANGE;
    private static final String HELP_DESC_COLOR = ConsoleColors.DARK_GRAY;

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

    public static void write(String s, String color) {
        System.out.print(colorText(s, color));
    }

    public static void writeln(String s, String color) {
        System.out.println(colorText(s, color));
    }

    public static void writeln() {
        System.out.println();
    }

    public static void writeln(String s) {
        System.out.println(s);
    }

    private static String colorText(String s, String color) {
        return color.isEmpty() ? ConsoleColors.RESET + s + ConsoleColors.RESET : color + s + ConsoleColors.RESET;
    }

    public static void printHelp(Stream<Command> commands, int consoleColumns) {
        List<Command> commandList = commands.collect(Collectors.toList());

        int fieldSize = commandList
                .stream()
                .map(x -> Math.max(x.getKey().getName().length(), x.getDescription().length()))
                .max(Comparator.naturalOrder())
                .orElse(15) + 1;

        int helpLength = fieldSize * 2;

        StringBuilder help = new StringBuilder();
        int currentRowLength = 0;
        for (Command c : commandList) {
            if (currentRowLength + helpLength > consoleColumns) {
                help.append(Const.NEW_LINE);
                currentRowLength = 0;
            }
            help.append(commandColoredHelp(c, fieldSize));
            currentRowLength += helpLength;
        }

        writeln();
        writeln(help.toString());
        writeln();
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

    public static <T> List<List<T>> sliding(List<T> items, long step) {
        long n = Math.round(Math.ceil((double) items.size() / (double) step));
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(items.stream().skip(i * step).limit(step).collect(Collectors.toList()));
        }
        return result;
    }

    private static String commandColoredHelp(Command command, int fieldSize) {
        return HELP_CMD_COLOR +
                command.getKey().getName() +
                ConsoleColors.RESET +
                Utils.generateString(fieldSize - command.getKey().getName().length(), ' ') +
                HELP_DESC_COLOR +
                command.getDescription() +
                ConsoleColors.RESET +
                Utils.generateString(fieldSize - command.getDescription().length(), ' ');
    }
}
