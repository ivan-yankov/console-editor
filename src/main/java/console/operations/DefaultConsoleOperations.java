package console.operations;

import console.ConsoleReader;
import console.Key;
import either.Either;

import java.util.function.Supplier;

public class DefaultConsoleOperations implements ConsoleOperations {
    @Override
    public void writeln() {
        System.out.println();
    }

    @Override
    public void writeln(String s) {
        System.out.println(s);
    }

    @Override
    public void write(String s) {
        System.out.print(s);
    }

    @Override
    public Supplier<String> consoleReadLine() {
        return System.console()::readLine;
    }

    @Override
    public void writeError(String s) {
        System.err.println(s);
    }

    @Override
    public Either<String, Key> readKey() {
        return ConsoleReader.readKey();
    }
}
