package console.operations;

import java.util.function.Supplier;

public interface ConsoleOperations {
    void writeln();

    void writeln(String s);

    void write(String s);

    Supplier<String> consoleReadLine();

    void writeError(String s);
}
