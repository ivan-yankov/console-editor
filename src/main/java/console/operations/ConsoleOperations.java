package console.operations;

import console.ConsoleReader;
import console.Key;
import either.Either;

import java.util.function.Supplier;

public class ConsoleOperations {
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
}
