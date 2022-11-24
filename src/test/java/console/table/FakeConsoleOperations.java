package console.table;

import console.Const;
import console.Key;
import console.operations.ConsoleOperations;
import yankov.functional.Either;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class FakeConsoleOperations extends ConsoleOperations {
    private String output;
    private final Stack<String> commands;

    public FakeConsoleOperations() {
        this.output = "";
        this.commands = new Stack<>();
    }

    public String getOutput() {
        return output;
    }

    public void setCommands(List<String> commands) {
        this.commands.clear();
        for (int i = commands.size() - 1; i >= 0; i--) {
            this.commands.push(commands.get(i));
        }
    }

    public boolean allExecuted() {
        return commands.isEmpty();
    }

    @Override
    public void clearConsole() {
        output = "";
    }

    @Override
    public void writeln(String s) {
        output += s + Const.NEW_LINE;
    }

    @Override
    public void write(String s) {
        output += s;
    }

    @Override
    public Supplier<String> consoleReadLine() {
        return commands::pop;
    }

    @Override
    public void writeError(String s) {
    }

    @Override
    public Either<String, Key> readKey() {
        return Either.left("Unexpected call");
    }
}
