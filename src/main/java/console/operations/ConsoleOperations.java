package console.operations;

import console.ConsoleReader;
import console.Key;
import either.Either;

import java.util.function.Supplier;

public interface ConsoleOperations {
    void writeln();

    void writeln(String s);

    void write(String s);

    Supplier<String> consoleReadLine();

    void writeError(String s);

    default Either<String, Key> readKey() {
        return ConsoleReader.readKey();
    }
}
