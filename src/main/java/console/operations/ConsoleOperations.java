package console.operations;

import console.ConsoleReader;
import console.Key;
import console.RawConsoleInput;
import either.Either;

import java.io.IOException;
import java.util.function.Supplier;

public class ConsoleOperations {
    public void clearConsole() {
        try {
            String os = System.getProperty("os.name");
            ProcessBuilder pb = os.contains("Windows")
                    ? new ProcessBuilder("cmd", "/c", "cls")
                    : new ProcessBuilder("clear");
            Process p = pb.inheritIO().start();
            p.waitFor();
        } catch (Exception e) {
            writeError("Unable to clear the console: " + e.getMessage());
        }
    }

    public void writeln() {
        System.out.println();
    }

    public void writeln(String s) {
        System.out.println(s);
    }

    public void write(String s) {
        System.out.print(s);
    }

    public Supplier<String> consoleReadLine() {
        return System.console()::readLine;
    }

    public void writeError(String s) {
        System.err.println(s);
    }

    public Either<String, Key> readKey() {
        return ConsoleReader.readKey();
    }

    public void resetConsole() {
        try {
            RawConsoleInput.resetConsoleMode();
        } catch (IOException e) {
            // ignored
        }
    }
}
